# syntax=docker/dockerfile:1.7
# ===== Stage 1: Build =====
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Allow toggling tests during build (default: run tests for safety)
ARG SKIP_TESTS=false

# Use BuildKit cache for Maven repo to accelerate builds across runs
COPY pom.xml .
RUN mvn -q -T 5 -e -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -T 5 -DskipTests=${SKIP_TESTS} clean package

# ===== Stage 2: Runtime =====
FROM eclipse-temurin:21-jre
WORKDIR /app

ENV JAVA_TOOL_OPTIONS=""

COPY --from=build /app/target/riot-api-spring*.jar app.jar

# Create non-root user and adjust ownership
RUN useradd -u 10001 -r -g root -s /usr/sbin/nologin app \
    && chown -R app:root /app

USER app

EXPOSE 8080
# Optional: HEALTHCHECK to probe app liveness via Actuator (uncomment and ensure wget/curl is available)
# HEALTHCHECK --interval=30s --timeout=3s --start-period=20s --retries=3 CMD wget -qO- http://127.0.0.1:8080/actuator/health | grep -q '"status":"UP"' || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
