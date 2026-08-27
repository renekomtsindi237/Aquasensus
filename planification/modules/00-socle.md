# Socle technique (hors module métier)

**Lot :** L0 · **Jalon :** J1  
**Paquets :** `backend-java`, `frontend-angular`, `mobile-flutter`, `infra/`  
**Livraison L0 :** 2026-08-26 — cœur Java testé (`mvnw test`) ; IT PostgreSQL optionnelle (`AQS_IT_POSTGRES=1`) ; CI Java + syntaxe Python.  
**Exigences transverses :** ENF-14, ENF-20 à ENF-22, ENF-27, DO-1 à DO-5, DO-10

---

## ISS-001 — Structurer le dépôt selon la conception

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L0 |
| Priorité | Must |
| Réf. | CDC §11.4, DA-03, DA-04 |

**Objectif :** Arborescence `backend-java/`, `data-python/`, `frontend-angular/`, `mobile-flutter/`, `infra/`, `docs/` déjà en place et respectée.

**Acceptation**
- [x] Les dossiers cibles existent, avec README techniques minimaux (comment lancer **ce** service uniquement).
- [x] Aucune logique métier dans un front (rappel DA-10).
- [x] Logo copié depuis `docs/design/aquasensus-logo.png` vers `assets/brand/` (non modifié).

**Hors périmètre :** lakehouse, microservices.

---

## ISS-002 — Docker Compose et PostgreSQL

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L0 |
| Priorité | Must |
| Réf. | DO-1, DO-2, DO-4, DO-10, ENF-14 |

**Objectif :** Une commande démarre la base sur une machine Linux (ou Docker Desktop). Empreinte compatible 2 vCPU / 4 Go.

**Acceptation**
- [x] `docker compose up` démarre `db` (PostgreSQL 16) avec volume persistant.
- [x] Configuration par variables d'environnement ; `.env.example` documenté, aucun secret dans Git.
- [x] Images multi-étapes prévues (même squelette).

---

## ISS-003 — Cœur Spring : santé, métriques, migrations Flyway

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L0 |
| Priorité | Must |
| Réf. | MD-1, DO-3, GET `/health`, GET `/metrics` |

**Objectif :** Le JAR démarre, joue les migrations SQL, refuse de démarrer si une migration échoue. `ddl-auto: validate`.

**Acceptation**
- [x] Script `V1__schema_initial.sql` (localités, comités, utilisateurs, rôles) versionné.
- [x] `GET /api/v1/health` et `/metrics` répondent.
- [x] Test d'intégration : migrations sur base réelle éphémère.

**TDD :** test d'échec de migration avant d'écrire le script suivant.

---

## ISS-004 — Authentification JWT

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L0 |
| Priorité | Must |
| Réf. | EF-80, EF-83, EF-85, ENF-20, ENF-21, ENF-24 |

**Objectif :** Login, refresh, verrouillage après 5 échecs, hachage Argon2id ou BCrypt coût ≥ 10.

**Acceptation**
- [x] `POST /auth/login`, `POST /auth/refresh`.
- [x] Jeton d'accès 15 min, rafraîchissement 7 j, stocké haché, révocable.
- [x] Messages d'erreur génériques (pas d'énumération de comptes).
- [x] Tests : mot de passe invalide, 5e échec, compte verrouillé.

**Diagrammes :** `SQ1-authentification.puml`

---

## ISS-005 — Autorisation rôle + périmètre

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L0 |
| Priorité | Must |
| Réf. | ENF-22, DA-10, `PolitiqueAcces` |

**Objectif :** Chaque appel métier vérifie le rôle **et** le périmètre côté serveur. Les fronts ne protègent rien.

**Acceptation**
- [x] Un délégué du comité A reçoit `403` sur un ouvrage du comité B.
- [x] Tentative journalisée.
- [x] Test d'architecture : aucun contrôleur n'accède à un dépôt directement.

**Diagrammes :** `CL1-identite-rbac.puml`

---

## ISS-006 — Squelette Angular PWA + design tokens

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L0 |
| Priorité | Must |
| Réf. | Charte, ENF-03 (budget à viser dès le squelette), FR-3, FR-7 |

**Objectif :** Application installable minimale, écran de connexion, tokens CSS, logo par défaut.

**Acceptation**
- [x] `tokens.css` consommé, aucune couleur brute.
- [x] Logo `aquasensus-logo.png` ; inverse sur fond sombre.
- [x] Aucun écran de saisie de volume (FR-7).

---

## ISS-007 — Squelette Flutter + design tokens

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L0 |
| Priorité | Must |
| Réf. | Charte `tokens.dart`, Android 8+, 2 Go RAM |

**Objectif :** App terrain compilable, thème issu des tokens, logo, écran de connexion.

**Acceptation**
- [x] `ThemeData` depuis `tokens.dart`.
- [x] Cibles tactiles 48 px sur les boutons principaux.
- [x] Aucun widget de relevé de volume.

---

## ISS-008 — Intégration continue

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L0 |
| Priorité | Must |
| Réf. | DO-5, QA-1 (seuil activé quand il y a du code métier) |

**Objectif :** Compilation, tests, analyse statique, audit de dépendances ; la régression bloque la fusion.

**Acceptation**
- [x] Pipeline sur chaque merge request.
- [x] Java et (plus tard) Python, Angular, Flutter branchés progressivement.
- [x] Pas de `--no-verify` documenté comme pratique.

---

## ISS-009 — Reverse proxy, TLS de démo, limitation de débit

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L0 |
| Priorité | Must |
| Réf. | ENF-24, ENF-25, DO-2 |

**Objectif :** Nginx route vers l'API et les fichiers statiques ; rate limit 60 req/min/IP en configuration.

**Acceptation**
- [x] Profils `dev` / `demo` / `prod` distincts.
- [x] HTTPS documenté pour `prod` (terminaison proxy).
- [x] API interne Python **non** routée publiquement.
