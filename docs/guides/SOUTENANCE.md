# Support de soutenance — AquaSensus v1.0

**Promesse :** moins de pannes surprises sur les forages communautaires, **sans aucun volume d’eau saisi**.

## Démo 8 minutes

1. `docker compose --env-file .env up --build` puis `http://localhost/` (PWA).
2. Signaler une panne (`/signaler`, OTP `123456`) — référence affichée.
3. Délégué : file, qualification, intervention, confirmation → **temps de rétablissement**.
4. Carte : couleur **+ forme + libellé** (ENF-43).
5. KPI + export CSV/PDF + budget indicatif (pas de nom de déclarant).
6. Notifications in-app (`/notifications`) après un signalement grave.
7. Admin : `/comptes`, `/admin` (CSV, pièces), `/simulation`.
8. Arrêter le service `data` : le signalement continue (ENF-13).

Comptes démo : `admin@aquasensus.local` / mot de passe `.env` ; `delegue.a@…` / `DelegueA!2026`.

Profil `AQS_PROFILE=demo` : 8 ouvrages et 12 mois d’indices, **aucun litre**.

## Ce que nous n’avons pas fait (volontaire)

Capteurs IoT, opérateur SMS réel, KYC, e-mail, préférences de notification (Could), job « intervention en retard ».

## Preuves

Tests CI (Java / Python / Angular) · OpenAPI `/api/docs/ui` · Recette `docs/recette/`.
