# SummonerAPI — Your Personal League Stats Tracker

SummonerAPI is a Spring Boot web app that uses the Riot Games API to fetch and display League of Legends summoner profiles, rankings, and recent match histories — all in a clean and simple web interface.

> Disclaimer: This project is not affiliated with or endorsed by Riot Games. League of Legends and Riot Games are trademarks or registered trademarks of Riot Games, Inc. This tool uses the Riot Games API in accordance with their Developer Terms of Service.

---

## Quality & Coverage

- Locally generate reports:
  ```bash
  mvn verify
  ```
  Reports:
  - JaCoCo coverage: `target/site/jacoco/index.html`
  - Checkstyle: `target/site/checkstyle.html`
  - PMD: `target/site/pmd.html`
  - SpotBugs: `target/site/spotbugs.html`

- In CI (GitHub Actions), test reports and coverage are uploaded as artifacts:
  - `surefire-reports`, `jacoco-report`, and `analysis-site`.

---

## Table of Contents

- Features
- Tech Stack
- Getting Started
  - Requirements
  - Configure (properties, environment, .env)
  - Build and Run
- Configuration Reference
- API Docs (Swagger UI)
- HTTP API (Endpoints & Examples)
- Actuator & Observability
- Quality & Coverage
- Docker
- Security
- Rate Limiting
- Caching & Concurrency
- Project Structure
- NGINX Reverse Proxy (Optional)
- Troubleshooting
- Contributing
- License
- Attribution
- Privacy
- Notes

## Features

- Summoner lookup by Riot ID (e.g., `YourName#EUW`)
- Profile overview (level, icon, profile icon from CommunityDragon)
- Ranked stats (tier, rank, wins/losses)
- Match history (recent games)
- Champions directory and detail pages with images and localized text
- Optional LP history via PostgreSQL (with Flyway support)
- Basic champion build aggregation API (sampling recent ranked matches)
- Lightweight UI with Thymeleaf and Bootstrap 5

---

## Tech Stack

- Java 21
- Spring Boot 3.3.x
- Maven
- Thymeleaf
- Lombok
- Caffeine (cache)
- PostgreSQL + Spring Data JPA (LP history)

---

## Getting Started

### 1) Requirements

- JDK 21+
- Maven
- Riot API key
- PostgreSQL (optional, for LP history)

### 2) Configure

Option A — properties file:
- Copy `src/main/resources/application-example.properties` to `src/main/resources/application.properties`.
- Fill in your values (at minimum `riot.api.key`).

Option B — environment variables (recommended):
- `RIOT_API_KEY`, `RIOT_API_REGION`, `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`

Option C — .env file (for Docker Compose):
- Copy `.env.example` to `.env` and fill in values. `docker-compose.yml` will pick these up.

### Get a Riot API Key

1. Create a developer account and register an application at https://developer.riotgames.com/
2. Use your temporary development key for testing. Note: dev keys expire frequently and are rate limited.
3. Never commit your API key to version control. Prefer environment variables or a local `application.properties`.

### 3) Build and Run (Local)

```bash
mvn clean package
java -jar target/riot-api-spring-2.0.jar
```

Open http://localhost:8080

Alternative (dev):

```bash
mvn spring-boot:run
```

---

## Configuration Reference

- Riot API
  - `riot.api.key` — your Riot API key (or env `RIOT_API_KEY`)
  - `riot.api.region` — platform region (e.g., `euw1`, `na1`, `kr`). Used for platform routes and to derive the regional route for Match V5.
  - `riot.api.community-dragon.url` — base for profile icons (default points to CommunityDragon)
  - `riot.api.max-concurrent` — max concurrent outbound requests to Riot API (default 15)

- Database
  - `spring.datasource.url`, `spring.datasource.username`, `spring.datasource.password`
  - JPA: `spring.jpa.hibernate.ddl-auto` (use `validate`/`none` when Flyway manages the schema; only fall back to `create`/`update` for disposable dev environments)

