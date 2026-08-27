from datetime import timedelta

from aquasensus_data.parametrage import Parametrage
from aquasensus_data.profil import ProfilOuvrage
from aquasensus_data.regles.base import AlerteProposee, Facteur, Regle


class R2DegradationProgressive(Regle):
    code = "R2_DEGRADATION_PROGRESSIVE"
    niveau = "ELEVE"

    def s_applique(self, profil: ProfilOuvrage, params: Parametrage, autres: set[str]) -> bool:
        debut = profil.aujourd_hui - timedelta(days=params.fenetre_signaux - 1)
        n = sum(1 for d in profil.signaux_faibles if debut <= d <= profil.aujourd_hui)
        return profil.t > 0 and n >= 3

    def expliquer(self, profil: ProfilOuvrage, params: Parametrage) -> AlerteProposee:
        debut = profil.aujourd_hui - timedelta(days=params.fenetre_signaux - 1)
        n = sum(1 for d in profil.signaux_faibles if debut <= d <= profil.aujourd_hui)
        return AlerteProposee(
            self.code,
            self.niveau,
            f"Les signalements de débit faible augmentent depuis 3 semaines ({n} signaux faibles en 21 jours).",
            "Planifier une inspection de la pompe et prévoir un jeu de joints.",
            [
                Facteur("S", "Signalements faibles sur 21 jours", float(n), 3.0, 0.6),
                Facteur("T", "Tendance des signaux faibles", profil.t, 0.0, 0.4),
            ],
        )
