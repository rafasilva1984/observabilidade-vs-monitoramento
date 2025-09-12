# Guia do Lab 02 — Observabilidade Completa

## Objetivo
Correlacionar **logs + métricas + traces** para reduzir o MTTR.

## Passos
1. Suba o ambiente (`docker compose up -d --build`).
2. Gere carga (`./scripts/carga.sh`).
3. Observe picos de latência/erros em APM.
4. Abra traces e conecte com logs.
5. Valide impacto nas métricas (containers/host).
