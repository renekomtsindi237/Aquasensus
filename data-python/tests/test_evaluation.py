from aquasensus_data.evaluation import metriques
from aquasensus_data.evaluation.issues import evaluer_alerte
from datetime import date, timedelta


def test_rg06_issues():
    d0 = date(2026, 1, 1)
    assert evaluer_alerte(d0, 14, d0 + timedelta(days=10), None) == "PANNE_SURVENUE"
    assert evaluer_alerte(d0, 14, None, d0 + timedelta(days=5)) == "PANNE_EVITEE"
    assert evaluer_alerte(d0, 14, None, None, aujourdhui=d0 + timedelta(days=20)) == "INDETERMINEE"


def test_evaluation_graine_fixe():
    m = metriques()
    assert m["taux_anticipation"] >= 0.5
    assert "PANNE_SURVENUE" in m["issues"]
    assert "PANNE_EVITEE" in m["issues"]
