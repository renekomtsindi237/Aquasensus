# AquaSensus Core (Java / Spring)

Prérequis : JDK 21, Maven Wrapper inclus.

```powershell
cd backend-java
copy ..\.env.example ..\.env   # optionnel, hors Docker
.\mvnw.cmd spring-boot:run
```

Santé : `GET http://localhost:8080/api/v1/health`  
Login : `POST /api/v1/auth/login` avec `admin@aquasensus.local` / mot de passe `AQS_ADMIN_PASSWORD` (profil `dev`).

Les migrations Flyway sont le seul moyen de faire évoluer le schéma (`ddl-auto: validate`).
