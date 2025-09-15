# ---------- BUILD ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copia POM e resolve dependências em cache, com BYPASS SSL
COPY pom.xml .
RUN mvn -q -e -B -DskipTests dependency:go-offline \
    -Dmaven.wagon.http.ssl.insecure=true \
    -Dmaven.wagon.http.ssl.allowall=true

# (Opcional) Baixar o APM agent no build em vez de usar curl no runtime
# RUN mvn -q -e -B -DskipTests dependency:get \
#     -Dartifact=co.elastic.apm:elastic-apm-agent:1.48.0 \
#     -Dmaven.wagon.http.ssl.insecure=true \
#     -Dmaven.wagon.http.ssl.allowall=true

COPY src ./src
RUN mvn -q -e -B -DskipTests package \
    -Dmaven.wagon.http.ssl.insecure=true \
    -Dmaven.wagon.http.ssl.allowall=true

# ---------- RUNTIME ----------
FROM eclipse-temurin:17-jre
WORKDIR /app
ENV JAVA_OPTS=""

# App
COPY --from=build /app/target/app-java-*.jar app.jar

# APM agent:
# Opção A (runtime com curl BYPASS SSL) — simples para lab
RUN curl -fsSL -k -o elastic-apm-agent.jar \
  https://repo1.maven.org/maven2/co/elastic/apm/elastic-apm-agent/1.48.0/elastic-apm-agent-1.48.0.jar

# Opção B (sem internet no runtime) — descomente estas duas linhas e comente a opção A acima
# COPY --from=build /root/.m2/repository/co/elastic/apm/elastic-apm-agent/1.48.0/elastic-apm-agent-1.48.0.jar elastic-apm-agent.jar

EXPOSE 8080

# Variáveis do lab (bypass verificação de certificado já habilitado)
ENV SERVER_PORT=8080
ENV APM_SERVER_URL=http://apm-server:8200
ENV APM_SERVICE_NAME=app-java-lab
ENV APM_ENVIRONMENT=lab
ENV APM_VERIFY_SERVER_CERT=false

ENTRYPOINT ["sh","-c","java -javaagent:/app/elastic-apm-agent.jar \
  -Delastic.apm.server_urls=${APM_SERVER_URL} \
  -Delastic.apm.service_name=${APM_SERVICE_NAME} \
  -Delastic.apm.environment=${APM_ENVIRONMENT} \
  -Delastic.apm.verify_server_cert=${APM_VERIFY_SERVER_CERT} \
  -jar app.jar --server.port=${SERVER_PORT} ${JAVA_OPTS}"]
