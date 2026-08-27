# M1 — Référentiel des points d'eau

**Lot :** L1 · **Paquet Java :** `registry`  
**Exigences :** EF-01 à EF-07 · **Règles :** RG-01, RG-12, RG-13  
**Diagrammes :** `UC1-referentiel.puml`, `CL2-referentiel.puml`, `ET1-point-eau.puml`

---

## ISS-010 — Localités hiérarchiques

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L1 |
| Priorité | Must |
| Réf. | EF-02 |

**Objectif :** Région → commune → quartier/village, chemin complet affichable.

**Acceptation**
- [x] Un point d'eau a exactement une localité.
- [x] Recherche par niveau de localité.
- [x] Migration Flyway dédiée (ou étendue V2).

---

## ISS-011 — Créer, modifier, désactiver un point d'eau

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L1 |
| Priorité | Must |
| Réf. | EF-01, RG-01, RG-13 |

**Objectif :** Fiche complète (code unique, type, GPS, comité, population desservie estimée, date de mise en service, intervalle constructeur optionnel). Suppression **logique** uniquement.

**Acceptation**
- [x] `POST/PUT /water-points` réservés `ADMIN`.
- [x] Code dupliqué → `409`. GPS hors emprise → refus explicite.
- [x] État initial `OPERATIONNEL`, historisé.
- [x] Champ `population_desservie` nullable ; son absence dégradera plus tard la confiance (RG-08) — ne pas inventer un défaut silencieux.
- [x] **Aucun** champ volume de référence / litres.

**TDD :** emprise GPS, unicité du code, `actif = false` conserve l'historique.

---

## ISS-012 — Historiser les changements d'état

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L1 |
| Priorité | Must |
| Réf. | EF-04, machine à états ET1 |

**Objectif :** Un seul point d'entrée domaine `changerEtat(...)` ; jamais d'update SQL de `etat` hors de cette méthode.

**Acceptation**
- [x] Chaque transition : auteur, motif, horodatage.
- [x] `GET /water-points/{id}/history`.
- [x] Invariant : état courant = dernier historique.

---

## ISS-013 — Fiche publique et recherche

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L1 |
| Priorité | Must |
| Réf. | EF-03, EF-07, RG-12 |

**Objectif :** Consultation par tous (fiche réduite) ; filtres localité, état, comité, indice (indice pourra être « non calculé » avant L3).

**Acceptation**
- [x] `GET /water-points`, `GET /water-points/{id}` publics en lecture réduite.
- [x] Ouvrage `HORS_SERVICE` visible, distinct, exclu des KPI plus tard (RG-12).
- [x] Pagination plafonnée (taille max 100).

---

## ISS-014 — Import CSV

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L1 |
| Priorité | Should |
| Réf. | EF-05, TR-5 |

**Objectif :** Import admin, rapport ligne à ligne, transaction par ligne.

**Acceptation**
- [x] `POST /api/v1/water-points/import`.
- [x] Une ligne invalide n'empêche pas les autres.
- [x] Aucune colonne « volume » dans le modèle CSV.

---

## ISS-015 — Photos de fiche

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L1 |
| Priorité | Should |
| Réf. | EF-06, ENF-26 |

**Objectif :** JPEG/PNG/WebP, max 3 Mo, compression client, stockage hors racine web, type réel vérifié.

**Acceptation**
- [x] Types non autorisés refusés (magic bytes).
- [x] Nom de fichier généré, pas celui du client.