- Champion Build Aggregation
  - `build.agg.enabled`, `build.agg.queue-id`, `build.agg.pages`, `build.agg.matches-per-summoner`, `build.agg.max-summoners`, `build.agg.champions`, `build.agg.cron`, `build.agg.trigger-enabled`

- Rate Limiting
  - See the dedicated section below for `rate.limit.*` keys

- UI / Match History
  - `ui.matches.page-size`, `ui.matches.max-page-size`, `ui.matches.max-start-offset` — keep `/api/matches` pagination within safe bounds

### Minimal application.properties (example)

```properties
# Riot API
riot.api.key=REPLACE_WITH_YOUR_RIOT_API_KEY
riot.api.region=euw1

# Server
server.port=8080

# Optional: Database (for LP history)
spring.datasource.url=jdbc:postgresql://localhost:5432/summoner_db
spring.datasource.username=postgres
spring.datasource.password=postgres

# Caching
spring.cache.type=caffeine

# Actuator
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.probes.enabled=true

# Rate limiting
rate.limit.enabled=true
rate.limit.window-ms=60000
rate.limit.max-requests=60
rate.limit.paths=/api/**,/search
```

### Environment variables mapping

- `RIOT_API_KEY` → `riot.api.key`
- `RIOT_API_REGION` → `riot.api.region`
- `RIOT_API_COMMUNITY_DRAGON_URL` → `riot.api.community-dragon.url`
- `RIOT_API_MAX_CONCURRENT` → `riot.api.max-concurrent`
- `SPRING_DATASOURCE_URL` → `spring.datasource.url`
- `SPRING_DATASOURCE_USERNAME` → `spring.datasource.username`
- `SPRING_DATASOURCE_PASSWORD` → `spring.datasource.password`
- `SPRING_FLYWAY_BASELINE_ON_MIGRATE` → `spring.flyway.baselineOnMigrate`
- `SPRING_FLYWAY_BASELINE_VERSION` → `spring.flyway.baselineVersion`
- `SPRING_JPA_HIBERNATE_DDL_AUTO` → `spring.jpa.hibernate.ddl-auto`
- `BUILD_AGG_*` → `build.agg.*`
- `RATE_LIMIT_*` → `rate.limit.*`
- `UI_MATCHES_PAGE_SIZE` → `ui.matches.page-size`
- `UI_MATCHES_MAX_PAGE_SIZE` → `ui.matches.max-page-size`
- `UI_MATCHES_MAX_START_OFFSET` → `ui.matches.max-start-offset`

### Supported platform regions and regional routing (Match V5)

Match V5 uses a regional route derived from your platform region:

```
Platform → Regional Route
- euw1, eun1, tr1, ru, me1 → europe
- na1, br1, la1, la2, oc1   → americas
- kr, jp1                   → asia
- vn2, ph2, sg2, th2, tw2, id1 → sea
```
If an unknown platform is used, the client will default to using the platform as-is.

---

## Actuator & Observability

Actuator is enabled. Exposed endpoints:
- `/actuator/health`
- `/actuator/info`

### Observability & Metrics

- The Riot API HTTP client records metrics via Micrometer:
  - `riotapi.client.requests{type,status}` – request counts by logical request type and status class (2xx/4xx/5xx/429/error)
  - `riotapi.client.latency{type,status,retries}` – end-to-end latency including retries
  - `riotapi.client.retries{type}` – retry counter per request type
- Note: By default, the security configuration allows only `/actuator/health` and `/actuator/info`. The `/actuator/metrics/*` endpoint is exposed by management but denied by the web security layer. For local debugging, temporarily permit metrics in `SecurityConfig` or use a separate management port.
 - Build info: When the Spring Boot Maven Plugin goal `build-info` is enabled, `GET /actuator/info` includes build metadata (name, version, time, git) for easier troubleshooting and deployments.

### Access Logging & Correlation

- Each request is assigned/propagated a correlation ID via `X-Request-Id` and exposed in logs and responses.
  - If the header is provided by the client, it is sanitized to `[A-Za-z0-9_.-]` and truncated to 64 chars.
  - Otherwise a UUID is generated. See `RequestIdFilter` and tests in `src/test/java/.../RequestIdFilterTest.java`.
