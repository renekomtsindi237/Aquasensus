# M3 — Interventions et maintenance

**Lot :** L2 · **Paquet Java :** `maintenance`  
**Exigences :** EF-20 à EF-28 · **Règles :** RG-04, RG-05 · **KPI :** KPI-01, KPI-04  
**Diagrammes :** `UC3-maintenance.puml`, `CL4-maintenance.puml`, `AC3`, `SQ5`, `SQ7`, `ET3`

C'est le cœur social : **déclarer n'est pas clôturer**.

---

## ISS-023 — Ouvrir une intervention

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L2 |
| Priorité | Must |
| Réf. | EF-20 |

**Objectif :** Depuis signalements qualifiés, alerte (L3), ou saisie manuelle. Types CORRECTIVE / PREVENTIVE / INSPECTION.

**Acceptation**
- [x] `POST /interventions`.
- [x] Origine tracée (`SIGNALEMENT`, `ALERTE`, `MANUELLE`).
- [x] Référence lisible `INT-AAAA-nnnn`.

---

## ISS-024 — Affecter technicien et échéance

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L2 |
| Priorité | Must |
| Réf. | EF-21, KPI-04 |

**Objectif :** Transition `OUVERTE` → `AFFECTEE`, jalon `affectee_le` (délai comité → technicien).

**Acceptation**
- [x] Notification technicien (peut rester no-op jusqu'à M8, mais événement publié).
- [x] Périmètre du délégué vérifié.

---

## ISS-025 — Machine à états des interventions

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L2 |
| Priorité | Must |
| Réf. | EF-22, DA-07 |

**Objectif :** Graphe ET3 uniquement. Hors graphe → 422. `SUSPENDUE` exige motif fermé.

**Acceptation**
- [x] `POST /interventions/{id}/transitions`.
- [x] Version concurrente → 409.
- [x] Tests : **chaque** transition autorisée et **chaque** interdite.
- [x] Ouvrage → `EN_REPARATION` au démarrage.

**TDD :** table de transitions écrite avant le service.

---

## ISS-026 — Compte rendu technique et pièces

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L2 |
| Priorité | Must |
| Réf. | EF-23, RG-05 |

**Objectif :** Diagnostic, cause racine, actions, pièces, coûts, photos. Complet exigé pour `REALISEE`.

**Acceptation**
- [x] `PUT /interventions/{id}/report`, `POST .../parts`.
- [x] Transition `REALISEE` refusée si diagnostic ou action manquant (RG-05).
- [ ] Saisie hors ligne possible (M11).

---

## ISS-027 — Confirmation du rétablissement et KPI n°1

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L2 |
| Priorité | Must |
| Réf. | EF-24, EF-25, RG-04, KPI-01 |

**Objectif :** Confirmateur **distinct** du technicien. Délai = clôture − **premier signalement rattaché**.

**Acceptation**
- [x] Confirmateur = déclarant → 422.
- [x] Signalements rattachés → `RESOLU`.
- [x] Ouvrage → `OPERATIONNEL`.
- [x] Habitants notifiés (événement ; canal en M8).
- [x] Tests unitaires RG-04 (cas sans signalement, réouverture).

**Diagrammes :** `SQ7-cloture-et-kpi.puml`

---

## ISS-028 — Dossier de préparation du technicien

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L2 |
| Priorité | Must |
| Réf. | EF-26 |

**Objectif :** Symptômes corroborés, historique de pannes, pièces déjà posées, accès — **avant** le déplacement.

**Acceptation**
- [x] `GET /interventions/{id}/briefing`.
- [ ] Disponible hors ligne si synchronisé avant départ (M11).

---

## ISS-029 — Budget indicatif

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L2 |
| Priorité | Should |
| Réf. | EF-27 |

**Objectif :** Coût pièces + main-d'œuvre, agrégation comité / période. Pas de paiement.

**Acceptation**
- [x] Visible délégué et partenaire du périmètre (`GET /api/v1/dashboard/budget`).
- [x] Aucun objet `Facture`.

---

## ISS-030 — Réouverture si récidive 15 jours

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L2 |
| Priorité | Could |
| Réf. | EF-28 |

**Objectif :** Nouvelle intervention liée à l'originale.

**Acceptation**
- [x] Lien de filiation persisté (`POST /interventions/{id}/reouverture`).
- [x] Le KPI de la première clôture n'est pas écrasé (nouvelle intervention).
