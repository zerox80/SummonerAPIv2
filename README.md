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

## 📝 License

This project is licensed under the **GNU General Public License v3.0** - see the [LICENSE](LICENSE) file for details.

### What this means:

- ✅ **Freedom to use**: Use this software for any purpose
- ✅ **Freedom to study**: Examine and modify the source code
- ✅ **Freedom to share**: Redistribute copies to help others
- ✅ **Freedom to improve**: Distribute your modified versions
- ⚠️ **Share-alike**: Derivative works must also be GPL-licensed

---

## 🙏 Acknowledgments

- **[Riot Games](https://developer.riotgames.com/)** - For providing the League of Legends API
- **[Community Dragon](https://www.communitydragon.org/)** - For additional game assets
- **[Data Dragon](https://developer.riotgames.com/docs/lol#data-dragon)** - For champion, item, and spell data
- **[Spring Boot](https://spring.io/projects/spring-boot)** - For the amazing backend framework
- **[React](https://react.dev)** - For the powerful frontend library
- **[Vite](https://vitejs.dev/)** - For the blazing-fast build tool

---

## 📧 Contact & Support

- **Author**: Mavi
- **Repository**: [github.com/zerox80/SummonerAPIv2](https://github.com/zerox80/SummonerAPIv2)
- **Issues**: [Report a bug or request a feature](https://github.com/zerox80/SummonerAPIv2/issues)

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

**Made with ❤️ for the League of Legends community**

⭐ **Star this repo if you find it helpful!** ⭐

</div>
