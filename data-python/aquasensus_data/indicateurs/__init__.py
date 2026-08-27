from datetime import date, timedelta

from aquasensus_data.parametrage import Parametrage
from aquasensus_data.profil import PeriodeSaison, ProfilOuvrage


def coefficient(jour_annee: int, localite_id: str | None, periodes: list[PeriodeSaison], defaut: float) -> float:
    if localite_id:
        for p in periodes:
            if p.localite_id == localite_id and p.couvre(jour_annee):
                return p.coefficient
    for p in periodes:
        if p.localite_id is None and p.couvre(jour_annee):
            return p.coefficient
    return defaut


def intervalle_effectif(population: int | None, constructeur: int | None, p: Parametrage) -> int:
    if constructeur and constructeur > 0:
        return constructeur
    pop = p.population_reference if not population else population
    brut = p.intervalle_base * (p.population_reference / pop)
    return int(round(min(p.intervalle_max, max(p.intervalle_min, brut))))


def calculer_charge(profil: ProfilOuvrage, params: Parametrage) -> ProfilOuvrage:
    profil.calendrier_absent = len(profil.saisons) == 0
    ref = profil.derniere_preventive or profil.date_mise_en_service
    profil.intervalle_effectif = intervalle_effectif(
        profil.population_desservie, profil.intervalle_constructeur, params
    )
    if ref is None or ref > profil.aujourd_hui:
        profil.charge_cumulee = None
        profil.m = None
        return profil
    charge = 0.0
    secs = 0
    jour = ref + timedelta(days=1)
    while jour <= profil.aujourd_hui:
        k = coefficient(jour.timetuple().tm_yday, profil.localite_id, profil.saisons, params.coefficient_defaut)
        charge += k
        if k > 1.0:
            secs += 1
        jour += timedelta(days=1)
    profil.charge_cumulee = charge
    profil.jours_saison_seche = secs
    profil.m = min(1.5, charge / profil.intervalle_effectif) if profil.intervalle_effectif else None
    return profil


def jours_avant_saison_seche(aujourdhui: date, periodes: list[PeriodeSaison]) -> int | None:
    seches = [p for p in periodes if p.coefficient > 1.0]
    if not seches:
        return None
    jour = aujourdhui.timetuple().tm_yday
    for p in seches:
        if p.couvre(jour):
            return 0
    for delta in range(1, 367):
        candidat = aujourdhui + timedelta(days=delta)
        j = candidat.timetuple().tm_yday
        if any(p.couvre(j) for p in seches):
            return delta
    return None
