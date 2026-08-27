#!/usr/bin/env sh
set -eu
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
DEST="$ROOT/sauvegarde/dumps"
mkdir -p "$DEST"
STAMP="$(date +%Y%m%d-%H%M%S)"
OUT="$DEST/aquasensus-$STAMP.dump"
COMPOSE="$ROOT/../compose.yml"
ENVF="$ROOT/../.env"
if [ -f "$ENVF" ]; then
  docker compose -f "$COMPOSE" --env-file "$ENVF" exec -T db pg_dump -U aquasensus -d aquasensus -Fc > "$OUT"
else
  docker compose -f "$COMPOSE" exec -T db pg_dump -U aquasensus -d aquasensus -Fc > "$OUT"
fi
echo "Dump écrit : $OUT"
