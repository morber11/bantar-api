# Bantar API

Backend for Bantar

Frontend: [morber11/bantar-api](https://github.com/morber11/bantar-web)

## Requirements

- Java 17+, Maven or Docker

## Quick start

- With Docker:

```bash
docker compose up -d --build
```

- With Maven:

```bash
mvn spring-boot:run
```

## Configuration

Place a `.env` file at the project root or set environment variables directly. `docker compose` will load `.env` automatically.

**Common variables** (defaults shown):

- `SPRING_PROFILES_ACTIVE` — profile (e.g., `local`, `prod`)
- `SPRING_DATASOURCE_URL` — JDBC URL (default: `jdbc:h2:file:./data/bantar`)
- `SPRING_DATASOURCE_USERNAME` — DB user (default: `sa`)
- `SPRING_DATASOURCE_PASSWORD` — DB password (default: `password`)

### Examples

Minimal (H2):

```dotenv
# .env (local, H2)
SPRING_PROFILES_ACTIVE=local
SPRING_DATASOURCE_URL=jdbc:h2:file:./data/bantar
SPRING_DATASOURCE_USERNAME=sa
SPRING_DATASOURCE_PASSWORD=password
```

## Database migrations

- Migrations are applied with Flyway. Java-based migrations live under `src/main/java/com/bantar/db/migration` and are executed programmatically on startup.

## Usage

- Base API: `http://localhost:8080/api`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- H2 console (if H2): `http://localhost:8080/h2-console`
