# M10 — Journal d'audit et traçabilité

**Lots :** L2 (branchement) · L5 (écran de consultation abouti)  
**Priorité module :** Should  
**Exigences :** EF-90 à EF-92

---

## ISS-057 — Journal en insertion seule, consultable

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L2 (écriture) / L5 (filtre admin) |
| Priorité | Should |
| Réf. | EF-90, EF-91, EF-92, RG-09 |

**Objectif :** Création/modif référentiel, changement d'état, affectation, clôture, rôles, paramètres moteur. Colonnes `avant` / `après` structurées. Aucun UPDATE/DELETE applicatif.

**Acceptation**
- [x] Insertion seule applicative (aucun UPDATE/DELETE exposé).
- [x] `GET /api/v1/audit` — ADMIN, filtres entité / acteur / période.
- [x] Pas de téléphone en clair dans le journal.
- [x] Distinct des logs techniques (rétention documentée 24 mois vs 30 jours).
