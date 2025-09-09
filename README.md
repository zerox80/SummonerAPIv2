# SummonerAPI — Your Personal League Stats Tracker

SummonerAPI is a Spring Boot web app that uses the Riot Games API to fetch and display League of Legends summoner profiles, rankings, and recent match histories — all in a clean and simple web interface.

> Disclaimer: This project is not affiliated with or endorsed by Riot Games. League of Legends and Riot Games are trademarks or registered trademarks of Riot Games, Inc. This tool uses the Riot Games API in accordance with their Developer Terms of Service.

---

## Features

- Summoner lookup by Riot ID (e.g., `YourName#EUW`)
- Profile overview (level, icon)
- Ranked stats (tier, rank, wins/losses)
- Match history (recent games)
- LP history (optional via PostgreSQL)
- Lightweight UI with Thymeleaf

---

## Tech Stack

- Java 21
- Spring Boot 3.2.x
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

### 3) Build and Run

```bash
mvn clean package
java -jar target/riot-api-spring-2.0.jar
```

Open http://localhost:8080

---

## Actuator

Actuator is enabled. Exposed endpoints:
- `/actuator/health`
- `/actuator/info`

### Observability & Metrics

- The Riot API HTTP client records metrics via Micrometer:
  - `riotapi.client.requests{type,status}` – request counts by logical request type and status class (2xx/4xx/5xx/429/error)
  - `riotapi.client.latency{type,status,retries}` – end-to-end latency including retries
  - `riotapi.client.retries{type}` – retry counter per request type
- Note: By default, the security configuration allows only `/actuator/health` and `/actuator/info`. The `/actuator/metrics/*` endpoint is exposed by management but denied by the web security layer. For local debugging, temporarily permit metrics in `SecurityConfig` or use a separate management port.

---

## API Docs (Swagger UI)

- Swagger UI is available when running locally:
  - OpenAPI JSON: `/v3/api-docs`
  - Swagger UI: `/swagger-ui.html`
- Only public endpoints are documented (e.g., `/api/summoner-suggestions`, `/api/me`).

---

## Docker (Optional)

Build and run with Docker Compose:

```bash
docker compose up --build
```

This starts PostgreSQL and the app. Override environment variables in `docker-compose.yml` as needed.

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
