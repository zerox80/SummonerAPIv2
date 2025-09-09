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

ENV JAVA_OPTS=""

COPY --from=build /app/target/riot-api-spring-2.0.jar app.jar

# Create non-root user and adjust ownership
RUN useradd -u 10001 -r -g root -s /usr/sbin/nologin app \
    && chown -R app:root /app

USER app

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
