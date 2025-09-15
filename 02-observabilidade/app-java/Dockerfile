# ---------- BUILD (sem Maven) ----------
FROM eclipse-temurin:17-jdk AS build
WORKDIR /work

# API pública do APM para compilar (bypass SSL)
RUN mkdir -p /work/lib && \
    curl -fsSL -k -o /work/lib/apm-agent-api.jar \
      https://repo1.maven.org/maven2/co/elastic/apm/apm-agent-api/1.48.0/apm-agent-api-1.48.0.jar

# Código-fonte
COPY src ./src

# Compila com a API no classpath
RUN mkdir -p /app/classes && \
    find ./src -name "*.java" > sources.txt && \
    javac -cp /work/lib/apm-agent-api.jar -d /app/classes @sources.txt

# Empacota com main-class
RUN mkdir -p /app && \
    jar --create --file /app/app.jar \
        --main-class com.observabilidade.lab.AppJavaApplication \
        -C /app/classes .

# ---------- RUNTIME ----------
FROM eclipse-temurin:17-jre
WORKDIR /app
ENV JAVA_OPTS=""

# App e API do APM
COPY --from=build /app/app.jar app.jar
COPY --from=build /work/lib/apm-agent-api.jar apm-agent-api.jar

# Agent do APM (bypass SSL)
RUN curl -fsSL -k -o elastic-apm-agent.jar \
  https://repo1.maven.org/maven2/co/elastic/apm/elastic-apm-agent/1.48.0/elastic-apm-agent-1.48.0.jar

EXPOSE 8080

# Variáveis do lab (bypass habilitado)
ENV SERVER_PORT=8080
ENV APM_SERVER_URL=http://apm-server:8200
ENV APM_SERVICE_NAME=app-java-lab
ENV APM_ENVIRONMENT=lab
ENV APM_VERIFY_SERVER_CERT=false

# Classpath inclui a API do APM; ativa o javaagent
ENTRYPOINT ["sh","-c","java -cp /app/app.jar:/app/apm-agent-api.jar \
  -javaagent:/app/elastic-apm-agent.jar \
  -Delastic.apm.server_urls=${APM_SERVER_URL} \
  -Delastic.apm.service_name=${APM_SERVICE_NAME} \
  -Delastic.apm.environment=${APM_ENVIRONMENT} \
  -Delastic.apm.verify_server_cert=${APM_VERIFY_SERVER_CERT} \
  ${JAVA_OPTS} com.observabilidade.lab.AppJavaApplication"]
