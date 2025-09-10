# ===== Stage 1: Build =====
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -q -e -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -DskipTests clean package

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
