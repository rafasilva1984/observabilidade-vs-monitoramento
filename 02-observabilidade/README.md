# Lab 02 — Observabilidade Completa (Logs + Métricas + Traces)

Neste lab vamos **correlacionar logs, métricas e traces** para chegar à **causa raiz** de problemas em uma aplicação real.

## Objetivo
- Mostrar a diferença prática entre monitoramento (Lab 01) e observabilidade (Lab 02).
- Usar logs, métricas e traces para identificar **lentidão** e **erros simulados** em um app Java.

---

## Subir o ambiente
```bash
cd 02-observabilidade
docker compose up -d --build
```

### Serviços disponíveis
- **Elasticsearch** → [http://localhost:9220](http://localhost:9220)  
- **Kibana** → [http://localhost:5621](http://localhost:5621)  
- **APM Server** → [http://localhost:8200](http://localhost:8200)  
- **App Java (simulado)** → [http://localhost:8088](http://localhost:8088)  

---

## Validar ingestão
### Elasticsearch saudável
```bash
curl -s http://localhost:9220/_cluster/health?pretty
```

### Confirmar data streams do Filebeat e APM
```bash
curl -s 'http://localhost:9220/_cat/data_streams?v' | grep -E "filebeat|traces"
```

---

## Gerar carga e falhas
O app possui dois endpoints:
- `/ok` → responde sempre rápido com **200 OK**.  
- `/checkout` → simula:
  - **30%** de erro (500).  
  - **40%** de lentidão (1,5–4s).  
  - **30%** de resposta rápida.  

### Script de carga
```bash
./scripts/carga.sh
```

### Alternativa manual (simples)
```bash
for i in $(seq 1 50); do
  curl -fsS http://localhost:8088/ok >/dev/null || true
  curl -fsS http://localhost:8088/checkout >/dev/null || true
done
```

---

## Investigação no Kibana

### 1. **APM / Services**
- Vá em **Observability → APM → Services**.  
- Selecione `app-java-lab`.  
- Veja **latência** e **taxa de erro**.  

### 2. **Traces**
- Abra o trace de uma transação `/checkout`.  
- Veja onde ocorreu a falha ou lentidão.  

### 3. **Logs (Filebeat)**
- Vá em **Discover** com data view `filebeat-*`.  
- Procure por mensagens:
  - `"Erro de processamento no /checkout (simulado)"`
  - `"Checkout lento"`

### 4. **Métricas (Metricbeat ou APM JVM)**
- Compare **CPU/memória** do container com os períodos de falha/lentidão.  

---

## Conclusão
- **Lab 01** → vimos apenas **sintomas** (ex.: CPU alta).  
- **Lab 02** → conseguimos **explicar a causa raiz**:
  - Traces mostram **onde** a transação quebrou.  
  - Logs mostram **o erro real**.  
  - Métricas mostram **o impacto no host/container**.  

➡️ Isso é **observabilidade**: juntar os sinais para entender *por que* o problema ocorreu.

---

## Desafios extras
1. Criar um dashboard correlacionando:
   - Latência média (`transaction.duration.us`)
   - Taxa de erro
   - Logs de erro/lentidão
2. Medir o MTTR: quanto tempo leva entre o erro ocorrer e você achar a causa no Kibana?
3. (Avançado) Habilitar segurança no Elastic (x-pack, TLS, usuário `elastic`).

