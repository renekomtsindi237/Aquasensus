# AquaSensus

![AquaSensus](docs/design/aquasensus-logo.png)

**Plateforme collaborative de suivi et de maintenance prédictive des forages communautaires.**

AquaSensus aide les quartiers périphériques de Yaoundé, et plus largement le rural et le semi-rural camerounais, à **garder l’eau potable avant que la pompe lâche**. Ce n’est ni un démonstrateur IoT, ni un outil ministériel : c’est un logiciel léger, open-source, pensé pour un comité de quartier, une ONG ou une mairie d’arrondissement.

---

## Le problème

Quand un forage communautaire tombe en panne, des centaines de familles se retrouvent sans eau du jour au lendemain. La réparation arrive tard, à l’aveugle. La corvée d’eau retombe surtout sur les femmes et les enfants (école, revenus, santé). L’eau de surface redevient tentante. Les tensions de quartier montent.

Aujourd’hui, on **réagit quand la pompe est déjà morte**. AquaSensus vise l’inverse : **anticiper et coordonner**.

## Ce que fait la plateforme

Les habitants **signalent** un dysfonctionnement (web, mobile, ou SMS/USSD simulé). Le comité **qualifie**, priorise et envoie un technicien. Chacun voit le **même état** des points d’eau sur une carte. Un **pipeline data** (extraction, indicateurs, règles explicables) alerte quand un ouvrage approche de son échéance d’entretien.

Le projet porte **deux ingénieries** : logiciel (API, rôles, états, interfaces) et data (ETL, qualité, prédiction interprétable). Aucun volume d’eau n’est saisi.

**Indicateur n°1 :** le **temps de rétablissement** — combien de temps les familles restent sans eau.

Exemple d’alerte : *« Ce forage a atteint 93 % de son échéance d’entretien pour 450 habitants desservis, dont 40 jours de saison sèche — risque de panne sous ~2 semaines. »*

### Aucun volume d’eau n’est saisi

Sur le terrain, les habitants puisent librement. Personne ne sait, en fin de journée, combien de litres ont été tirés. Demander ce chiffre produirait des écrans vides. AquaSensus **n’enregistre donc ni litres, ni bidons, ni minutes de pompage**. L’usure d’usage est estimée à partir de ce qu’on connaît déjà : population desservie, calendrier (saison sèche), date de dernière maintenance.

## À qui ça s’adresse

| Rôle | Intention |
| --- | --- |
| Habitant | Signaler vite, savoir si c’est pris en charge |
| Délégué / comité | Décider quoi réparer, dans quel ordre |
| Technicien | Arriver informé, consigner le diagnostic, même hors ligne |
| Association / mairie | Carte, KPI, redevabilité devant un bailleur |
| Administrateur | Référentiel des forages, comptes, paramétrage |

Pas de KYC lourd, pas d’accord opérateur ni de capteurs chers comme prérequis.

## Hors v1 (volontaire)

Capteurs IoT payants, contrats SMS réels, accords gouvernementaux, modèles d’apprentissage complexes.

---

## Documentation

| Document | Contenu |
| --- | --- |
| [`docs/CONTEXTE-AQUASENSUS.md`](docs/CONTEXTE-AQUASENSUS.md) | Bible du projet (source de vérité) |
| [`docs/CAHIER-DES-CHARGES.md`](docs/CAHIER-DES-CHARGES.md) | Exigences et règles métier |
| [`docs/CAHIER-ANALYSE.md`](docs/CAHIER-ANALYSE.md) | Cas d’utilisation et modèle du domaine |
| [`docs/CAHIER-CONCEPTION.md`](docs/CAHIER-CONCEPTION.md) | Architecture et API |
| [`docs/guides/SOUTENANCE.md`](docs/guides/SOUTENANCE.md) | Démo courte |
| [`docs/recette/`](docs/recette/) | Recette et preuves |

---

## Lancer le projet (technique)

À la racine (`compose.yml`). Sous **Windows (cmd)**, `make.cmd` permet `make build` sans GNU Make. Docker Desktop doit être démarré.

```powershell
copy .env.example .env
make build
make up
```

Équivalent : `docker compose --env-file .env up --build -d` puis `docker compose ps`.

| Service | Rôle | Accès |
| --- | --- | --- |
| `db` | PostgreSQL 16 | `localhost:5432` |
| `core` | Backend Java / Spring | `localhost:8080` |
| `data` | Pipeline Python | interne uniquement |
| `web` | PWA Angular | via le proxy |
| `proxy` | Nginx | [http://localhost/](http://localhost/) |

Santé : [http://localhost/api/v1/health](http://localhost/api/v1/health). Arrêt : `make down`. Seed de soutenance : décommenter `AQS_PROFILE=demo` dans `.env`.

### Sans Docker

| Dossier | Commande |
| --- | --- |
| `backend-java/` | `.\mvnw.cmd spring-boot:run` (PostgreSQL local) |
| `frontend-angular/` | `npm start` → [http://localhost:4200](http://localhost:4200) |
| `mobile-flutter/` | `flutter run` |
| `data-python/` | voir le README du dossier |

**Stack :** Angular PWA + Flutter · Java 21 / Spring · Python · PostgreSQL 16 · Flyway SQL uniquement.

**Tests :** `cd frontend-angular` puis `npm run e2e` (Playwright). `cd mobile-flutter` puis `flutter test`.
