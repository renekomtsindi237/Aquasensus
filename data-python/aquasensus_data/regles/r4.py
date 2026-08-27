from aquasensus_data.indicateurs import jours_avant_saison_seche
from aquasensus_data.parametrage import Parametrage
from aquasensus_data.profil import ProfilOuvrage
from aquasensus_data.regles.base import AlerteProposee, Facteur, Regle, estimation_retenue


class R4PressionSaisonniere(Regle):
    code = "R4_PRESSION_SAISONNIERE"
    niveau = "MODERE"

    def s_applique(self, profil: ProfilOuvrage, params: Parametrage, autres: set[str]) -> bool:
        if estimation_retenue(profil):
            return False
        delai = jours_avant_saison_seche(profil.aujourd_hui, profil.saisons)
        if delai is None or delai <= 0 or delai > 30:
            return False
        m_ok = profil.m is not None and profil.m >= params.seuil_r4_m
        p_ok = profil.p >= params.seuil_r4_p
        return m_ok or p_ok

    def expliquer(self, profil: ProfilOuvrage, params: Parametrage) -> AlerteProposee:
        delai = jours_avant_saison_seche(profil.aujourd_hui, profil.saisons) or 0
        semaines = max(1, round(delai / 7))
        return AlerteProposee(
            self.code,
            self.niveau,
            f"La saison sèche commence dans {semaines} semaine(s) et la fréquentation va augmenter : inspection recommandée avant le pic.",
            "Inspecter l'ouvrage avant le pic de saison sèche.",
            [
                Facteur("M", "Charge de maintenance", profil.m or 0, params.seuil_r4_m, 0.5),
                Facteur("P", "Pannes pondérées", profil.p, params.seuil_r4_p, 0.5),
            ],
        )
