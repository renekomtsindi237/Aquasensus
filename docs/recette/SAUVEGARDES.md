# Sauvegardes PostgreSQL (ISS-064)

**RPO :** 24 h (dump quotidien). **RTO :** 4 h (restauration sur compose vierge + `docker compose up`).

Les dumps ne se commitent pas. Chiffrement : conserver la clé hors du dépôt.

## Sauvegarde (hôte avec Docker)

```powershell
cd infra
.\sauvegarde\sauvegarder.ps1
```

Le script exécute `pg_dump -Fc` dans le conteneur `db` vers `infra/sauvegarde/dumps/`.

Chiffrement optionnel (OpenSSL) :

```powershell
openssl enc -aes-256-cbc -salt -in dumps\aquasensus.dump -out dumps\aquasensus.dump.enc
```

Rétention : supprimer les fichiers de plus de 30 jours dans `dumps/`.

## Restauration testée (compose vierge)

1. Arrêter la pile et **supprimer le volume** `aqs_pg` (données perdues — uniquement sur un environnement de test).
2. `docker compose --env-file .env up -d db`
3. Attendre `pg_isready`.
4. `.\infra\sauvegarde\restaurer.ps1 -Dump .\infra\sauvegarde\dumps\<fichier>.dump`
5. Démarrer `core` : les migrations Flyway déjà dans le dump ne se rejouent pas ; un dump à jour contient le schéma.

**Preuve attendue au jalon J4 :** coller ici la date, le nom du dump restauré, et `GET /api/v1/health` → `ok`.
