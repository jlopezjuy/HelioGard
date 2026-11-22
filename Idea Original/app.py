"""HelioGuard - backend (Flask).
Uso:  pip install flask  ->  python app.py  ->  abrir http://localhost:5000
Simula la telemetría de una instalación solar y detecta datos sospechosos.
"""
import math
import random
from collections import deque

from flask import Flask, jsonify, request, send_from_directory

app = Flask(__name__)

PICO_KW = 5.0      # potencia pico de la instalación simulada (kW)
TARIFA = 120.0     # $ por kWh (valor de ejemplo, ajustalo)
PASO_H = 5 / 60    # cada lectura avanza 5 minutos simulados
MODOS = ("normal", "suciedad", "replay", "inyeccion")

E = {}  # estado global de la simulación (demo de un solo usuario)


def reiniciar():
    E.update(hora=7.0, modo="normal", suciedad=1.0, congelado=None,
             perdida_kwh=0.0, hist=deque(maxlen=12))


reiniciar()


def irradiancia(h):
    """Irradiancia en W/m2: campana solar entre las 6:30 y las 18:30."""
    return max(0.0, math.sin(math.pi * (h - 6.5) / 12)) * 1000


def lectura():
    """Genera una lectura de sensores según el modo activo."""
    h, modo = E["hora"], E["modo"]
    irr = irradiancia(h)
    temp = 20 + irr * 0.025 + random.uniform(-0.4, 0.4)
    esperada = PICO_KW * irr / 1000 * (1 - 0.004 * (temp - 25))

    if modo == "suciedad":      # el panel se ensucia de a poco
        E["suciedad"] = max(0.55, E["suciedad"] - 0.012)
    elif modo == "normal":      # se "limpia" el panel
        E["suciedad"] = min(1.0, E["suciedad"] + 0.05)

    pot = esperada * E["suciedad"] * random.uniform(0.985, 1.015)
    v = 380 + random.uniform(-3, 3)
    r = dict(potencia=pot, tension=v, corriente=pot * 1000 / v,
             irradiancia=irr * random.uniform(0.98, 1.02), temperatura=temp)

    if modo == "replay":        # el atacante repite una lectura vieja
        if E["congelado"] is None:
            E["congelado"] = dict(r)
        r = dict(E["congelado"])
    else:
        E["congelado"] = None
        if modo == "inyeccion": # dato falso: potencia imposible
            r["potencia"] = esperada * 1.7 + 0.8
    r["esperada"] = esperada
    return r


def analizar(r):
    """Aplica los tres detectores. Devuelve lista de alertas."""
    hist = E["hist"]
    hist.append(r)
    alertas = []

    # 1) Dato imposible: potencia != tensión x corriente, o más de lo posible
    pvi = r["tension"] * r["corriente"] / 1000
    if (abs(r["potencia"] - pvi) > 0.08 * max(pvi, 0.1)
            or r["potencia"] > 1.25 * r["esperada"] + 0.3):
        alertas.append(dict(nivel="rojo", titulo="Dato imposible",
            texto="La potencia informada no coincide con tensión × corriente "
                  "o supera lo físicamente posible. Sensor roto o dato inyectado."))

    # 2) Replay: lecturas idénticas (en la realidad siempre hay ruido)
    ult = list(hist)[-5:]
    if len(ult) == 5 and len({(x["potencia"], x["tension"]) for x in ult}) == 1:
        alertas.append(dict(nivel="rojo", titulo="Posible replay",
            texto="Las últimas 5 lecturas son idénticas: los datos están "
                  "congelados o repetidos."))

    # 3) Rendimiento bajo: producción real vs esperada
    util = [x for x in hist if x["esperada"] > 0.5]
    if len(util) >= 6 and r["esperada"] > 0.5:
        ratio = sum(x["potencia"] for x in util) / sum(x["esperada"] for x in util)
        if ratio < 0.93:
            alertas.append(dict(nivel="amarillo", titulo="Rendimiento bajo",
                texto=f"Produce {ratio * 100:.0f}% de lo esperado. "
                      "Probable suciedad: programar limpieza."))
    return alertas


@app.get("/api/telemetria")
def telemetria():
    r = lectura()
    alertas = analizar(r)
    niveles = [a["nivel"] for a in alertas]
    estado = "rojo" if "rojo" in niveles else "amarillo" if niveles else "verde"

    # La pérdida solo se cuenta cuando el dato es confiable
    if estado != "rojo" and r["potencia"] < 0.97 * r["esperada"]:
        E["perdida_kwh"] += (r["esperada"] - r["potencia"]) * PASO_H

    hora = E["hora"]
    E["hora"] += PASO_H
    if E["hora"] >= 19:
        E["hora"] = 7.0  # nuevo día simulado

    return jsonify(hora=hora, modo=E["modo"], estado=estado, alertas=alertas,
                   lectura={k: round(v, 3) for k, v in r.items()},
                   perdida_kwh=round(E["perdida_kwh"], 2),
                   perdida_pesos=round(E["perdida_kwh"] * TARIFA))


@app.post("/api/modo")
def cambiar_modo():
    modo = (request.get_json(silent=True) or {}).get("modo")
    if modo not in MODOS:
        return jsonify(error="Modo inválido", validos=MODOS), 400
    E["modo"] = modo
    return jsonify(modo=modo)


@app.post("/api/reiniciar")
def api_reiniciar():
    reiniciar()
    return jsonify(ok=True)


@app.get("/")
def index():
    return send_from_directory(app.root_path, "index.html")


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=False)
