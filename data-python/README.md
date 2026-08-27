# Service data (Python) — ingénierie data, interne uniquement (ISS-009, DA-01, DA-11)

Ce répertoire est le **pilier data engineer** d’AquaSensus, au même titre que `backend-java` est le pilier génie logiciel. Il n’est pas un notebook collé au backend.

| Étape | Rôle |
| --- | --- |
| Extraction | API interne Java, incrémentale, aucun volume d’eau |
| Préparation | Déduplication, calendrier, confiance |
| Indicateurs / indice / règles | `M`, `P`, `S`, `T`, R1–R5, texte explicable |
| Publication | Retour vers le cœur Java |
| Évaluation | Anticipation, faux positifs (qualité du moteur) |

Nginx ne route pas ce service. Si Python est arrêté, signalements et interventions restent disponibles (ENF-13) ; les derniers indices restent affichés.

```powershell
cd data-python
python -m venv .venv
.\.venv\Scripts\pip install -r requirements.txt
$env:PYTHONPATH = (Get-Location).Path
.\.venv\Scripts\pytest -q
.\.venv\Scripts\uvicorn app.main:app --port 8090
```

Variables : `AQS_CORE_URL` (ex. `http://core:8080`), `AQS_INTERNAL_SECRET`.
