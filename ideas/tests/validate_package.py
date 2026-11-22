#!/usr/bin/env python3
"""Static check only; not a JHipster importer or Angular build."""
from pathlib import Path
import json, csv, re, sys
ROOT=Path(__file__).resolve().parents[1]
errors=[]
texts=[]

jdl=(ROOT/'specs/domain.v4.draft.jdl').read_text(encoding='utf-8')
ents=re.findall(r'^entity\s+(\w+)\s*\{', jdl,re.M)
rels=re.findall(r'^\s+\w+\{[^\n]*\}\s+to\s+\w+',jdl,re.M)
if len(ents)!=47: errors.append(f'JDL entidades esperadas 47, recibidas {len(ents)}')
if len(rels)!=114: errors.append(f'JDL relaciones esperadas 114, recibidas {len(rels)}')
if len(set(ents))!=len(ents): errors.append('Entidades JDL repetidas')
for rr in rels:
    source=rr.strip().split('{')[0]; target=rr.split(' to ')[1].split()[0]
    if source not in ents: errors.append('Source no definido: '+source)
    if target not in ents and target!='User': errors.append('Target no definido: '+target)

with (ROOT/'frontend-demo/entity-to-ui.csv').open(encoding='utf-8',newline='') as f: rows=list(csv.DictReader(f))
names=[r['entity'] for r in rows]
if len(rows)!=47 or set(names)!=set(ents): errors.append('Matriz CSV no coincide exactamente con las 47 entidades')
for r in rows:
    if not r['route'] or not r['feature'] or not r['treatment']: errors.append('Fila de mapeo incompleta: '+r['entity'])

for fp in ROOT.rglob('*.json'):
    try: json.loads(fp.read_text(encoding='utf-8'))
    except Exception as e: errors.append(f'JSON inválido {fp.relative_to(ROOT)}: {e}')

try:
    import jsonschema
    from jsonschema import FormatChecker
    pairs=[('telemetry-event','telemetry-event'),('telemetry-integrity-event','telemetry-integrity-event'),('drone-mission','drone-mission')]
    for schema,ex in pairs:
        sch=json.loads((ROOT/f'contracts/{schema}.schema.json').read_text())
        val=json.loads((ROOT/f'contracts/{ex}.example.json').read_text())
        jsonschema.Draft202012Validator.check_schema(sch)
        jsonschema.validate(val,sch,format_checker=FormatChecker())
except ImportError:
    print('AVISO: jsonschema ausente, ejemplos no se validaron contra esquemas')
except Exception as e: errors.append(f'Schemas: {e}')

try:
    import yaml
    for fp in ROOT.rglob('*.yaml'):
        yaml.safe_load(fp.read_text(encoding='utf-8'))
except ImportError:
    print('AVISO: PyYAML ausente, YAML no analizado')
except Exception as e: errors.append(f'YAML: {e}')

critical=['README.md','docs/00_ARQUITECTURA_MAESTRA_SDD.md','docs/03_SPRINTS_FRONTEND_DEMO_F0_F7.md', 'docs/06_SEGURIDAD_TELEMETRIA_ATAQUES.md','prompts/PROMPT_MAESTRO_IA_FRONTEND_ANGULAR_MOCK.md']
for c in critical:
    if not (ROOT/c).exists(): errors.append('Falta archivo crítico '+c)
for fp in ROOT.rglob('*'):
    if not fp.is_file():continue
    if fp.suffix in {'.md','.csv','.yaml','.json','.jdl','.css','.sql'} and 'SolarCare' in fp.read_text(encoding='utf-8'):
        # Only ADR migration note allowed
        if fp.name!='12_ADR.md': errors.append('Marca anterior en '+str(fp.relative_to(ROOT)))

print(f'Entidades JDL: {len(ents)} / Relaciones declaradas: {len(rels)} / Mapa UI: {len(rows)} / JSON: {len(list(ROOT.rglob("*.json")))} / YAML: {len(list(ROOT.rglob("*.yaml")))}')
if errors:
    for e in errors: print('ERROR:',e)
    sys.exit(1)
print('OK: validación estática de estructura, referencias y esquemas de ejemplo. NO prueba JHipster CLI ni apps reales.')