- An access log is emitted for every request with method, path, status, duration, client IP and sanitized user agent. See `AccessLogFilter`.
  - Log levels: INFO for <400, WARN for 4xx, ERROR for 5xx.
  - To reduce noise, tune `logging.level.com.zerox80.riotapi.config.AccessLogFilter` (e.g., to `WARN`).
- MDC propagation across async tasks and virtual threads ensures the `requestId` appears in logs from the Java `HttpClient` and `@Async` executors.
  - See `MdcPropagatingExecutor` and `AsyncConfig` task decorator.


---

## API Docs (Swagger UI)

- Swagger UI is available when running locally:
  - OpenAPI JSON: `/v3/api-docs`
  - Swagger UI: `/swagger-ui/index.html`
  - Only public endpoints are documented (e.g., `/api/summoner-suggestions`, `/api/me`).

---

## HTTP API (Endpoints & Examples)

- GET `/` — Home page
- GET or POST `/search?riotId=GameName#TAG` — Render profile, ranked, and recent matches
- GET `/api/summoner-suggestions?query=<text>` — Suggestions from local history and service
  - Example:
    ```bash
    curl 'http://localhost:8080/api/summoner-suggestions?query=ahri'
    ```
- GET `/api/me` — Resolve the current summoner via RSO Bearer token
  - Requires `Authorization: Bearer <token>` header
    ```bash
    curl -H 'Authorization: Bearer YOUR_RSO_TOKEN' http://localhost:8080/api/me
    ```
- GET `/champions` — Champions listing page (HTML)
- GET `/champions/{id}` — Champion detail page (HTML)
- GET `/api/champions` — Champions list (JSON)
  ```bash
  curl http://localhost:8080/api/champions
  ```
- GET `/api/champions/{id}` — Champion detail (JSON)
  ```bash
  curl http://localhost:8080/api/champions/Ahri
  ```
- GET `/api/champions/{id}/build` — Aggregated build for a champion
  - Optional params: `queueId` (e.g., 420), `role` (e.g., MID)
  ```bash
  curl 'http://localhost:8080/api/champions/Ahri/build?queueId=420&role=MID'
  ```
- POST `/api/champions/{id}/aggregate` — Trigger aggregation (requires `build.agg.trigger-enabled=true`)
  - Optional params: `queueId`, `pages`, `matchesPerSummoner`, `maxSummoners`
  ```bash
  curl -X POST 'http://localhost:8080/api/champions/Ahri/aggregate?queueId=420&pages=1&matchesPerSummoner=8&maxSummoners=75'
  ```

---

## Docker (Optional)

Build and run with Docker Compose:

```bash
docker compose up --build
```

This starts PostgreSQL and the app. Override environment variables in `docker-compose.yml` as needed.
Docker builds now run the full Maven test suite by default; if you need a faster inner-loop build (not recommended for production images), pass `--build-arg SKIP_TESTS=true` in your Compose `build.args` section.
Tip: You can adjust the host port via `.env` using `APP_HTTP_PORT`, e.g., `APP_HTTP_PORT=8081`.

Production profile:
- To enable production hardening (see `src/main/resources/application-prod.properties`), set `SPRING_PROFILES_ACTIVE=prod` in your `.env` before starting Compose.
  - Example `.env` snippet:
    ```env
    APP_HTTP_PORT=8081
    SPRING_PROFILES_ACTIVE=prod
    ```

Runtime notes:
- The container now uses exec-form ENTRYPOINT (`["java","-jar","app.jar"]`). Prefer passing JVM options via `JAVA_TOOL_OPTIONS` instead of shell-expanding `JAVA_OPTS`.

Run with plain Docker (without Compose):

```bash
docker build -t summonerapi:local .
docker run --rm \
  -p 8080:8080 \
  -e RIOT_API_KEY=YOUR_RIOT_API_KEY \
  -e RIOT_API_REGION=euw1 \
  -e SPRING_PROFILES_ACTIVE=prod \
  summonerapi:local
```

