# M5 — Indice de santé et alertes prédictives

**Lot :** L3 · **Paquets :** `prediction` (Java, cycle de vie) + `data-python` (calcul)  
**Exigences :** EF-40 à EF-47 · **Règles :** RG-06, RG-07, RG-08, RG-15, RG-16  
**Diagrammes :** `UC5-prediction-alertes.puml`, `CL6-prediction.puml`, `AC4`, `AC5`, `SQ8`, `ET4`

Java ne calcule pas les indicateurs. Python n'expose pas d'API publique. Arrêt Python ≠ arrêt du signalement (ENF-13).

---

## ISS-035 — Pipeline Python et API interne

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L3 |
| Priorité | Must |
| Réf. | CDC §8.2 interne, ENF-05, ENF-13, DA-01 |

**Objectif :** Extraction incrémentale, publication indices/alertes, secret partagé, réseau privé.

**Acceptation**
- [x] `GET /internal/analytics/dataset`, `POST .../health-scores`, `POST .../alerts`.
- [x] Non routé par nginx public.
- [x] Python down : signalement et interventions intactes, derniers indices figés.
- [ ] Traitement < 10 min sur le volume cible (peut être mesuré en L5).

---

## ISS-036 — Indicateurs M, P, S, T et indice 0–100

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L3 |
| Priorité | Must |
| Réf. | EF-40, CDC §9.3–9.4, RG-15 |

**Objectif :** Score quotidien + événementiel (clôture, panne). Pondérations `w_S=0,35`, `w_P=0,35`, `w_M=0,30`. Bandes de la charte.

**Acceptation**
- [x] Panne confirmée impose `EN_PANNE` quel que soit le score.
- [x] Historique < 30 j : indice confiance faible, **aucune** alerte.
- [x] Tests Pytest par indicateur, y compris population inconnue.

**Interdit :** indicateurs U (volume) et A séparés — fusionnés dans `M`.

---

## ISS-037 — Moteur de règles R1 à R5

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L3 |
| Priorité | Must |
| Réf. | EF-41, CDC §9.5 |

**Objectif :** Une classe par règle, registre, explication propre. R4 = pression saisonnière (plus « maintenance en retard » redondante avec R1).

**Acceptation**
- [x] Tests de déclenchement **et** de non-déclenchement pour chaque règle.
- [x] Horizon défaut 14 jours.

---

## ISS-038 — Alerte explicable

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L3 |
| Priorité | Must |
| Réf. | EF-42 |

**Objectif :** 3 facteurs, valeurs, seuils, règle, recommandation en français de comité. Pas de score nu.

**Acceptation**
- [x] JSON d'exemple du CDC §9.6 productible.
- [x] Recette manuelle : un non-spécialiste comprend la phrase.

---

## ISS-039 — Cycle de vie : acquitter, reporter, contester, caducité

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L3 |
| Priorité | Must |
| Réf. | EF-44, EF-47, RG-07, ET4 |

**Objectif :** Une seule alerte active par règle et par ouvrage. Contestation historisée, plus de relance.

**Acceptation**
- [x] `PATCH /alerts/{id}`.
- [x] Deuxième alerte même règle → ignorée.
- [x] Test RG-07 (négatif : pas d'effacement).

---

## ISS-040 — Paramétrage versionné

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L3 |
| Priorité | Should |
| Réf. | EF-45 |

**Objectif :** Seuils, pondérations, horizon historisés. Chaque alerte/indice fige `version_parametrage`.

**Acceptation**
- [x] Une alerte passée reste lisible après changement de seuils (`GET /engine/parameters/history`).

---

## ISS-041 — Confiance et rétention des alertes estimées

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L3 |
| Priorité | Must |
| Réf. | RG-08, RG-16, EF-35 |

**Objectif :** Confiance HAUTE/MOYENNE/FAIBLE selon CDC. En FAIBLE : seulement R2, R3, R5 (faits observés).

**Acceptation**
- [x] Tests RG-08 et RG-16.
- [x] Interface : mention « fiche incomplète », pas « volumes estimés ».

---

## ISS-042 — Issues d'alerte et évaluation du moteur

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L3 |
| Priorité | Must |
| Réf. | EF-46, RG-06, KPI-02, KPI-06, CDC §9.7 |

**Objectif :** À l'échéance : PANNE_SURVENUE / PANNE_EVITEE / INDETERMINEE. Taux d'anticipation et de fausses alertes.

**Acceptation**
- [x] Validation rétrospective sur jeu à graine fixe (ISS-065).
- [x] Cibles v1 suivies, pas forcément atteintes en démo.
