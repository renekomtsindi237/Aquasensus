# Durcissement, qualité et recette (L5)

**Lot :** L5 · **Jalon :** J4 — v1.0 déployable  
**Réf. :** CDC §10, §12, §13, §17 · QA-1 à QA-7 · UAT

---

## ISS-061 — Sécurité applicative (OWASP, fichiers, secrets)

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L5 |
| Priorité | Must |
| Réf. | ENF-23 à ENF-27, ENF-30 à ENF-32 |

**Objectif :** Validation, requêtes paramétrées, en-têtes, CORS, CSRF, pas de traces en prod, fichiers bornés, minimisation RGPD-like, masquage téléphones.

**Acceptation**
- [x] Revue checklist OWASP documentée (`docs/recette/OWASP.md`).
- [x] Aucun secret dans le dépôt ; garde `prod` ; dumps gitignorés.
- [x] Tests 401/403 (`SecuriteRbacEtEntetesTest`).

---

## ISS-062 — Performance et charge minimale

| Champ | Valeur |
| --- | --- |
| Statut | Fait (CI + script k6) |
| Lot | L5 |
| Priorité | Must |
| Réf. | ENF-01, ENF-02, ENF-06, QA-6 |

**Objectif :** P95 lecture < 400 ms, écriture < 800 ms ; 200 utilisateurs simulés sans casser les cibles.

**Acceptation**
- [x] Scénario k6 `infra/charge/lecture.js` + `docs/recette/PERF.md`.
- [x] Carte 500 points (`CarteChargeApiTest`). Mesure P95 prod à coller après k6 sur la pile Docker.

---

## ISS-063 — Accessibilité WCAG 2.1 AA

| Champ | Valeur |
| --- | --- |
| Statut | Fait (socle + axe partiel) |
| Lot | L5 |
| Priorité | Must |
| Réf. | ENF-40 à ENF-46, QA-7, charte |

**Objectif :** Contraste, clavier, ARIA, français, SMS simples.

**Acceptation**
- [x] axe-core Karma sur signalement, file, liste.
- [x] Playwright (parcours UX/UI, clavier, mobile, hors ligne) + axe-core sur les pages critiques.
- [x] Six états forme + libellé (légende carte) ; revue tokens documentée.

---

## ISS-064 — Sauvegardes et restauration

| Champ | Valeur |
| --- | --- |
| Statut | Fait (procédure) |
| Lot | L5 |
| Priorité | Must |
| Réf. | ENF-11, DO-6, DO-8, DO-9 |

**Objectif :** `pg_dump` quotidien chiffré, rétention 30 j, restauration **testée** une fois. RPO 24 h, RTO 4 h documentés.

**Acceptation**
- [x] Procédure `docs/recette/SAUVEGARDES.md` + scripts `infra/sauvegarde/`.
- [ ] Preuve dump restauré sur compose vierge (à dater au jalon J4).

---

## ISS-065 — Jeu de démonstration à graine fixe

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L3 (amorce) / L5 (12 mois réalistes) |
| Priorité | Must |
| Réf. | QA-4, MD-6, profil `demo` uniquement |

**Objectif :** `R__seed_demo.sql` impossible à charger en `prod`. Historique suffisant pour valider R1–R5 a posteriori.

**Acceptation**
- [x] Graine fixe (UUID déterministes, `md5` pour séries).
- [x] Aucune donnée personnelle réelle ; **aucun** volume (`SeedDemoSansVolumeTest`).
- [x] Locations Flyway `demo` seulement (`application-demo.yml` vs `application-prod.yml`).

---

## ISS-066 — Documentation d'exploitation, guides et recette UAT

| Champ | Valeur |
| --- | --- |
| Statut | Fait (dossier ouvert) |
| Lot | L5 |
| Priorité | Must |
| Réf. | CDC §17.2 UAT-01 à UAT-10, livrables §15 |

**Objectif :** Guide install, guide délégué/technicien illustré, dossier de recette signé, support de soutenance.

**Acceptation**
- [x] UAT cochés avec date/résultat (`docs/recette/UAT.md`).
- [ ] `docker compose up` relu par une personne qui n'a pas développé le socle.

---

## ISS-067 — OpenAPI 3.1 complet et à jour

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L5 (publication) — à maintenir dès L1 |
| Priorité | Must |
| Réf. | CDC §8.1, `/api/docs` |

**Objectif :** Tous les endpoints v1 documentés, erreurs `problem+json`, exemples signalement.

**Acceptation**
- [x] Groupe `v1` sans `/internal/**` (`OpenApiPublicTest`).
- [x] Groupe `interne` séparé ; contrôleur analytics `@Hidden` du groupe public.

---

## ISS-068 — Couverture de tests, RG et architecture

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L5 |
| Priorité | Must |
| Réf. | QA-1, QA-2, QA-3, conception §15.5 |

**Objectif :** ≥ 70 % cœur Java et Python. Chaque EF Must a un test auto. Chaque RG-nn a un test dédié. Tests d'architecture (domaine sans Spring, pas de JPA dans le domaine).

**Acceptation**
- [x] CI JaCoCo (seuil bundle 55 %, à durcir) + pytest-cov 70 % `aquasensus_data`.
- [x] Matrice RG `docs/recette/MATRICE-RG.md` ; ArchUnit déjà L0.
