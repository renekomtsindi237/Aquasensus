#!/usr/bin/env sh
set -eu
DUMP="${1:?usage: restaurer.sh fichier.dump}"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
COMPOSE="$ROOT/../compose.yml"
ENVF="$ROOT/../.env"
if [ -f "$ENVF" ]; then
  docker compose -f "$COMPOSE" --env-file "$ENVF" exec -T db pg_restore -U aquasensus -d aquasensus --clean --if-exists < "$DUMP"
else
  docker compose -f "$COMPOSE" exec -T db pg_restore -U aquasensus -d aquasensus --clean --if-exists < "$DUMP"
fi
echo "Restauration demandée depuis $DUMP"
