from aquasensus_data.parametrage import Parametrage
from aquasensus_data.profil import ProfilOuvrage
from aquasensus_data.regles.base import AlerteProposee, alertes_autorisees
from aquasensus_data.regles.r1 import R1EcheanceMaintenance
from aquasensus_data.regles.r2 import R2DegradationProgressive
from aquasensus_data.regles.r3 import R3FragiliteChronique
from aquasensus_data.regles.r4 import R4PressionSaisonniere
from aquasensus_data.regles.r5 import R5CumulCritique

REGISTRE = [
    R1EcheanceMaintenance(),
    R2DegradationProgressive(),
    R3FragiliteChronique(),
    R4PressionSaisonniere(),
    R5CumulCritique(),
]


def evaluer(profil: ProfilOuvrage, params: Parametrage) -> list[AlerteProposee]:
    if not alertes_autorisees(profil):
        return []
    retenues: list[AlerteProposee] = []
    codes: set[str] = set()
    # R5 a besoin des autres : deux passes
    for _ in range(2):
        codes = {a.type_regle for a in retenues}
        retenues = []
        for regle in REGISTRE:
            if regle.s_applique(profil, params, codes):
                retenues.append(regle.expliquer(profil, params))
    return retenues