> By default `docker build` runs unit tests (`SKIP_TESTS=false`). For experimental builds you can opt out via `docker build --build-arg SKIP_TESTS=true -t summonerapi:fast .`, but always let CI run with tests enabled.

---

## Security

- CSRF protection enabled via cookie-based token (`CookieCsrfTokenRepository`).
- Dynamic Content Security Policy (CSP) nonce is added per request to harden inline scripts.
- Actuator hardening: only `/actuator/health` and `/actuator/info` are permitted; everything else under `/actuator/**` is denied by web security.
- A lightweight IP-based rate limiter protects `/api/**` and `/search` (see Rate Limiting below).
- CSRF exemption for Aggregation: The mutating endpoint `POST /api/champions/{id}/aggregate` is exempt from CSRF to allow non-browser clients (curl/Swagger). Other mutating endpoints (if added) require sending the CSRF token header `X-XSRF-TOKEN`.

### Server-side HTML Sanitizing for tooltips

- Tooltips and passive descriptions from DDragon/CDragon are sanitized server-side using OWASP Java HTML Sanitizer.
- Only a conservative set of inline formatting tags and known game-specific tags (e.g., `<magicDamage>`, `<physicalDamage>`, `<trueDamage>`, `<status>`, `<br>`) are allowed, without attributes. This is an additional layer to the strict CSP.

Behind a reverse proxy (NGINX/Ingress):

- Set `rate.limit.trust-proxy=true` (or env `RATE_LIMIT_TRUST_PROXY=true`) so client IP is resolved from `Forwarded`/`X-Forwarded-For`.
- `ForwardedHeaderFilter` is registered to apply forwarded headers to requests.
- Access logging honors the same proxy trust rules: when `rate.limit.trust-proxy=true` (and optionally `rate.limit.allowed-proxies` is set), the `AccessLogFilter` logs the left-most client IP from `Forwarded`/`X-Forwarded-For`.

### Error Handling & Request Correlation

- API-Fehler folgen dem RFC 7807 "Problem Details"-Format (`ProblemDetail`). Zusätzliche Felder:
  - `requestId` – eindeutige Korrelations-ID (auch als Response-Header `X-Request-Id` gesetzt und in Logs enthalten)
  - `path` – angefragter Pfad
- Upstream-Fehler der Riot API werden als `502/5xx` o.ä. mit Kontext gemeldet.
- Referenz: [Spring MVC ProblemDetails](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-ann-rest-exceptions.html)

### Produktionsprofil

- `application-prod.properties` deaktiviert die API-Dokumentation in Prod:
  - `springdoc.api-docs.enabled=false`
  - `springdoc.swagger-ui.enabled=false`
- Aktivieren via: `--spring.profiles.active=prod`

---

## Rate Limiting

- A lightweight fixed-window, IP-based limiter protects `/api/*` and `/search`.
- Default settings (can be overridden via `application.properties` or environment):
  - `rate.limit.enabled=true`
  - `rate.limit.window-ms=60000`
  - `rate.limit.max-requests=60`
  - `rate.limit.paths=/api/**,/search` (comma-separated Ant-style patterns)
- Environment variable equivalents (see `.env.example`):
  - `RATE_LIMIT_ENABLED`, `RATE_LIMIT_WINDOW_MS`, `RATE_LIMIT_MAX_REQUESTS`
  - You can also set `RATE_LIMIT_PATHS` if you prefer env vars for path patterns
  - Additional options:
    - `RATE_LIMIT_TRUST_PROXY` — trust `Forwarded`/`X-Forwarded-For` for client IP (behind NGINX/Ingress)
    - `RATE_LIMIT_INCLUDE_HEADERS` — include `X-RateLimit-*` headers in responses (default true)
    - `RATE_LIMIT_CACHE_MAX_IPS` — cap distinct client windows kept in memory (default 100000)
    - `RATE_LIMIT_ALLOWED_PROXIES` — comma-separated IP allowlist; only when the incoming remote address is in this list are proxy headers trusted

---

## Caching & Concurrency

