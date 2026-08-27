import json

from aquasensus_data.profil import ProfilOuvrage
from aquasensus_data.regles.base import AlerteProposee


def facteurs_json(alerte: AlerteProposee) -> str:
    return json.dumps(
        [
            {
                "code": f.code,
                "libelle": f.libelle,
                "valeur": f.valeur,
                "seuil": f.seuil,
                "contribution": f.contribution,
            }
            for f in alerte.facteurs[:3]
        ],
        ensure_ascii=False,
    )


def phrase_indice(profil: ProfilOuvrage) -> list[dict]:
    return [
        {
            "code": "M",
            "libelle": "Charge de maintenance (jours pondérés / intervalle)",
            "valeur": profil.m,
        },
        {
            "code": "P",
            "libelle": "Pannes pondérées par la récence",
            "valeur": profil.p,
        },
        {
            "code": "S",
            "libelle": "Signaux faibles normalisés",
            "valeur": profil.s,
        },
    ]
