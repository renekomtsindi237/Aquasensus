# Installation (ISS-066)

1. Copier `.env.example` vers `.env` et remplacer **tous** les secrets (`AQS_JWT_SECRET` ≥ 32 caractères, mot de passe admin, secret interne).
2. Docker + Compose v2.
3. À la racine : `docker compose --env-file .env up --build -d`
4. Santé : `http://localhost/api/v1/health`
5. PWA (via Nginx, service `web`) : `http://localhost/` — carte, KPI, signalement, simulation.
6. PWA en local sans Docker : `cd frontend-angular` puis `npm start` → `http://localhost:4200`
6. Démo soutenance : `AQS_PROFILE=demo` dans `.env` (charge `R__seed_demo.sql`, **jamais en prod**).
7. OpenAPI : `http://localhost:8080/api/docs/ui` (ou via le proxy selon Nginx).
8. Python data n'est **pas** exposé publiquement.

Aucun volume d'eau n'est demandé nulle part.
