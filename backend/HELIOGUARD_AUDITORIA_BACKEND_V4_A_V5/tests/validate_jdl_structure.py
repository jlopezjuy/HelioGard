#!/usr/bin/env python3
"""Static reference validator. NOT a replacement for JHipster JDL parser or Liquibase."""
from pathlib import Path
import re, json, sys
root=Path(__file__).resolve().parents[1]
fn=root/'specs/domain.v5.review.jdl'
s=fn.read_text()
s=re.sub(r'(?m)^\s*//.*$', '', s)
entities={m.group(1):m.group(2) for m in re.finditer(r'\bentity\s+([A-Z][A-Za-z0-9]*)\s*\{(.*?)\}',s,re.S)}
enums={m.group(1):[x.strip() for x in m.group(2).split(',') if x.strip()] for m in re.finditer(r'\benum\s+([A-Z][A-Za-z0-9]*)\s*\{(.*?)\}',s,re.S)}
blocks=re.findall(r'\brelationship\s+ManyToOne\s*\{(.*?)\n\}\s*\n\s*dto\s',s,re.S)
assert len(blocks)==1, f'Expected 1 relationship block, got {len(blocks)}'
rels=[]
for l in blocks[0].splitlines():
    l=l.strip()
    if not l or l.startswith('//'):continue
    m=re.fullmatch(r'([A-Z]\w*)\{([a-z]\w*)(?:\((\w+)\))?(?:\s+required)?\}\s+to\s+([A-Z]\w*)(?:\s+with\s+builtInEntity)?',l)
    if not m: raise AssertionError(f'Cannot parse relationship line: {l}')
    rels.append(dict(source=m.group(1),field=m.group(2),dest=m.group(4),display=m.group(3),jdl=l))
errs=[]
types={'String','Boolean','Integer','Long','Float','Double','BigDecimal','Instant','LocalDate','ZonedDateTime','Duration','UUID','TextBlob','ImageBlob','AnyBlob','LocalTime'}
fields={}
for e,body in entities.items():
    fields[e]=set()
    for l in body.splitlines():
        l=l.strip()
        if not l: continue
        parts=l.split()
        if len(parts)<2: errs.append(f'Invalid field {e}: {l}'); continue
        name,typ=parts[:2]
        if name in fields[e]: errs.append(f'Duplicate field {e}.{name}')
        fields[e].add(name)
        if typ not in types and typ not in enums:errs.append(f'Unknown field type {e}.{name}:{typ}')
    if not fields[e]: errs.append(f'Empty entity: {e}')
source_rel_fields=set()
for r in rels:
    if r['source'] not in entities:errs.append(f'Unknown source: {r}')
    if r['dest'] not in entities and r['dest']!='User':errs.append(f'Unknown dest: {r}')
    if (r['source'],r['field']) in source_rel_fields:errs.append(f'Duplicate relation field: {r["source"]}.{r["field"]}')
    source_rel_fields.add((r['source'],r['field']))
    if r['source'] in fields and r['field'] in fields[r['source']]:errs.append(f'Field/relation collision {r["source"]}.{r["field"]}')
    if r['dest'] in fields and r['display'] and r['display'] not in fields[r['dest']] and r['display']!='id':errs.append(f'Wrong display field {r["dest"]}.{r["display"]}')
    if r['dest']=='User' and 'with builtInEntity' not in r['jdl']:errs.append('User relation missing builtInEntity')
base=(root/'specs/domain.v4.original.jdl').read_text()
old=set(re.findall(r'\bentity\s+([A-Z]\w*)\s*\{',base))
new=set(entities)
if old-new:errs.append(f'Missing original v4 entities: {old-new}')
expected_count=len(new)
# basic check braces count despite comments removed (safe for provided source)
if s.count('{')!=s.count('}'):errs.append('Unbalanced braces')
# Validate unique entity/enum declarations (regex matching exact count)
if len(re.findall(r'\bentity\s+[A-Z]\w*\s*\{',s))!=len(entities):errs.append('Duplicate entity declarations')
if len(re.findall(r'\benum\s+[A-Z]\w*\s*\{',s))!=len(enums):errs.append('Duplicate enum declarations')
result={'file':str(fn),'base_entities':len(old),'entities':len(new),'new_entities':len(new-old),'relations':len(rels),'enum_count':len(enums),'errors':errs,'added_entities':sorted(new-old),'validation':'STRUCTURAL_ONLY'}
(root/'docs/VALIDACION_ESTRUCTURAL.json').write_text(json.dumps(result,ensure_ascii=False,indent=2))
print(json.dumps(result,ensure_ascii=False,indent=2))
sys.exit(bool(errs))
