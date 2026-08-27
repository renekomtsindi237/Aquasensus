from datetime import date, timedelta


def evaluer_alerte(
    alerte_le: date,
    horizon: int,
    panne_le: date | None,
    preventive_le: date | None,
    aujourdhui: date | None = None,
) -> str:
    """RG-06 : anticipation si panne ou préventive dans l'horizon."""
    fin = alerte_le + timedelta(days=horizon)
    if panne_le and alerte_le < panne_le <= fin:
        return "PANNE_SURVENUE"
    if preventive_le and alerte_le < preventive_le <= fin:
        return "PANNE_EVITEE"
    jour = aujourdhui or date.today()
    if jour > fin:
        return "INDETERMINEE"
    return "INDETERMINEE"
