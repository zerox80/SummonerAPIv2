# syntax=docker/dockerfile:1.7
# ===== Stage 1: Frontend Build =====
FROM node:20-alpine AS frontend-build
WORKDIR /app

# Copy package files first for better caching
COPY package*.json ./
RUN npm install

# Copy source and build
COPY . .
RUN npm run build:docker

# Debug: list build output
RUN ls -la src/main/resources/static/

# ===== Stage 2: Backend Build =====
FROM maven:3.9-eclipse-temurin-21 AS backend-build
WORKDIR /app

# Allow toggling tests during build (default: skip tests for faster builds)
ARG SKIP_TESTS=true

# Use BuildKit cache for Maven repo to accelerate builds across runs
COPY pom.xml .
RUN mvn -q -T 5 -e -DskipTests dependency:go-offline

COPY src ./src

# Copy built frontend assets from static directory
COPY --from=frontend-build /app/src/main/resources/static/ ./src/main/resources/static/

RUN mvn -q -T 5 -DskipTests=${SKIP_TESTS} clean package

# ===== Stage 3: Runtime =====
FROM eclipse-temurin:21-jre
WORKDIR /app

ENV JAVA_TOOL_OPTIONS=""

COPY --from=backend-build /app/target/riot-api-spring*.jar app.jar

# Create non-root user and adjust ownership
RUN useradd -u 10001 -r -g root -s /usr/sbin/nologin app \
    && chown -R app:root /app

USER app

EXPOSE 8080
# Optional: HEALTHCHECK to probe app liveness via Actuator (uncomment and ensure wget/curl is available)
# HEALTHCHECK --interval=30s --timeout=3s --start-period=20s --retries=3 CMD wget -qO- http://127.0.0.1:8080/actuator/health | grep -q '"status":"UP"' || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
