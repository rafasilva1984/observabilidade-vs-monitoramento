# 🧪 Lab 02 – Observabilidade vs Monitoramento  

Este laboratório demonstra a diferença entre **monitoramento tradicional** e **observabilidade**, utilizando o **Elastic Stack** (Elasticsearch, Kibana, APM e Filebeat) + uma aplicação Java simples.

---

## 🚀 Subindo o ambiente  

1. Clone o repositório e entre no diretório do Lab 02:  
   ```bash
   git clone https://github.com/rafasilva1984/observabilidade-vs-monitoramento.git
   cd observabilidade-vs-monitoramento/02-observabilidade
   ```

2. Suba os containers:  
   ```bash
   docker compose up -d --build
   ```

3. Aguarde os serviços subirem:  
   - **Elasticsearch** → [http://localhost:9220](http://localhost:9220)  
   - **Kibana** → [http://localhost:5621](http://localhost:5621)  
   - **APM Server** → [http://localhost:8200](http://localhost:8200)  
   - **Aplicação Java** → [http://localhost:8088](http://localhost:8088)  

---

## ⚠️ Ajuste importante – Permissões no APM e Filebeat  

No Docker Desktop (Windows/Mac), arquivos montados entram com permissão **777** dentro do container.  
O **APM Server** e o **Filebeat** não aceitam isso e exibem erro:  

```
Exiting: error loading config file: config file ("apm-server.yml") can only be writable by the owner...
```

🔧 **Solução aplicada no `docker-compose.yml`:**  
- Montar os arquivos de configuração como **read-only** (`:ro`)  
- Adicionar a flag `-strict.perms=false` no comando de inicialização  

Exemplo do serviço **APM Server**:  
```yaml
apm-server:
  image: docker.elastic.co/apm/apm-server:${ELASTIC_VERSION:-7.17.22}
  container_name: obsv-apm
  volumes:
    - ./apm-server.yml:/usr/share/apm-server/apm-server.yml:ro
  ports:
    - "8200:8200"
  command: ["apm-server","-e","-strict.perms=false","-c","/usr/share/apm-server/apm-server.yml"]
```

📌 Caso o mesmo erro ocorra no **Filebeat**, aplicar a mesma solução.

---

## 🧩 Testando a aplicação  

1. Verifique se a aplicação responde na rota de saúde:  
   ```bash
   curl -s http://localhost:8088/ok
   ```

2. Gere carga simulada para os endpoints:  
   ```bash
   ./scripts/carga.sh
   ```

3. Confirme se os dados chegaram no **Elasticsearch**:  
   ```bash
   curl -s 'http://localhost:9220/_cat/indices?v' | grep -E 'filebeat|apm'
   ```

4. Acesse no Kibana:  
   - **Discover** → índice `filebeat-*`  
   - **APM → Services** → serviço `app-java-lab`  

---

## ✅ Resultado esperado  

- 📊 **Logs da aplicação** coletados pelo Filebeat disponíveis no índice `filebeat-*`.  
- 📈 **Métricas de performance** da aplicação coletadas no APM.  
- 🔎 **Dashboard de Observabilidade** acessível no Kibana, mostrando logs, métricas e traces.  

---

## 🎯 Objetivo pedagógico  

- Demonstrar que **monitoramento tradicional** responde apenas "a aplicação está no ar?".  
- Mostrar que a **observabilidade** vai além: **o que a aplicação está fazendo, como está performando e por que falhou**.  

---
