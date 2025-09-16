# 🔍 Observabilidade vs Monitoramento – Laboratório Prático

Este repositório demonstra, de forma prática, a diferença entre **monitoramento básico** e **observabilidade completa** usando o **Elastic Stack** e uma aplicação real.

---

## 🎯 Objetivos

- Entender as limitações do monitoramento baseado apenas em métricas.  
- Evoluir para observabilidade correlacionando **logs + métricas + traces**.  
- Simular falhas reais e reduzir o **MTTR** usando o **Kibana** para investigação.  

---

## 🧱 Estrutura do Projeto

```bash
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

---

## ⚙️ Compatibilidade

- Focado em **Elastic 8.x**  
- Você pode ajustar a versão via variável **`ELASTIC_VERSION`** no `docker-compose.yml`.  

---

## 🚀 Como começar

### 🔹 Lab 01 – Monitoramento Básico
Suba o ambiente de monitoramento simples com métricas:

```bash
cd 01-monitoramento-basico
docker compose up -d
```

Acesse o **Kibana**: [http://localhost:5601](http://localhost:5601)

---

### 🔹 Lab 02 – Observabilidade Completa
Suba o ambiente de observabilidade com **logs + métricas + traces**:

```bash
cd 02-observabilidade
docker compose up -d --build
```

Gere carga para simular requisições e falhas:

```bash
./scripts/carga.sh
```

---

## 🔒 Segurança

Este projeto está configurado para **uso local/educacional**:

- As conexões TLS foram simplificadas com **`ssl.verification_mode: none`** ou **`verify_server_cert: false`** nos Beats e APM Agent.  
- O cluster está com **HTTP aberto (sem TLS) e sem autenticação** para reduzir a complexidade inicial.  
- ⚠️ Em produção, **sempre habilite TLS, autenticação e controle de acesso**.

---

## 📚 Documentação

- [Lab 01 – Monitoramento Básico](./docs/01_lab_monitoramento.md)  
- [Lab 02 – Observabilidade Completa](./docs/02_lab_observabilidade.md)  
- [Desafios Propostos](./docs/desafios.md)  
