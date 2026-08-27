from datetime import date, timedelta

from aquasensus_data.evaluation.issues import evaluer_alerte


def jeu_graine_fixe() -> list[dict]:
    """ISS-065 / ISS-042 : historique simulé reproductible (graine 42 implicite)."""
    jour0 = date(2026, 1, 1)
    return [
        {
            "alerte_le": jour0,
            "horizon": 14,
            "panne_le": jour0 + timedelta(days=10),
            "preventive_le": None,
        },
        {
            "alerte_le": jour0 + timedelta(days=40),
            "horizon": 14,
            "panne_le": None,
            "preventive_le": jour0 + timedelta(days=45),
        },
        {
            "alerte_le": jour0 + timedelta(days=80),
            "horizon": 14,
            "panne_le": None,
            "preventive_le": None,
            "aujourdhui": jour0 + timedelta(days=100),
        },
        {
            "alerte_le": jour0 + timedelta(days=120),
            "horizon": 14,
            "panne_le": jour0 + timedelta(days=150),
            "preventive_le": None,
        },
    ]


def metriques(cas: list[dict] | None = None) -> dict:
    cas = cas or jeu_graine_fixe()
    issues = [evaluer_alerte(**c) for c in cas]
    pannes = [c for c in cas if c.get("panne_le")]
    anticipees = sum(1 for c in cas if c.get("panne_le") and evaluer_alerte(**c) == "PANNE_SURVENUE")
    fausses = sum(1 for i in issues if i == "INDETERMINEE")
    return {
        "issues": issues,
        "taux_anticipation": anticipees / len(pannes) if pannes else 0.0,
        "taux_fausses_alertes": fausses / len(issues) if issues else 0.0,
    }
