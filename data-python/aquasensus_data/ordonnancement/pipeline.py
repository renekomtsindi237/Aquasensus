from __future__ import annotations

import json
import os
from datetime import date

import httpx

from aquasensus_data.explication import facteurs_json, phrase_indice
from aquasensus_data.indicateurs import calculer_charge
from aquasensus_data.indicateurs.series import confiance, pression_pannes, signaux_et_tendance
from aquasensus_data.indice import calculer_score
from aquasensus_data.parametrage import Parametrage
from aquasensus_data.profil import PeriodeSaison, ProfilOuvrage, parse_date
from aquasensus_data.regles.registre import evaluer


def profil_depuis_dataset(ouvrage: dict, saisons: list[dict], date_calcul: date, params: Parametrage) -> ProfilOuvrage:
    periodes = [
        PeriodeSaison(s.get("localiteId"), s["jourDebut"], s["jourFin"], s["coefficient"]) for s in saisons
    ]
    signaux = [
        parse_date(s["declareLe"])
        for s in ouvrage.get("signalements", [])
        if s.get("signalFaible") and parse_date(s["declareLe"])
    ]
    pannes = [parse_date(p) for p in ouvrage.get("pannesIso", [])]
    return ProfilOuvrage(
        id=ouvrage["id"],
        code=ouvrage.get("code", ""),
        localite_id=ouvrage.get("localiteId"),
        population_desservie=ouvrage.get("populationDesservie"),
        intervalle_constructeur=ouvrage.get("intervalleMaintenanceJours"),
        date_mise_en_service=parse_date(ouvrage.get("dateMiseEnService")),
        derniere_preventive=parse_date(ouvrage.get("dernierePreventive")),
        etat=ouvrage.get("etat", "OPERATIONNEL"),
        jours_historique=int(ouvrage.get("joursHistorique") or 0),
        pannes=[p for p in pannes if p],
        signaux_faibles=[s for s in signaux if s],
        saisons=periodes,
        aujourd_hui=date_calcul,
    )


def traiter_profil(profil: ProfilOuvrage, params: Parametrage) -> tuple[dict, list[dict]]:
    calculer_charge(profil, params)
    pression_pannes(profil, params)
    signaux_et_tendance(profil, params)
    confiance(profil)
    calculer_score(profil, params)
    indice = {
        "pointEauId": profil.id,
        "dateCalcul": profil.aujourd_hui.isoformat(),
        "score": round(profil.score or 0, 2),
        "bande": profil.bande,
        "confiance": profil.confiance,
        "chargeCumuleeJours": profil.charge_cumulee,
        "intervalleEffectifJours": profil.intervalle_effectif,
        "indicateurM": profil.m,
        "indicateurP": profil.p,
        "indicateurS": profil.s,
        "indicateurT": profil.t,
        "facteurs": json.dumps(phrase_indice(profil), ensure_ascii=False),
        "versionParametrage": params.version,
    }
    alertes = []
    for a in evaluer(profil, params):
        alertes.append(
            {
                "pointEauId": profil.id,
                "typeRegle": a.type_regle,
                "niveau": a.niveau,
                "horizonJours": params.horizon_jours,
                "explication": a.explication,
                "recommandation": a.recommandation,
                "facteurs": facteurs_json(a),
                "versionParametrage": params.version,
            }
        )
    return indice, alertes


def executer(core_url: str | None = None, secret: str | None = None) -> dict:
    core_url = (core_url or os.environ.get("AQS_CORE_URL", "http://127.0.0.1:8080")).rstrip("/")
    secret = secret or os.environ.get("AQS_INTERNAL_SECRET", "dev-internal-secret-change-me")
    headers = {"X-Aqs-Internal-Secret": secret}
    params = Parametrage()
    with httpx.Client(timeout=60.0) as client:
        jeu = client.get(f"{core_url}/internal/analytics/dataset", headers=headers).json()
        date_calcul = parse_date(jeu["dateCalcul"]) or date.today()
        saisons = jeu.get("saisons", [])
        indices = []
        alertes = []
        for ouvrage in jeu.get("ouvrages", []):
            profil = profil_depuis_dataset(ouvrage, saisons, date_calcul, params)
            indice, al = traiter_profil(profil, params)
            indices.append(indice)
            alertes.extend(al)
        if indices:
            client.post(f"{core_url}/internal/analytics/health-scores", headers=headers, json=indices)
        if alertes:
            client.post(f"{core_url}/internal/analytics/alerts", headers=headers, json=alertes)
        return {"indices": len(indices), "alertes": len(alertes)}
