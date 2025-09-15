# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -e -B -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -e -B -DskipTests package

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app
ENV JAVA_OPTS=""
# Baixa o Elastic APM Java agent (para traces). Em caso de falha de CA, o agente usará verify_server_cert=false via env.
RUN curl -fsSL -k -o elastic-apm-agent.jar https://repo1.maven.org/maven2/co/elastic/apm/elastic-apm-agent/1.48.0/elastic-apm-agent-1.48.0.jar
COPY --from=build /app/target/app-java-*.jar app.jar
EXPOSE 8080
ENV SERVER_PORT=8080
ENV APM_SERVER_URL=http://apm-server:8200
ENV APM_SERVICE_NAME=app-java-lab
ENV APM_ENVIRONMENT=lab
ENV APM_VERIFY_SERVER_CERT=false
ENTRYPOINT ["sh","-c","java -javaagent:/app/elastic-apm-agent.jar   -Delastic.apm.server_urls=${APM_SERVER_URL}   -Delastic.apm.service_name=${APM_SERVICE_NAME}   -Delastic.apm.environment=${APM_ENVIRONMENT}   -Delastic.apm.verify_server_cert=${APM_VERIFY_SERVER_CERT}   -jar app.jar --server.port=${SERVER_PORT} ${JAVA_OPTS}"]
