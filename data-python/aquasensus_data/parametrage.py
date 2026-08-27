from dataclasses import dataclass


@dataclass(frozen=True)
class Parametrage:
    version: str = "v1"
    intervalle_base: int = 180
    population_reference: int = 300
    intervalle_min: int = 90
    intervalle_max: int = 270
    coefficient_defaut: float = 1.0
    w_s: float = 0.35
    w_p: float = 0.35
    w_m: float = 0.30
    seuil_r1: float = 0.85
    seuil_r4_m: float = 0.60
    seuil_r4_p: float = 1.0
    horizon_jours: int = 14
    demi_vie_pannes: int = 60
    fenetre_pannes: int = 180
    fenetre_signaux: int = 21