- Caching
  - In-memory caches backed by Caffeine via Spring Cache (`spring.cache.type=caffeine`).
  - Cached domains include: `accounts`, `summoners`, `leagueEntries`, `matchIds`, `matchDetails` (see `RiotApiClient`).
  - Keys are derived from method parameters (e.g., PUUID, matchId). This reduces upstream calls and accelerates page loads.

- In-flight request coalescing
  - `RiotApiClient` coalesces identical concurrent requests to avoid duplicate upstream calls on cache misses.

- Outbound concurrency limit
  - Controlled by `riot.api.max-concurrent` (default 15). A semaphore limits concurrent HTTP requests to Riot and helps respect rate limits.

---

## Project Structure

- `controller/` — Web endpoints and views
- `service/` — Business logic
- `client/` — Riot API HTTP client
- `model/` — DTOs and entities
- `repository/` — JPA repositories
- `config/` — Cache and app configuration
- `templates/` — Thymeleaf pages

---

## NGINX Reverse Proxy (Optional)

- A production-ready sample config is provided at `nginx/summonerapi.conf.example`.
- It handles HTTP→HTTPS redirect, ACME challenges, TLS, basic security headers, and proxy forwarding.
- After adapting the domain and port, test and reload NGINX:
  ```bash
  sudo nginx -t && sudo systemctl reload nginx
  ```

---

## Troubleshooting

- Build fails due to Java version: Ensure JDK 21+ is installed and `JAVA_HOME` points to it.
- 401 on `/api/me`: Make sure you are sending a valid RSO bearer token: `Authorization: Bearer <token>`.
- 429 responses from Riot API: The client auto-retries with backoff. Consider lowering `riot.api.max-concurrent` or spacing requests.
- Postgres connection errors: Verify `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`. With Docker Compose, the defaults should work.
- Actuator metrics not visible: They are exposed by management but denied by web security by default. Temporarily permit in `SecurityConfig` for local debugging.
- IDE build/Lombok: Install the Lombok plugin in your IDE and enable "Annotation Processing" (IntelliJ IDEA: Settings > Build, Execution, Deployment > Compiler > Annotation Processors).

## Contributing

1. Fork the repo
2. Create a branch: `git checkout -b feature/NewFeature`
3. Commit: `git commit -m "Add NewFeature"`
4. Push: `git push origin feature/NewFeature`
5. Open a Pull Request

---

## License

GPL v3 — see `LICENSE`.

---

## Attribution

- This project is not affiliated with or endorsed by Riot Games. League of Legends and Riot Games are trademarks or registered trademarks of Riot Games, Inc.
- Profile icons and some static data are fetched at runtime from CommunityDragon: https://raw.communitydragon.org (see `riot.api.community-dragon.url`).
- UI framework uses Bootswatch themes (Darkly/Flatly) and Bootstrap 5.
- Icons by Font Awesome.

---

## Privacy

- The web UI stores a small search history locally (in a cookie and/or localStorage) to provide suggestions. No account credentials are stored.
- The server may persist League Points (LP) snapshots for your PUUID to compute LP deltas across matches. If you want your LP history removed, please open an issue or contact the maintainer.
- Logs may include masked identifiers (e.g., partial PUUID) for troubleshooting. Full PUUIDs are not logged.
- This tool is intended for personal, educational use. It is not designed for scraping or bulk data collection. Please respect Riot's rate limits and Developer Terms.

---

## Notes

- Secrets are never logged. Configure via env vars or `application.properties`.
- The Riot API has strict rate limits. The client includes basic retry/backoff for 429/5xx.

UI & Misc updates (Sept 2025):
- Dynamic region indicator in the navbar reflects the configured platform region (e.g., `euw1`).
- Server-side sanitizing for champion tooltips and passive descriptions to complement CSP.
- Access logging is proxy-aware when `rate.limit.trust-proxy` is enabled.

Dependency updates (Sept 2025):
- Spring Boot parent: 3.3.5
- springdoc-openapi-starter-webmvc-ui: 2.6.0
- PostgreSQL JDBC driver: 42.7.7
