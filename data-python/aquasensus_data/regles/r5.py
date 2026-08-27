from aquasensus_data.parametrage import Parametrage
from aquasensus_data.profil import ProfilOuvrage
from aquasensus_data.regles.base import AlerteProposee, Facteur, Regle


class R5CumulCritique(Regle):
    code = "R5_CUMUL_CRITIQUE"
    niveau = "CRITIQUE"

    def s_applique(self, profil: ProfilOuvrage, params: Parametrage, autres: set[str]) -> bool:
        score_bas = profil.score is not None and profil.score < 40
        cumul = "R1_ECHEANCE_MAINTENANCE" in autres and "R2_DEGRADATION_PROGRESSIVE" in autres
        return score_bas or cumul

    def expliquer(self, profil: ProfilOuvrage, params: Parametrage) -> AlerteProposee:
        return AlerteProposee(
            self.code,
            self.niveau,
            f"Risque de panne très élevé sous {params.horizon_jours} jours.",
            "Mobiliser le comité : intervention prioritaire.",
            [
                Facteur("score", "Indice de santé", profil.score or 0, 40.0, 1.0),
            ],
        )
