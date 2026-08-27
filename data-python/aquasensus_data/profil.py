from __future__ import annotations

from dataclasses import dataclass, field
from datetime import date, datetime
from typing import Any


@dataclass
class PeriodeSaison:
    localite_id: str | None
    jour_debut: int
    jour_fin: int
    coefficient: float

    def couvre(self, jour_annee: int) -> bool:
        if self.jour_debut <= self.jour_fin:
            return self.jour_debut <= jour_annee <= self.jour_fin
        return jour_annee >= self.jour_debut or jour_annee <= self.jour_fin


@dataclass
class ProfilOuvrage:
    id: str
    code: str
    localite_id: str | None
    population_desservie: int | None
    intervalle_constructeur: int | None
    date_mise_en_service: date | None
    derniere_preventive: date | None
    etat: str
    jours_historique: int
    pannes: list[date] = field(default_factory=list)
    signaux_faibles: list[date] = field(default_factory=list)
    saisons: list[PeriodeSaison] = field(default_factory=list)
    aujourd_hui: date = field(default_factory=date.today)

    charge_cumulee: float | None = None
    intervalle_effectif: int | None = None
    m: float | None = None
    p: float = 0.0
    s: float = 0.0
    t: float = 0.0
    confiance: str = "FAIBLE"
    score: float | None = None
    bande: str | None = None
    calendrier_absent: bool = False
    jours_saison_seche: int = 0


def parse_date(valeur: Any) -> date | None:
    if valeur is None:
        return None
    if isinstance(valeur, date) and not isinstance(valeur, datetime):
        return valeur
    texte = str(valeur)
    if "T" in texte:
        return datetime.fromisoformat(texte.replace("Z", "+00:00")).date()
    return date.fromisoformat(texte[:10])
