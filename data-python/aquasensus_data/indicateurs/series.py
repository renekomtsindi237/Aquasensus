from datetime import timedelta

from aquasensus_data.parametrage import Parametrage
from aquasensus_data.profil import ProfilOuvrage


def pression_pannes(profil: ProfilOuvrage, params: Parametrage) -> float:
    total = 0.0
    for panne in profil.pannes:
        age = (profil.aujourd_hui - panne).days
        if age < 0 or age > params.fenetre_pannes:
            continue
        total += 0.5 ** (age / params.demi_vie_pannes)
    profil.p = total
    return total


def signaux_et_tendance(profil: ProfilOuvrage, params: Parametrage) -> tuple[float, float]:
    debut = profil.aujourd_hui - timedelta(days=params.fenetre_signaux - 1)
    recents = [d for d in profil.signaux_faibles if debut <= d <= profil.aujourd_hui]
    brut = float(len(recents))
    if profil.population_desservie is None:
        profil.s = 0.0
    else:
        profil.s = min(1.0, brut / 5.0)

    fenetres = []
    for i in range(3):
        fin = profil.aujourd_hui - timedelta(days=7 * (2 - i))
        start = fin - timedelta(days=6)
        fenetres.append(sum(1 for d in profil.signaux_faibles if start <= d <= fin))
    # pente de régression simple sur x = 0,1,2
    n = 3
    xs = [0.0, 1.0, 2.0]
    mean_x = 1.0
    mean_y = sum(fenetres) / n
    num = sum((x - mean_x) * (y - mean_y) for x, y in zip(xs, fenetres))
    den = sum((x - mean_x) ** 2 for x in xs)
    profil.t = 0.0 if den == 0 else num / den
    return profil.s, profil.t


def confiance(profil: ProfilOuvrage) -> str:
    pop = profil.population_desservie is not None
    hist = profil.jours_historique
    preventive = profil.derniere_preventive is not None
    ref = preventive or profil.date_mise_en_service is not None
    if hist < 90 or not pop or not ref:
        niveau = "FAIBLE"
    elif hist >= 180 and pop and preventive:
        niveau = "HAUTE"
    else:
        niveau = "MOYENNE"
    profil.confiance = niveau
    return niveau
