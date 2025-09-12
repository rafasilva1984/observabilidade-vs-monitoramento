#!/usr/bin/env bash
set -e
APP_URL="${APP_URL:-http://localhost:8088}"
echo "Gerando carga contra ${APP_URL} ..."
for i in $(seq 1 200); do
  curl -fsS "${APP_URL}/ok" >/dev/null || true
  curl -fsS "${APP_URL}/checkout" >/dev/null || true
  sleep 0.2
done
echo "Concluído."
