from datetime import date, timedelta

import pytest

from aquasensus_data.indicateurs import calculer_charge, intervalle_effectif
from aquasensus_data.parametrage import Parametrage
from aquasensus_data.profil import PeriodeSaison, ProfilOuvrage


def test_saison_seche_vs_defaut():
    params = Parametrage()
    fin = date(2026, 1, 20)
    debut = fin - timedelta(days=10)
    seche = [PeriodeSaison(None, 1, 366, 1.3)]
    p1 = ProfilOuvrage("1", "A", None, 300, None, debut, debut, "OPERATIONNEL", 200, saisons=seche, aujourd_hui=fin)
    p2 = ProfilOuvrage("2", "B", None, 300, None, debut, debut, "OPERATIONNEL", 200, saisons=[], aujourd_hui=fin)
    calculer_charge(p1, params)
    calculer_charge(p2, params)
    assert p1.charge_cumulee == pytest.approx(13.0)
    assert p2.charge_cumulee == pytest.approx(10.0)
    assert p2.calendrier_absent is True


def test_reference_absente():
    p = ProfilOuvrage("1", "A", None, 400, None, None, None, "OPERATIONNEL", 10, aujourd_hui=date(2026, 8, 26))
    calculer_charge(p, Parametrage())
    assert p.m is None
    assert p.charge_cumulee is None


def test_intervalle_800_plus_court_que_150():
    params = Parametrage()
    assert intervalle_effectif(800, None, params) == 90
    assert intervalle_effectif(150, None, params) == 270
    assert intervalle_effectif(800, 120, params) == 120
