# Lab 02 — Observabilidade Completa (Logs + Métricas + Traces)

Neste lab vamos **correlacionar logs, métricas e traces** para chegar à causa raiz de problemas na aplicação.

## Subir o ambiente
```bash
docker compose up -d --build
```

- Elasticsearch: http://localhost:9220
- Kibana: http://localhost:5621
- APM Server: http://localhost:8200
- App Java: http://localhost:8088

## Gerar carga e falhas
```bash
./scripts/carga.sh
```
O endpoint `/checkout` simula **erros (500)** e **lentidão** para você investigar.

## Investigar no Kibana (passo a passo)
1. **APM / Services**: veja `app-java-lab` e métricas de latência/erros.
2. **Traces**: abra transações lentas de `/checkout`.
3. **Logs**: procure por `Erro de processamento` e `Checkout lento`.
4. **Métricas**: compare com métricas de container/host para entender impacto.

> **Bypass de CA**: o APM Agent está com `verify_server_cert=false` por padrão. Beats usam HTTP sem TLS e `ssl.verification_mode` desnecessário neste lab.
