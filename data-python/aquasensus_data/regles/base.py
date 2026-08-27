from __future__ import annotations

from abc import ABC, abstractmethod
from dataclasses import dataclass

from aquasensus_data.parametrage import Parametrage
from aquasensus_data.profil import ProfilOuvrage


@dataclass
class Facteur:
    code: str
    libelle: str
    valeur: float
    seuil: float
    contribution: float


@dataclass
class AlerteProposee:
    type_regle: str
    niveau: str
    explication: str
    recommandation: str
    facteurs: list[Facteur]


class Regle(ABC):
    code: str
    niveau: str

    @abstractmethod
    def s_applique(self, profil: ProfilOuvrage, params: Parametrage, autres: set[str]) -> bool:
        raise NotImplementedError

    @abstractmethod
    def expliquer(self, profil: ProfilOuvrage, params: Parametrage) -> AlerteProposee:
        raise NotImplementedError


def alertes_autorisees(profil: ProfilOuvrage) -> bool:
    return profil.jours_historique >= 30


def estimation_retenue(profil: ProfilOuvrage) -> bool:
    """RG-16 : en confiance faible, pas d'alerte fondée sur M."""
    return profil.confiance == "FAIBLE"
