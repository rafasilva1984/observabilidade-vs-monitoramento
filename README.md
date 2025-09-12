# Observabilidade vs Monitoramento – Laboratório Prático

Este repositório demonstra, de forma prática, a diferença entre **monitoramento básico** e **observabilidade completa** usando o Elastic Stack e uma aplicação real.

## 🎯 Objetivos
- Entender as limitações do monitoramento baseado apenas em métricas.
- Evoluir para observabilidade correlacionando **logs + métricas + traces**.
- Simular falhas reais e **reduzir o MTTR** usando o Kibana para investigação.

## 🧱 Estrutura
```
observabilidade-vs-monitoramento/
├── 01-monitoramento-basico/
│   ├── docker-compose.yml
│   ├── metricbeat.yml
│   └── README.md
├── 02-observabilidade/
│   ├── docker-compose.yml
│   ├── apm-server.yml
│   ├── filebeat.yml
│   ├── app-java/
│   │   ├── Dockerfile
│   │   ├── pom.xml
│   │   └── src/...
│   ├── scripts/
│   │   └── carga.sh
│   └── README.md
└── docs/
    ├── 01_lab_monitoramento.md
    ├── 02_lab_observabilidade.md
    └── desafios.md
```

> **Compatibilidade**: focado em **Elastic 8.x**. Você pode ajustar a versão via variável `ELASTIC_VERSION` no `docker-compose.yml`.

## 🚀 Como começar (resumo)
1. **Lab 01** – suba o monitoramento básico:
   ```bash
   cd 01-monitoramento-basico
   docker compose up -d
   ```
   Acesse Kibana: http://localhost:5601

2. **Lab 02** – suba a observabilidade completa:
   ```bash
   cd 02-observabilidade
   docker compose up -d --build
   ```
   Gere carga:
   ```bash
   ./scripts/carga.sh
   ```

> **Bypass de CA / SSL**: os serviços estão configurados para **ambiente local**. Onde há conexões TLS possíveis, já deixamos `ssl.verification_mode: none`/`verify_server_cert: false` nas ferramentas (Beats/APM agent) quando aplicável. Para simplificar, os clusters estão com HTTP aberto (sem TLS) e **sem autenticação**. Em produção, habilite TLS e segurança.
