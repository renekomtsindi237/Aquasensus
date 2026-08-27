from datetime import date, timedelta

from aquasensus_data.indicateurs import calculer_charge
from aquasensus_data.indicateurs.series import pression_pannes, signaux_et_tendance
from aquasensus_data.indice import calculer_score
from aquasensus_data.parametrage import Parametrage
from aquasensus_data.profil import PeriodeSaison, ProfilOuvrage
from aquasensus_data.regles.r1 import R1EcheanceMaintenance
from aquasensus_data.regles.r2 import R2DegradationProgressive
from aquasensus_data.regles.r3 import R3FragiliteChronique
from aquasensus_data.regles.r4 import R4PressionSaisonniere
from aquasensus_data.regles.r5 import R5CumulCritique
from aquasensus_data.regles.registre import evaluer


def _base(**kwargs) -> ProfilOuvrage:
    today = date(2026, 8, 26)
    valeurs = dict(
        id="1",
        code="YDE",
        localite_id=None,
        population_desservie=450,
        intervalle_constructeur=None,
        date_mise_en_service=today - timedelta(days=200),
        derniere_preventive=today - timedelta(days=170),
        etat="OPERATIONNEL",
        jours_historique=200,
        aujourd_hui=today,
    )
    valeurs.update(kwargs)
    return ProfilOuvrage(**valeurs)


def test_r1_declenche_et_non():
    params = Parametrage()
    oui = _base()
    oui.m = 0.93
    oui.confiance = "HAUTE"
    assert R1EcheanceMaintenance().s_applique(oui, params, set())
    non = _base()
    non.m = 0.5
    non.confiance = "HAUTE"
    assert not R1EcheanceMaintenance().s_applique(non, params, set())


def test_r2_declenche_et_non():
    params = Parametrage()
    today = date(2026, 8, 26)
    croissants = [today - timedelta(days=d) for d in (20, 12, 10, 5, 3, 1)]
    oui = _base(signaux_faibles=croissants)
    signaux_et_tendance(oui, params)
    assert R2DegradationProgressive().s_applique(oui, params, set())
    non = _base(signaux_faibles=[today - timedelta(days=2)])
    signaux_et_tendance(non, params)
    assert not R2DegradationProgressive().s_applique(non, params, set())


def test_r3_declenche_et_non():
    params = Parametrage()
    today = date(2026, 8, 26)
    oui = _base(pannes=[today - timedelta(days=10), today - timedelta(days=40)])
    assert R3FragiliteChronique().s_applique(oui, params, set())
    non = _base(pannes=[today - timedelta(days=10)])
    assert not R3FragiliteChronique().s_applique(non, params, set())


def test_r4_declenche_et_non():
    params = Parametrage()
    saisons = [PeriodeSaison(None, 250, 280, 1.3)]
    oui = _base(saisons=saisons)
    oui.m = 0.7
    oui.confiance = "MOYENNE"
    assert R4PressionSaisonniere().s_applique(oui, params, set())
    non = _base(saisons=saisons)
    non.m = 0.1
    non.p = 0.0
    non.confiance = "MOYENNE"
    assert not R4PressionSaisonniere().s_applique(non, params, set())


def test_r5_score_ou_cumul():
    params = Parametrage()
    bas = _base()
    bas.score = 30
    assert R5CumulCritique().s_applique(bas, params, set())
    cumul = _base()
    cumul.score = 70
    assert R5CumulCritique().s_applique(
        cumul, params, {"R1_ECHEANCE_MAINTENANCE", "R2_DEGRADATION_PROGRESSIVE"}
    )
    non = _base()
    non.score = 70
    assert not R5CumulCritique().s_applique(non, params, set())


def test_rg16_confiance_faible_bloque_r1_r4():
    params = Parametrage()
    p = _base()
    p.m = 0.99
    p.confiance = "FAIBLE"
    p.score = 80
    assert not R1EcheanceMaintenance().s_applique(p, params, set())
    assert not R4PressionSaisonniere().s_applique(p, params, set())
    p.pannes = [p.aujourd_hui - timedelta(days=5), p.aujourd_hui - timedelta(days=20)]
    assert R3FragiliteChronique().s_applique(p, params, set())


def test_moins_de_30_jours_aucune_alerte():
    params = Parametrage()
    p = _base(jours_historique=10)
    p.m = 1.0
    p.confiance = "FAIBLE"
    p.score = 20
    calculer_charge(p, params)
    pression_pannes(p, params)
    signaux_et_tendance(p, params)
    calculer_score(p, params)
    assert evaluer(p, params) == []


def test_json_explicable_r2():
    params = Parametrage()
    today = date(2026, 8, 26)
    p = _base(signaux_faibles=[today - timedelta(days=d) for d in (20, 12, 10, 5, 3, 1)])
    signaux_et_tendance(p, params)
    alerte = R2DegradationProgressive().expliquer(p, params)
    assert alerte.facteurs
    assert "débit" in alerte.explication.lower()
    assert "litre" not in alerte.explication.lower()
