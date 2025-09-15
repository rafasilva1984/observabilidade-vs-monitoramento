# Lab 01 — Monitoramento Básico (Elastic 8.x, data streams)

Objetivo: visualizar **métricas isoladas** (CPU, memória, rede, containers) sem correlação com logs e traces — mostrando o limite do monitoramento.

## Subir o ambiente
```bash
docker compose up -d
```
- Elasticsearch: http://localhost:9200  
- Kibana: http://localhost:5601  

> **Permissões resolvidas**: o Metricbeat já inicia com `-strict.perms=false` para evitar erro ao montar `metricbeat.yml` em NTFS/Windows.

## Validar ingestão (data streams)
```bash
# Data streams do Metricbeat
curl -s 'http://localhost:9200/_cat/data_streams?v' | grep -i metricbeat

# Backing indices (ex.: .ds-metricbeat-8.14.3-2025.09.12-000001)
curl -s 'http://localhost:9200/_cat/indices?v' | grep -i '\.ds-metricbeat'

# Contagem de documentos
curl -s 'http://localhost:9200/metricbeat-*/_count?pretty'
```

## Criar Data View (Kibana)
**Stack Management → Data Views → Create**
- **Name**: `metricbeat`
- **Index pattern**: `metricbeat-*`
- **Time field**: `@timestamp`

> Use `metricbeat-*` (alias do data stream), **não** `.ds-*`.

## Discover (filtros úteis)
- **Host (system)**: `event.module : "system"`
- **Containers (docker)**: `event.module : "docker"`
- **Campo do lab** (opcional): `lab.scenario : "monitoramento-basico"`

## Visualizações rápidas (Lens)
- **Containers por CPU** — *Max of* `docker.cpu.total.pct` + *Break down* `docker.container.name`
- **CPU do host** — *Average of* `system.cpu.total.pct`
- **Memória do host** — *Average of* `system.memory.actual.used.pct`

## Dashboards prontos (opcional)
Edite `metricbeat.yml` e habilite:
```yaml
setup.dashboards.enabled: true
setup.kibana.host: "kibana:5601"
```
Depois:
```bash
docker compose down
docker compose up -d
```
Acesse: **Kibana → Dashboards → Metricbeat**.

## Gerar pico de CPU (para ver acontecer)
```bash
docker run --rm --name stress busybox sh -c "yes > /dev/null & sleep 60"
```

## Mensagem-chave do Lab 01
Você vê **sintomas** (CPU/memória/containers) com `metricbeat-*` (data stream), mas não a **causa raiz**.  
➡️ Essa é a limitação do **monitoramento**. O **Lab 02** fecha a lacuna com **logs + métricas + traces**.
