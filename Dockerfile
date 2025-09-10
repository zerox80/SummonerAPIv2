# syntax=docker/dockerfile:1.7
# ===== Stage 1: Build =====
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Use BuildKit cache for Maven repo to accelerate builds across runs
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 \
    mvn -q -T 5 -e -DskipTests dependency:go-offline

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -q -T 5 -DskipTests clean package

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
ENTRYPOINT ["java", "-jar", "app.jar"]
