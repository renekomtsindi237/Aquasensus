# M4 — Charge d'usage et saisonnalité

**Lot :** L3 · **Paquet :** `charge` (Java) + calcul quotidien (Python)  
**Exigences :** EF-30 à EF-35 · **Règles :** RG-08 · **Hypothèse :** H-2  
**Diagrammes :** `UC4-charge-saisonnalite.puml`, `CL5-charge-usage.puml`, `AC6-calcul-charge-usage.puml`

**Interdit :** litres, bidons, seaux, minutes de pompage, table `releve_volume`, écran de saisie.

La charge est un **objet valeur recalculé**, pas une saisie.

---

## ISS-031 — Charge cumulée en jours pondérés

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L3 |
| Priorité | Must |
| Réf. | EF-30, CDC §9.3 |

**Objectif :** `charge_cumulée = Σ k(j)` depuis la dernière maintenance préventive (sinon mise en service).

**Acceptation**
- [x] Unité affichée : jours pondérés, jamais litres.
- [x] Calcul pour **tout** ouvrage actif, même sans autre saisie que la fiche.
- [x] Tests : saison sèche vs hors saison, date de référence manquante.

**TDD :** formule du CDC avant l'adaptateur Python.

---

## ISS-032 — Calendrier saisonnier paramétrable

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L3 |
| Priorité | Must |
| Réf. | EF-31, `GET/PUT /seasons` |

**Objectif :** Coefficient défaut 1,0 ; saison sèche 1,3. Périodes d'une localité sans chevauchement.

**Acceptation**
- [x] Admin seul en écriture.
- [x] Absence de calendrier → coefficient 1,0 partout, mentionné dans l'explication.
- [x] Migration `V5__calendrier_saisonnier.sql`.

---

## ISS-033 — Intervalle de maintenance effectif

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L3 |
| Priorité | Must (calcul) / Should (saisie constructeur) |
| Réf. | EF-32, EF-33 |

**Objectif :** `intervalle_base × (pop_réf / pop_desservie)` borné [90, 270], défauts CDC. Valeur manuelle constructeur prime.

**Acceptation**
- [x] 800 habitants → échéance plus courte que 150.
- [x] Champ `intervalle_maintenance_jours` optionnel sur l'ouvrage.
- [x] `M = charge / intervalle`, borné à 1,5.

---

## ISS-034 — Afficher l'estimation et signaler un référentiel incomplet

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L3 |
| Priorité | Must |
| Réf. | EF-34, EF-35, KPI-08 |

**Objectif :** Phrase du type : « estimation fondée sur 450 habitants desservis et 168 jours depuis la dernière maintenance, dont 40 jours de saison sèche ». Badge si population ou date de référence absente.

**Acceptation**
- [x] `GET /water-points/{id}/health` inclut charge, intervalle, échéance.
- [x] Aucun écran n'affiche de volume en litres (contrôle de recette + FR-7).
- [x] Invitation à corriger la fiche, pas d'imputation silencieuse de population.
