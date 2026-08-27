# AquaSensus

Plateforme collaborative de signalement et de maintenance prédictive des forages communautaires (périphérie de Yaoundé). **Aucun volume d’eau n’est saisi** : la charge d’usage est un proxy calendaire.

Documentation : `docs/CONTEXTE-AQUASENSUS.md` (bible). Recette : `docs/recette/`. Soutenance : `docs/guides/SOUTENANCE.md`.

## Démarrage global (Docker Compose)

À la racine du dépôt (`compose.yml`) :

```powershell
copy .env.example .env
docker compose --env-file .env up --build -d
docker compose ps
```

| Service | Rôle | Accès |
| --- | --- | --- |
| `db` | PostgreSQL 16 | `localhost:5432` |
| `core` | Backend Java / Spring | `localhost:8080` |
| `data` | Pipeline Python | interne uniquement |
| `web` | PWA Angular (Nginx) | via `proxy` |
| `proxy` | Nginx reverse proxy | `http://localhost/` |

Santé : `http://localhost/api/v1/health`. Arrêt : `docker compose down`. Pour le seed soutenance, décommenter `AQS_PROFILE=demo` dans `.env`.

Raccourcis optionnels : `make help`.

## Services isolés (sans Docker)

| Dossier | Lancer |
| --- | --- |
| `backend-java/` | `.\mvnw.cmd spring-boot:run` (PostgreSQL local requis) |
| `frontend-angular/` | `npm start` |
| `mobile-flutter/` | `flutter run` |
| `data-python/` | voir README du dossier |

## Stack

Angular PWA + Flutter · Java 21 / Spring · Python · PostgreSQL 16 · Flyway SQL uniquement.

Tests UX/UI : `cd frontend-angular` puis `npm run e2e` (Playwright, API mockée).
Tests mobile : `cd mobile-flutter` puis `flutter test` (widget + SQ6, API mockée).
