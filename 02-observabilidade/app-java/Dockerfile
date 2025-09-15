# ---------- BUILD (sem Maven) ----------
FROM eclipse-temurin:17-jdk AS build
WORKDIR /work

# Copia código-fonte simples (JDK puro)
COPY src ./src

# Compila as classes
RUN mkdir -p /app/classes && \
    find ./src -name "*.java" > sources.txt && \
    javac -d /app/classes @sources.txt

# Empacota em um JAR executável (main-class definida abaixo)
RUN mkdir -p /app && \
    jar --create --file /app/app.jar \
        --main-class com.observabilidade.lab.AppJavaApplication \
        -C /app/classes .

# ---------- RUNTIME ----------
FROM eclipse-temurin:17-jre
WORKDIR /app
ENV JAVA_OPTS=""

# App
COPY --from=build /app/app.jar app.jar

# APM agent (bypass CA no download)
RUN curl -fsSL -k -o elastic-apm-agent.jar \
  https://repo1.maven.org/maven2/co/elastic/apm/elastic-apm-agent/1.48.0/elastic-apm-agent-1.48.0.jar

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
  -jar app.jar ${JAVA_OPTS}"]
