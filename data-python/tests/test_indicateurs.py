from datetime import date, timedelta

from aquasensus_data.indicateurs.series import confiance, pression_pannes, signaux_et_tendance
from aquasensus_data.indice import calculer_score
from aquasensus_data.parametrage import Parametrage
from aquasensus_data.profil import ProfilOuvrage


def test_p_decroit_avec_lanciennete():
    params = Parametrage()
    today = date(2026, 8, 26)
    recent = ProfilOuvrage("1", "A", None, 300, None, today - timedelta(days=200), None, "OPERATIONNEL", 200, pannes=[today - timedelta(days=5)], aujourd_hui=today)
    ancien = ProfilOuvrage("2", "B", None, 300, None, today - timedelta(days=200), None, "OPERATIONNEL", 200, pannes=[today - timedelta(days=120)], aujourd_hui=today)
    pression_pannes(recent, params)
    pression_pannes(ancien, params)
    assert recent.p > ancien.p


def test_s_neutralise_si_population_inconnue():
    params = Parametrage()
    today = date(2026, 8, 26)
    dates = [today - timedelta(days=i) for i in range(5)]
    p = ProfilOuvrage("1", "A", None, None, None, today - timedelta(days=200), None, "OPERATIONNEL", 200, signaux_faibles=dates, aujourd_hui=today)
    signaux_et_tendance(p, params)
    assert p.s == 0.0


def test_confiance_rg08():
    today = date(2026, 8, 26)
    faible = ProfilOuvrage("1", "A", None, None, None, None, None, "OPERATIONNEL", 10, aujourd_hui=today)
    assert confiance(faible) == "FAIBLE"
    moyenne = ProfilOuvrage("2", "B", None, 400, None, today - timedelta(days=100), None, "OPERATIONNEL", 100, aujourd_hui=today)
    assert confiance(moyenne) == "MOYENNE"
    haute = ProfilOuvrage("3", "C", None, 400, None, today - timedelta(days=200), today - timedelta(days=30), "OPERATIONNEL", 200, aujourd_hui=today)
    assert confiance(haute) == "HAUTE"


def test_score_pondere():
    params = Parametrage()
    p = ProfilOuvrage("1", "A", None, 300, None, date(2025, 1, 1), None, "OPERATIONNEL", 400, aujourd_hui=date(2026, 8, 26))
    p.m = 0.0
    p.p = 0.0
    p.s = 0.0
    assert calculer_score(p, params) == 100
    p.m = 1.0
    p.p = 3.0
    p.s = 1.0
    assert calculer_score(p, params) == 0
