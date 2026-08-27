# Service data (Python) — interne uniquement (ISS-009, DA-01).

```powershell
cd data-python
python -m venv .venv
.\.venv\Scripts\pip install -r requirements.txt
$env:PYTHONPATH = (Get-Location).Path
.\.venv\Scripts\pytest -q
.\.venv\Scripts\uvicorn app.main:app --port 8090
```

Variables : `AQS_CORE_URL` (ex. `http://core:8080`), `AQS_INTERNAL_SECRET`.

Nginx ne route pas ce service. Si Python est arrêté, signalements et interventions restent disponibles (ENF-13) ; les derniers indices restent affichés.
