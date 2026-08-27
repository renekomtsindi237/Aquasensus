from aquasensus_data.parametrage import Parametrage
from aquasensus_data.profil import ProfilOuvrage
from aquasensus_data.regles.base import AlerteProposee, Facteur, Regle, estimation_retenue


class R1EcheanceMaintenance(Regle):
    code = "R1_ECHEANCE_MAINTENANCE"
    niveau = "ELEVE"

    def s_applique(self, profil: ProfilOuvrage, params: Parametrage, autres: set[str]) -> bool:
        if estimation_retenue(profil) or profil.m is None:
            return False
        return profil.m >= params.seuil_r1

    def expliquer(self, profil: ProfilOuvrage, params: Parametrage) -> AlerteProposee:
        pct = int(round((profil.m or 0) * 100))
        pop = profil.population_desservie or 0
        texte = (
            f"{int(profil.charge_cumulee or 0)} jours d'usage pour {pop} habitants desservis, "
            f"dont {profil.jours_saison_seche} jours de saison sèche : échéance d'entretien atteinte à {pct} %."
        )
        return AlerteProposee(
            self.code,
            self.niveau,
            texte,
            "Planifier la maintenance préventive avant la panne.",
            [
                Facteur(
                    "M",
                    f"Échéance d'entretien ({profil.charge_cumulee:.0f} jours pondérés sur {profil.intervalle_effectif})",
                    profil.m or 0,
                    params.seuil_r1,
                    1.0,
                )
            ],
        )
