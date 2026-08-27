# Reverse proxy (ISS-009)

Les fichiers Nginx restent ici. **L’orchestration Compose est à la racine** (`compose.yml`).

| Fichier | Usage |
| --- | --- |
| `nginx/dev.conf` | HTTP local, rate limit 60 req/min/IP |
| `nginx/demo.conf` | Idem, bannière demo |
| `nginx/prod.conf` | Redirection HTTPS, certificats à monter en volume |

Variable `AQS_NGINX_CONF` (`dev` / `demo` / `prod`) dans `.env`.

```powershell
docker compose --env-file .env up --build -d
```

Le service `data` n’a pas de `location` publique.
