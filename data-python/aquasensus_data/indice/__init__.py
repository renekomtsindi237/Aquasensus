from aquasensus_data.parametrage import Parametrage
from aquasensus_data.profil import ProfilOuvrage


def bande(score: float) -> str:
    if score >= 80:
        return "OPERATIONNEL"
    if score >= 60:
        return "SOUS_SURVEILLANCE"
    if score >= 40:
        return "RISQUE_ELEVE"
    return "CRITIQUE"


def calculer_score(profil: ProfilOuvrage, params: Parametrage) -> float:
    m = 0.0 if profil.m is None else min(profil.m, 1.0)
    score = 100 - 100 * (params.w_s * profil.s + params.w_p * min(profil.p / 3.0, 1.0) + params.w_m * m)
    score = max(0.0, min(100.0, score))
    profil.score = score
    profil.bande = bande(score)
    return score
