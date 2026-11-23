# Guion de 2 minutos

Datos simulados. El botón **Restablecer demo** vuelve a la semilla `helioguard-hackaton-v4`.

| Momento | Control | Qué se ve |
|---|---|---|
| 0–12 s | Inicio, planta en régimen | Puna Norte comparable. Déficit chico. Fuente meteorológica simulada. |
| 12–27 s | Escenario (polvo, inversor, nube, helada, granizo, humo) o Sector sucio | Bloque B2 con trama y rótulo. En polvo: 4,25 kWh y 697 ARS. Ningún escenario atribuye el 100 % a una sola causa. |
| 27–45 s | Crear misión → Revisar seguridad → Aprobar → Despachar → Animar dron → Verificar | La misión no sale de DRAFT sin checklist. La aprobación dice que no es un permiso real. En vuelo, el mapa muestra «Dron simulado» sobre el parque. |
| 45–60 s | Evento original → Replay exacto | La segunda lectura queda retenida. Inyección falsa abre investigación y no afirma un ataque. |

Si el viento está marcado, la autorización y el despacho se bloquean. Cortar red encola en la Raspberry simulada; Restaurar red sincroniza una vez.

Atajo de sesión: el selector «Usuario demo». Técnico Demo no entra a Economía ni a Planes. Administradora Salta no ve Puna Norte.
