from datetime import timedelta

from aquasensus_data.parametrage import Parametrage
from aquasensus_data.profil import ProfilOuvrage
from aquasensus_data.regles.base import AlerteProposee, Facteur, Regle


class R3FragiliteChronique(Regle):
    code = "R3_FRAGILITE_CHRONIQUE"
    niveau = "MODERE"

    def s_applique(self, profil: ProfilOuvrage, params: Parametrage, autres: set[str]) -> bool:
        debut = profil.aujourd_hui - timedelta(days=90)
        n = sum(1 for d in profil.pannes if debut <= d <= profil.aujourd_hui)
        return n >= 2

    def expliquer(self, profil: ProfilOuvrage, params: Parametrage) -> AlerteProposee:
        return AlerteProposee(
            self.code,
            self.niveau,
            "Deuxième panne en 3 mois : diagnostic de fond recommandé.",
            "Organiser un diagnostic de fond (pièces d'usure, étanchéité).",
            [Facteur("P", "Pannes sur 90 jours", 2.0, 2.0, 1.0)],
        )
