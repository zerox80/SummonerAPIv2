<div align="center">

<img width="1203" height="851" alt="image" src="https://github.com/user-attachments/assets/e893494c-886f-49be-948f-d387ac8602a3" />

**A modern, high-performance League of Legends statistics and analytics platform**

[![CI](https://github.com/zerox80/SummonerAPIv2/actions/workflows/ci.yml/badge.svg)](https://github.com/zerox80/SummonerAPIv2/actions/workflows/ci.yml)
[![CodeQL](https://github.com/zerox80/SummonerAPIv2/actions/workflows/codeql.yml/badge.svg)](https://github.com/zerox80/SummonerAPIv2/actions/workflows/codeql.yml)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](LICENSE)
[![Java Version](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18.3.1-61DAFB.svg?logo=react)](https://react.dev)

[Features](#-features) •
[Quick Start](#-quick-start) •
[Documentation](#-documentation) •
[API](#-api-documentation) •
[Contributing](#-contributing)

</div>

---

## 📖 Overview

SummonerAPI v2 is a full-stack web application that provides comprehensive League of Legends player statistics, match history, champion analytics, and live build aggregation. Built with modern technologies and best practices, it offers a fast, responsive, and intuitive interface for analyzing player performance.

> **⚠️ LEGAL NOTICE**  
> **SummonerAPI v2** was created under Riot Games' ["Legal Jibber Jabber"](https://www.riotgames.com/en/legal) policy using assets owned by Riot Games. **Riot Games does not endorse or sponsor this project.**
>
> This project is **not affiliated with, endorsed by, or sponsored by Riot Games**. Riot Games and all associated properties are trademarks or registered trademarks of Riot Games, Inc.  
> 
> **You must have your own Riot Games API key and comply with their [Terms of Service](https://developer.riotgames.com/terms) to use this application.**

### Why SummonerAPI v2?

- **⚡ Lightning Fast**: Powered by Spring Boot 3.5, React 18, and Vite for optimal performance
- **🎯 Real-time Data**: Direct integration with Riot Games API for live match data
- **📊 Advanced Analytics**: Champion builds, win rates, match history, and performance metrics
- **🔒 Production Ready**: Built-in rate limiting, caching, security, and monitoring
- **🐳 Docker Native**: One-command deployment with Docker Compose
- **📱 Responsive Design**: Beautiful UI that works seamlessly on all devices
- **🌍 Multi-region**: Support for all League of Legends regions
- **🔄 Auto-updates**: Scheduled champion build aggregation from high-level gameplay

---

## ✨ Features

### 🎮 Core Features

- **Summoner Lookup**: Search and view detailed summoner profiles across all regions
- **Match History**: Comprehensive match history with detailed statistics and performance metrics
- **Champion Statistics**: In-depth champion analytics including win rates, KDA, and play rates
- **Live Builds**: Automated aggregation of champion builds from high-ELO matches
- **Ranked Overview**: Current season rankings, LP gains, and ranked progression
- **Profile Icons**: Dynamic profile icon display with Community Dragon integration

### 🛠️ Technical Features

- **Intelligent Caching**: Multi-layer caching with Caffeine for reduced API calls and faster responses
- **Rate Limiting**: Configurable per-IP rate limiting with customizable windows and thresholds
- **Database Persistence**: PostgreSQL with Flyway migrations for schema management
- **API Documentation**: Interactive Swagger/OpenAPI documentation
- **Health Monitoring**: Spring Boot Actuator endpoints for application health
- **Security**: Spring Security integration with CSRF protection and secure headers
- **Code Quality**: Integrated Checkstyle, PMD, SpotBugs, and JaCoCo for code quality assurance
- **Comprehensive Testing**: Unit and integration tests with Vitest for frontend, JUnit for backend

---

## 🏗️ Tech Stack

### Backend

| Technology | Version | Purpose |
|-----------|---------|---------|
| **Java** | 21 | Programming Language |
| **Spring Boot** | 3.5.6 | Application Framework |
| **Spring Security** | 3.x | Authentication & Authorization |
| **Spring Data JPA** | 3.x | Data Access Layer |
| **PostgreSQL** | 16 | Primary Database |
| **Flyway** | 11.13.2 | Database Migrations |
| **Caffeine** | 3.2.2 | In-Memory Caching |
| **Lombok** | 1.18.42 | Boilerplate Reduction |
| **SpringDoc OpenAPI** | 2.8.13 | API Documentation |

### Frontend

| Technology | Version | Purpose |
|-----------|---------|---------|
| **React** | 18.3.1 | UI Framework |
| **Vite** | 5.2.8 | Build Tool |
| **React Router** | 6.28.0 | Client-side Routing |
| **TanStack Query** | 5.51.21 | Data Fetching & Caching |
| **Day.js** | 1.11.11 | Date Manipulation |
| **Vitest** | 1.3.1 | Testing Framework |
| **ESLint** | 8.57.0 | Code Linting |

### DevOps

- **Docker** & **Docker Compose** - Containerization
- **Maven** - Dependency Management & Build Tool
- **GitHub Actions** - CI/CD Pipeline
- **Nginx** - Reverse Proxy (optional)

---

## 🚀 Quick Start

### Prerequisites

- **Java 21** or higher ([Eclipse Temurin](https://adoptium.net/) recommended)
- **Node.js 18+** and **npm 9+**
- **Docker** and **Docker Compose** (for containerized deployment)
- **PostgreSQL 16** (if running without Docker)
- **Riot Games API Key** ([Get one here](https://developer.riotgames.com/))
  - ⚠️ **Required**: You must have your own API key to use this application
  - ⚠️ **Never commit your API key** to version control or share it publicly
  - ⚠️ Read and comply with [Riot's API Terms of Service](https://developer.riotgames.com/terms)

### 🐳 Docker Deployment (Recommended)

The fastest way to get started:

```bash
# 1. Clone the repository
git clone https://github.com/zerox80/SummonerAPIv2.git
cd SummonerAPIv2

# 2. Configure environment variables
cp .env.example .env
# Edit .env and add your RIOT_API_KEY

# 3. Start the application
docker-compose up -d

# 4. Access the application
# Frontend: http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html
```

### 💻 Local Development Setup

For development with hot-reload:

```bash
# 1. Clone and configure
git clone https://github.com/zerox80/SummonerAPIv2.git
cd SummonerAPIv2
cp .env.example .env
# Edit .env with your settings

# 2. Start PostgreSQL (via Docker or local install)
docker run -d \
  --name summoner-db \
  -e POSTGRES_DB=summoner_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:16

# 3. Build and run backend
./mvnw clean install
./mvnw spring-boot:run

# 4. In another terminal, run frontend dev server
npm install
npm run dev

# Frontend dev server: http://localhost:5173 (with proxy to :8080)
# Backend API: http://localhost:8080
```

---

## ⚙️ Configuration

### Environment Variables

The application is configured via environment variables defined in `.env`. Copy `.env.example` to `.env` and customize:

#### Required Configuration

```bash
# Your Riot Games API key (REQUIRED)
RIOT_API_KEY=YOUR_RIOT_API_KEY

# Primary region for API calls
RIOT_API_REGION=euw1
```

#### Database Configuration

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/summoner_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
```

#### Rate Limiting

```bash
RATE_LIMIT_ENABLED=true              # Enable/disable rate limiting
RATE_LIMIT_WINDOW_MS=60000           # Window size (60 seconds)
RATE_LIMIT_MAX_REQUESTS=60           # Max requests per window per IP
RATE_LIMIT_PATHS=/api/**,/search     # Protected paths
RATE_LIMIT_TRUST_PROXY=false         # Trust X-Forwarded-For headers
RATE_LIMIT_INCLUDE_HEADERS=true      # Include X-RateLimit-* headers
```

#### Build Aggregation

```bash
BUILD_AGG_ENABLED=false              # Enable scheduled build aggregation
BUILD_AGG_QUEUE_ID=420               # Queue ID (420 = Ranked Solo/Duo)
BUILD_AGG_PAGES=1                    # Number of pages to fetch
BUILD_AGG_MATCHES_PER_SUMMONER=6     # Matches per summoner to analyze
BUILD_AGG_MAX_SUMMONERS=50           # Max summoners to fetch
BUILD_AGG_CHAMPIONS=                 # Comma-separated champion IDs
BUILD_AGG_CRON=0 15 3 * * *          # Cron schedule (default: 3:15 AM UTC)
BUILD_AGG_TRIGGER_ENABLED=false      # Allow manual POST triggers
```

#### Application Settings

```bash
APP_HTTP_PORT=8080                   # HTTP port for the application
SPRING_PROFILES_ACTIVE=prod          # Spring profile (use 'prod' for production)
DDRAGON_DEFAULT_LOCALE=de_DE         # Default locale for Data Dragon
```

### Advanced Configuration

For production deployments, additional configuration is available in:
- `src/main/resources/application.properties` - Base configuration
- `src/main/resources/application-prod.properties` - Production overrides

---

## 🧭 New Developer Guide

1. **Understand the system**
   - Revisit the [Overview](#-overview) and [Features](#-features) sections to internalize the product vision.
   - Skim the [Project Structure](#-documentation) tree so you know where backend services, the React frontend, scripts, and infrastructure live.
2. **Configure your environment**
   - Install the prerequisites from [Quick Start](#-quick-start) (Java 21, Node 18+, Docker, PostgreSQL, Riot API key).
   - Copy `.env.example` → `.env`, then set `RIOT_API_KEY`, database credentials, and any overrides such as `APP_HTTP_PORT` before starting services.
   - If you use a local PostgreSQL instance, keep the `SPRING_DATASOURCE_*` values aligned with your container or local install.
3. **Run the stack**
   - Backend (hot reload):
     ```bash
     ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
     ```
   - Frontend (Vite dev server):
     ```bash
     cd frontend
     npm install
     npm run dev
     ```
   - Full Docker stack:
     ```bash
     docker compose up --build
     ```
4. **Use the application**
   - Web UI: open `http://localhost:5173`, search for a Riot ID such as `Faker#KR1`, and navigate the Summoner → Match History → Champions flows.
   - REST API: visit `http://localhost:8080/swagger-ui.html` to explore `/api/profile`, `/api/matches`, and `/api/champions`.
   - Health checks: run `curl http://localhost:8080/actuator/health` to confirm the backend is up.
5. **Develop with confidence**
   - Backend tests: `./mvnw verify`
   - Frontend unit tests: `cd frontend && npm run test`
   - Linting/formatting: `npm run lint` and (if enabled) `./mvnw spotless:apply`
6. **Troubleshoot & observe**
   - Tail backend logs with `./mvnw spring-boot:run -Dspring-boot.run.arguments=--logging.level.root=DEBUG`
   - Inspect metrics via `http://localhost:8080/actuator/metrics` and rate-limit headers on API responses.
   - Toggle scheduled build aggregation through the `build.agg.*` environment variables documented above.

Following these steps takes a new contributor from clone → configuration → running stack → productive development in minutes.

---

## 📚 Documentation

### Project Structure

```
SummonerAPIv2/
├── .github/workflows/     # CI/CD pipelines
├── config/               # Build tools configuration
│   └── checkstyle/      # Code style rules
├── frontend/            # React frontend application
│   └── src/
│       ├── api/         # API client & queries
│       ├── components/  # Reusable React components
│       ├── data/        # Static data & translations
│       ├── hooks/       # Custom React hooks
│       ├── layouts/     # Page layouts
│       ├── pages/       # Route pages
│       ├── providers/   # Context providers
│       ├── sections/    # Page sections
│       ├── styles/      # CSS stylesheets
│       └── utils/       # Utility functions
├── nginx/               # Nginx configuration examples
├── scripts/             # Build and utility scripts
├── src/
│   ├── main/
│   │   ├── java/        # Java source code
│   │   │   └── com/zerox80/riotapi/
│   │   │       ├── config/       # Spring configuration
│   │   │       ├── controller/   # REST controllers
│   │   │       ├── dto/          # Data transfer objects
│   │   │       ├── entity/       # JPA entities
│   │   │       ├── exception/    # Custom exceptions
│   │   │       ├── repository/   # Data repositories
│   │   │       ├── scheduler/    # Scheduled tasks
│   │   │       ├── security/     # Security configuration
│   │   │       ├── service/      # Business logic
│   │   │       └── util/         # Utility classes
│   │   └── resources/
│   │       ├── db/migration/     # Flyway SQL migrations
│   │       ├── static/           # Built frontend assets
│   │       └── templates/        # Thymeleaf templates
│   └── test/            # Backend tests
├── docker-compose.yml   # Docker Compose configuration
├── Dockerfile          # Multi-stage Docker build
├── pom.xml            # Maven dependencies
└── package.json       # NPM dependencies
```

### Code Documentation

This project includes **comprehensive code documentation** with 100% coverage of all public APIs, classes, methods, and functions. Every public interface is thoroughly documented to help developers understand, use, and contribute to the codebase effectively.

#### Backend Documentation (Java)

All Java classes, methods, and functions include detailed JavaDoc documentation following the official JavaDoc standard:

**Complete Coverage:**
- **Every public class** has comprehensive class-level documentation explaining purpose, key features, and usage examples
- **Every public method** includes detailed parameter descriptions, return value documentation, and behavior explanations
- **All configuration classes** document Spring Boot configurations, their purposes, and setup requirements
- **All API endpoints** document request/response formats, error handling, and usage examples
- **All service layer implementations** explain business logic, algorithms, and data processing flows
- **All JPA entities** document relationship mappings, constraints, and database schema details
- **All repository interfaces** document query methods, custom implementations, and data access patterns

**Key Documented Components:**
- `config/` - Spring configuration classes with detailed setup explanations and environment variable documentation
- `controller/` - REST API endpoints with complete request/response documentation and error scenarios
- `service/` - Business logic with algorithm explanations, data processing flows, and performance considerations
- `entity/` - JPA entities with relationship mappings, constraints, and database field documentation
- `repository/` - Data access layer with query methods, custom implementations, and caching strategies
- `dto/` - Data transfer objects with field descriptions, validation rules, and usage contexts
- `model/` - Domain models with comprehensive field documentation and business meaning

**Documentation Standards:**
- **JavaDoc format**: Official JavaDoc syntax with `@param`, `@return`, `@throws`, and `@see` tags
- **Comprehensive examples**: Real-world usage examples for complex APIs
- **Performance notes**: Important performance considerations and optimization guidance
- **Error handling**: Complete documentation of exceptions and error conditions
- **Version information**: `@since` and `@version` tags for API compatibility tracking

#### Frontend Documentation (JavaScript/JSX)

All JavaScript modules, functions, React hooks, and JSX components include comprehensive JSDoc documentation:

**Complete Coverage:**
- **Every module** has detailed module-level documentation explaining purpose, features, and usage patterns
- **Every exported function** includes parameter documentation, return value descriptions, and practical examples
- **All React hooks** document state management patterns, dependencies, and usage guidelines
- **All utility functions** explain input validation, edge cases, and performance considerations
- **All React components** document props, default values, and usage examples
- **API client methods** document request formats, error handling, and response structures

**Key Documented Components:**
- `api/client.js` - HTTP client with comprehensive request/response handling and error documentation
- `hooks/` - Custom React hooks with detailed state management patterns and dependency explanations
- `utils/` - Utility functions with complete formatting logic, validation rules, and edge case handling
- `data/` - Static data structures and curated content with field documentation and usage contexts
- `components/` - React components with complete prop documentation, examples, and accessibility features

**Documentation Standards:**
- **JSDoc format**: Standard JSDoc syntax with `@param`, `@returns`, `@example`, and `@typedef` tags
- **Type safety**: Complete TypeScript-style type annotations for better IDE support
- **Practical examples**: Real-world usage examples demonstrating common patterns
- **Accessibility**: Documentation of accessibility features and ARIA attributes
- **Performance**: Performance considerations and optimization notes for critical functions

#### Python Documentation

All Python scripts and functions include comprehensive Google Style docstrings:

**Complete Coverage:**
- **Every module** has detailed module-level documentation explaining purpose and features
- **Every function** includes comprehensive parameter documentation, return value descriptions, and usage examples
- **All utility functions** explain error handling, edge cases, and performance considerations
- **Script entry points** document command-line usage, arguments, and configuration options

**Key Documented Components:**
- `scripts/install_maven.py` - Installation utility with complete argument documentation and error handling

**Documentation Standards:**
- **Google Style**: Consistent Google Python Style Guide format
- **Complete examples**: Command-line usage examples and configuration scenarios
- **Error handling**: Documentation of exceptions and error conditions
- **Platform considerations**: Cross-platform compatibility notes and requirements

### Generating Documentation

#### Java Documentation

```bash
# Generate JavaDoc HTML documentation
./mvnw javadoc:javadoc
# Output: target/site/apidocs/index.html

# Generate documentation with custom options
./mvnw javadoc:javadoc -DadditionalJOption=-Xdoclint:none
```

#### JavaScript Documentation

```bash
# Install documentation generator
npm install -g jsdoc

# Generate JSDoc HTML documentation
jsdoc frontend/src/**/*.js -d docs/jsdoc
# Output: docs/jsdoc/index.html

# Generate documentation with custom template
jsdoc -c jsdoc.conf.json frontend/src/**/*.js
```

### IDE Integration

Most modern IDEs will automatically display the inline documentation:

- **IntelliJ IDEA**: Hover over methods/classes to see JavaDoc/JSDoc
- **VS Code**: Install "Document This" extension for enhanced documentation
- **WebStorm**: Built-in JSDoc support with type hints and examples
- **Eclipse**: JavaDoc integration with hover tooltips

---

## 🔌 API Documentation

### Interactive API Documentation

Once the application is running, access the interactive Swagger UI:

**🔗 http://localhost:8080/swagger-ui.html**

### Key Endpoints

#### Summoner Operations

- `GET /api/summoner/{region}/{summonerName}` - Get summoner by name
- `GET /api/summoner/{region}/by-puuid/{puuid}` - Get summoner by PUUID

#### Match Operations

- `GET /api/matches/{region}/{puuid}` - Get match history for a summoner
  - Query params: `page`, `size`, `start` (pagination)

#### Champion Operations

- `GET /api/champions` - List all champions
- `GET /api/champions/{championId}` - Get champion details
- `GET /api/champions/{championId}/builds` - Get champion builds
- `POST /api/champions/{championId}/aggregate` - Trigger build aggregation (if enabled)

#### Search

- `GET /search?q={query}&region={region}` - Search for summoners

#### Data Dragon

- `GET /ddragon/versions` - Get available DDragon versions
- `GET /ddragon/champions` - Get champion data
- `GET /ddragon/items` - Get item data
- `GET /ddragon/summoner-spells` - Get summoner spell data

### Response Format

All API responses follow a consistent format:

```json
{
  "data": { /* response data */ },
  "timestamp": "2025-10-03T23:42:50Z",
  "status": 200
}
```

Error responses include detailed error information:

```json
{
  "error": "SUMMONER_NOT_FOUND",
  "message": "Summoner not found in region EUW1",
  "timestamp": "2025-10-03T23:42:50Z",
  "status": 404
}
```

---

## 🧪 Testing

### Backend Tests

```bash
# Run all tests with coverage
./mvnw clean verify

# Run only unit tests
./mvnw test

# Run only integration tests
./mvnw integration-test

# Generate code coverage report
./mvnw jacoco:report
# Report available at: target/site/jacoco/index.html
```

### Frontend Tests

```bash
# Run all tests
npm test

# Run tests with coverage
npm run test -- --coverage

# Run tests with UI
npm run test:ui

# Run tests in watch mode
npm run test -- --watch
```

### Code Quality

```bash
# Run all quality checks (Checkstyle, PMD, SpotBugs)
./mvnw verify

# Run Checkstyle only
./mvnw checkstyle:checkstyle

# Run PMD only
./mvnw pmd:pmd

# Run SpotBugs only
./mvnw spotbugs:spotbugs

# Lint frontend code
npm run lint

# Auto-fix linting issues
npm run lint:fix
```

---

## 🏗️ Building for Production

### Docker Build

```bash
# Build optimized Docker image
docker build -t summonerapi:latest .

# Or use docker-compose
docker-compose build
```

The Dockerfile uses a multi-stage build:
1. **Stage 1**: Builds the frontend with Vite (optimized production bundle)
2. **Stage 2**: Builds the backend with Maven (includes frontend assets)
3. **Stage 3**: Creates minimal runtime image with JRE 21

### Manual Build

```bash
# Build frontend
npm run build
# Output: src/main/resources/static/

# Build backend (includes frontend)
./mvnw clean package -DskipTests
# Output: target/riot-api-spring-2.0.jar

# Run the JAR
java -jar target/riot-api-spring-2.0.jar
```

---

## 🚀 Deployment

### Docker Compose (Production)

1. **Configure environment**:
   ```bash
   cp .env.example .env
   # Edit .env with production values
   ```

2. **Set production profile**:
   ```bash
   SPRING_PROFILES_ACTIVE=prod
   ```

3. **Deploy**:
   ```bash
   docker-compose up -d
   ```

4. **Monitor logs**:
   ```bash
   docker-compose logs -f app
   ```

### Kubernetes

Example Kubernetes manifests can be created based on the Docker image. Key considerations:

- Use **ConfigMaps** for non-sensitive configuration
- Use **Secrets** for sensitive data (API keys, DB credentials)
- Set appropriate **resource limits**
- Configure **health checks** using `/actuator/health`
- Use **HPA** for auto-scaling based on CPU/memory
- Configure **ingress** for external access

### Nginx Reverse Proxy

An example Nginx configuration is provided in `nginx/summonerapi.conf.example`:

```bash
# Copy and configure
cp nginx/summonerapi.conf.example /etc/nginx/sites-available/summonerapi.conf
# Edit domain and SSL settings
nano /etc/nginx/sites-available/summonerapi.conf

# Enable site
ln -s /etc/nginx/sites-available/summonerapi.conf /etc/nginx/sites-enabled/
nginx -t
systemctl reload nginx
```

---

## 🔧 Development

### Backend Development

```bash
# Run in development mode with auto-reload
./mvnw spring-boot:run

# Run with specific profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Debug mode (port 5005)
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

### Frontend Development

```bash
# Start dev server with hot reload
npm run dev

# Dev server runs on http://localhost:5173
# API requests are proxied to http://localhost:8080
```

### Database Migrations

Flyway migrations are located in `src/main/resources/db/migration/`:

```bash
# Migrations run automatically on application startup
# To create a new migration:
# 1. Create a file: V{version}__{description}.sql
# 2. Example: V003__add_user_table.sql
# 3. Write your SQL migration
# 4. Restart the application
```

### Adding New Features

1. **Backend**:
   - Add entity in `entity/`
   - Create repository in `repository/`
   - Implement service in `service/`
   - Add controller in `controller/`
   - Write tests in `test/`

2. **Frontend**:
   - Create component in `components/`
   - Add API call in `api/`
   - Create page in `pages/` (if needed)
   - Add routing in `App.jsx`
   - Write tests

---

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

### Getting Started

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Make your changes
4. Run tests: `./mvnw verify && npm test`
5. Commit with conventional commits: `git commit -m "feat: add amazing feature"`
6. Push to your fork: `git push origin feature/amazing-feature`
7. Open a Pull Request

### Commit Convention

We follow [Conventional Commits](https://www.conventionalcommits.org/):

- `feat:` New feature
- `fix:` Bug fix
- `docs:` Documentation changes
- `style:` Code style changes (formatting, etc.)
- `refactor:` Code refactoring
- `test:` Adding or updating tests
- `chore:` Maintenance tasks

### Code Style

- **Backend**: Follow Google Java Style Guide (enforced by Checkstyle)
- **Frontend**: Follow Airbnb JavaScript Style Guide (enforced by ESLint)
- Run `./mvnw checkstyle:checkstyle` and `npm run lint` before committing

### Pull Request Process

1. Update the README.md with details of changes if applicable
2. Ensure all tests pass and code quality checks succeed
3. Update documentation for any API changes
4. The PR will be merged once approved by maintainers

---

## ⚖️ Legal & Compliance

### Riot Games API Terms

**This project is not endorsed by Riot Games and does not reflect the views or opinions of Riot Games or anyone officially involved in producing or managing Riot Games properties. Riot Games and all associated properties are trademarks or registered trademarks of Riot Games, Inc.**

By using this application, you acknowledge and agree to the following:

- This project uses the **Riot Games API** under their Developer Terms of Service
- All game data, assets, and content are property of **Riot Games, Inc.**
- This is an **unofficial, third-party application** and is not affiliated with, endorsed by, or sponsored by Riot Games
- Users must comply with the [Riot Games API Terms of Service](https://developer.riotgames.com/terms)
- Users must comply with the [Riot Games Terms of Service](https://www.riotgames.com/en/terms-of-service)

### API Key Requirements

**IMPORTANT**: To use this application, you must:

1. Obtain your own Riot Games API key from [https://developer.riotgames.com/](https://developer.riotgames.com/)
2. Comply with Riot's [API Usage Policy](https://developer.riotgames.com/api-usage)
3. **Never share or expose your API key publicly**
4. Respect rate limits imposed by Riot Games
5. Use the API only for non-commercial, educational, or personal purposes (unless you have a production API key)

⚠️ **Development API keys** are intended for personal use only. For production deployments serving third parties, you must apply for a production API key from Riot Games.

### Data Privacy & Usage

- This application **does not collect, store, or sell personal user data**
- Match and summoner data is cached temporarily for performance optimization
- All data is sourced directly from the Riot Games API
- No analytics, tracking, or third-party data sharing is performed
- Users are responsible for their own API key security and usage

### Disclaimer of Warranty

This software is provided "as is", without warranty of any kind, express or implied. The authors and contributors are not liable for any damages or issues arising from the use of this software.

- ⚠️ The application may stop working if Riot Games changes their API
- ⚠️ Your API key may be rate-limited or revoked if you violate Riot's terms
- ⚠️ The accuracy of data depends on the Riot Games API
- ⚠️ This is a personal project and may contain bugs or incomplete features

---

## 📝 License

This project is licensed under the **GNU General Public License v3.0** - see the [LICENSE](LICENSE) file for details.

### What this means:

- ✅ **Freedom to use**: Use this software for any purpose (subject to API terms above)
- ✅ **Freedom to study**: Examine and modify the source code
- ✅ **Freedom to share**: Redistribute copies to help others
- ✅ **Freedom to improve**: Distribute your modified versions
- ⚠️ **Share-alike**: Derivative works must also be GPL-licensed
- ⚠️ **No warranty**: Software is provided "as is" without warranty

**Note**: While this code is GPL-licensed, you must still comply with Riot Games' API Terms of Service when using their API.

---

## 🙏 Acknowledgments

- **[Riot Games](https://developer.riotgames.com/)** - For providing the League of Legends API and game data
- **[Community Dragon](https://www.communitydragon.org/)** - For additional game assets and resources
- **[Data Dragon](https://developer.riotgames.com/docs/lol#data-dragon)** - For champion, item, and spell data
- **[Spring Boot](https://spring.io/projects/spring-boot)** - For the amazing backend framework
- **[React](https://react.dev)** - For the powerful frontend library
- **[Vite](https://vitejs.dev/)** - For the blazing-fast build tool

### Legal Attribution

League of Legends and Riot Games are trademarks or registered trademarks of Riot Games, Inc. League of Legends © Riot Games, Inc.

All game assets, data, and content displayed in this application are the intellectual property of Riot Games, Inc. and are used in accordance with their API Terms of Service.

---

## 📧 Contact & Support

- **Author**: Mavi
- **Repository**: [github.com/zerox80/SummonerAPIv2](https://github.com/zerox80/SummonerAPIv2)
- **Issues**: [Report a bug or request a feature](https://github.com/zerox80/SummonerAPIv2/issues)

### Support Guidelines

- For **bugs** or **feature requests**: Open an issue on GitHub
- For **API-related issues**: Contact [Riot Games Developer Support](https://developer.riotgames.com/)
- For **security vulnerabilities**: Please report privately via GitHub Security Advisories

**Note**: This is a personal project maintained in spare time. Response times may vary.

---

## 🗺️ Roadmap

### Planned Features

- [ ] Real-time match spectator
- [ ] Advanced analytics dashboards
- [ ] Multi-language support (i18n)
- [ ] Champion mastery tracking
- [ ] Tier lists and meta analysis
- [ ] Friend comparison tools
- [ ] Mobile app (React Native)
- [ ] WebSocket for live updates
- [ ] Machine learning for win prediction

### Recently Completed

- [x] Automated champion build aggregation
- [x] Rate limiting system
- [x] PostgreSQL persistence
- [x] Docker containerization
- [x] CI/CD pipeline
- [x] Comprehensive testing
- [x] API documentation
- [x] Code quality tools

---

<div align="center">

---

**Made with ❤️ for the League of Legends community**

⭐ **Star this repo if you find it helpful!** ⭐

---

### Final Notes

This is an **educational, open-source hobby project** created for personal use and learning purposes by a solo developer.  
I am **not affiliated with or endorsed by Riot Games** - this is just a passion project for the LoL community.  

All League of Legends data, assets, and content are property of **Riot Games, Inc.**  

**Always respect the [Riot Games API Terms of Service](https://developer.riotgames.com/terms) and [Terms of Service](https://www.riotgames.com/en/terms-of-service).**

*League of Legends © Riot Games, Inc.*

</div>
