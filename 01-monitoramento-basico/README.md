# Lab 01 — Monitoramento Básico

Objetivo: visualizar **métricas isoladas** (CPU, memória, rede, containers) sem correlação com logs e traces.

## Subir o ambiente
```bash
docker compose up -d
```

- Kibana: http://localhost:5601  
- Elasticsearch: http://localhost:9200

## O que explorar no Kibana
1. **Métricas do Docker**: identificar containers com CPU alta.
2. **Métricas do sistema**: ver uso de CPU e memória da máquina host.
3. **Limitações**: com métricas apenas, fica difícil achar a **causa raiz**.

> Próximo: no Lab 02 vamos adicionar **logs e traces** para fechar o ciclo de investigação.
