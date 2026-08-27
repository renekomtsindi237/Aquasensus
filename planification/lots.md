# Lots et jalons

Durées et livrables : `docs/CAHIER-DES-CHARGES.md` §15. Ce fichier ne fait que les relier aux issues.

## L0 — Socle (2 semaines) → jalon J1

**Livrable :** `docker compose up` démarre PostgreSQL, le cœur Java, un reverse proxy ; `/health` répond ; CI verte ; login possible pour un admin de démonstration.

| Issues | Objectif |
| --- | --- |
| ISS-001 à ISS-009 | Dépôt, Docker, Flyway, Spring, fronts squelette, auth, RBAC, CI, proxy |
| ISS-054 (création de comptes, partie minimale) | Pouvoir se connecter en `ADMIN` |

**Sortie :** aucun métier visible, mais plus aucune barrière d'outillage.

## L1 — Référentiel et signalement (3 semaines)

**Livrable :** créer un ouvrage, le signaler (PWA et file hors ligne), corroborer, qualifier.

| Issues | Objectif |
| --- | --- |
| ISS-010 à ISS-015 | M1 |
| ISS-016 à ISS-022 | M2 |
| ISS-058, ISS-059 | M11 de base (PWA cache + file + idempotence) |

**Dépend de :** J1.

## L2 — Interventions et KPI social (3 semaines) → jalon J2

**Livrable :** cycle complet signalement → intervention → confirmation par un tiers → **temps de rétablissement** affiché. File de travail du délégué.

| Issues | Objectif |
| --- | --- |
| ISS-023 à ISS-030 | M3 |
| ISS-046 | File de travail (EF-56) |
| ISS-055, ISS-056 | Compléments admin |
| ISS-057 | Audit des opérations sensibles (démarrage) |

**Dépend de :** qualification M2. C'est le jalon social : sans J2, le projet n'a pas de preuve d'utilité.

## L3 — Data et prédiction (4 semaines) → jalon J3

**Livrable :** charge d'usage estimée (aucun litre), indice de santé, règles R1–R5, alerte explicable, évaluation rétrospective sur jeu de démo.

| Issues | Objectif |
| --- | --- |
| ISS-031 à ISS-034 | M4 — **aucune saisie de volume** |
| ISS-035 à ISS-042 | M5 + pipeline Python |
| ISS-065 (jeu de démo, amorce) | Graine fixe pour valider le moteur |

**Dépend de :** historique d'interventions (L2) et population desservie (M1). Le service Python down ne doit pas casser le signalement (ENF-13).

## L4 — Restitution et canaux (3 semaines)

**Livrable :** carte, tableau de bord KPI, export, SMS/USSD simulé bout en bout, notifications, PWA installable aboutie.

| Issues | Objectif |
| --- | --- |
| ISS-043 à ISS-045 | Carte, KPI, export |
| ISS-047 à ISS-051 | Canal simulé |
| ISS-052, ISS-053 | Notifications |
| ISS-060 | Conflits de sync visibles |

**Dépend de :** J2 (données à afficher) et J3 (indices et alertes sur la carte).

## L5 — Durcissement et recette (2 semaines) → jalon J4

**Livrable :** v1.0 déployable chez une ONG/mairie, dossier de recette, support de soutenance.

| Issues | Objectif |
| --- | --- |
| ISS-061 à ISS-068 | Sécu, perf, a11y, sauvegardes, docs, UAT, OpenAPI, couverture |

**Dépend de :** parcours nominaux L1–L4 verts.
