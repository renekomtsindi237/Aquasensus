# M6 — Carte, tableau de bord et file de travail

**Lots :** L2 (file) · L4 (carte, KPI, export)  
**Paquet :** `analytics` (lectures) + fronts  
**Exigences :** EF-50 à EF-56 · **KPI :** KPI-01 à KPI-08  
**Diagrammes :** `UC6-pilotage-kpi.puml`  
**Charte :** couleurs d'état §4.3, forme + libellé (ENF-43)

---

## ISS-043 — Carte des points d'eau

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L4 |
| Priorité | Must |
| Réf. | EF-50, EF-51, EF-52, ENF-04, ENF-43 |

**Objectif :** Marqueurs état (couleur **et** forme), clustering > 50, légende permanente, fiche depuis marqueur, filtres.

**Acceptation**
- [x] `GET /api/v1/water-points/map` projection allégée (forme + libellé).
- [ ] 500 marqueurs sans gel (recette perf en L5).
- [x] L'état n'est jamais porté par la seule couleur (ENF-43).

---

## ISS-044 — Tableau de bord KPI

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L4 |
| Priorité | Must |
| Réf. | EF-53, EF-54, KPI-01 à KPI-07 |

**Objectif :** Médiane et P90 du rétablissement, états, alertes actives, interventions en cours, délai comité → technicien, taux d'anticipation. Comparaison de deux périodes (Should).

**Acceptation**
- [x] `GET /api/v1/dashboard/kpi` — agrégation SQL, pas d'hydratation d'agrégats.
- [x] Filtres période, localité, comité.
- [x] `HORS_SERVICE` exclu des KPI de disponibilité (RG-12).
- [x] Partenaire : agrégats uniquement, pas de déclarants nominatifs (PO-5).

---

## ISS-045 — Export CSV et rapport PDF

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L4 |
| Priorité | Should |
| Réf. | EF-55 |

**Objectif :** Export du filtre courant ; PDF synthèse mensuelle. Génération en flux.

**Acceptation**
- [x] `GET /api/v1/dashboard/export` (CSV agrégé, pas de nominatif).
- [x] PDF synthèse mensuelle (`GET /api/v1/dashboard/export.pdf`).

---

## ISS-046 — File de travail du délégué

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L2 |
| Priorité | Must |
| Réf. | EF-56 |

**Objectif :** Signalements à qualifier, alertes à traiter (dès L3), interventions en retard — triés par priorité.

**Acceptation**
- [x] Périmètre du comité uniquement.
- [x] Disponible dès J2 pour les signalements, étendue aux alertes en L3.
