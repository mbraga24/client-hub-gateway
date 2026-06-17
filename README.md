# ClientHub Gateway

A Spring Boot microservice that provides user authentication, authorization, and client management via a gateway pattern. Uses Spring Security, JWT, Flyway, PostgreSQL, IP geolocation, and Docker.

## Overview

This service acts as the gateway layer for the ClientHub platform. It handles user registration (with IP-based geolocation eligibility checks), JWT authentication, and proxies client management operations to a downstream Client Management API.

## Tech Stack

* Java 17
* Spring Boot 3.1.2
* Spring Security
* Spring Data JPA
* PostgreSQL 15
* Flyway (database migrations)
* JWT (JJWT 0.11.5)
* Spring WebFlux (WebClient)
* Spring Mail
* Hibernate Validator
* Maven
* Docker & Docker Compose
* Lombok
* Log4j2

## Features

* User registration with IP geolocation eligibility check
* User login with JWT token generation
* JWT token validation and secured endpoints
* Role definitions (USER, ADMIN) — not yet enforced at the endpoint level
* Client management (create/delete) via downstream microservice
* Flyway database migrations
* Password validation (custom regex service)
* Global exception handling
* Docker Compose deployment
* Multi-stage Docker build
* Environment-based configuration

## API Endpoints

| Method | Endpoint                              | Description                                      |
| ------ | ------------------------------------- | ------------------------------------------------ |
| POST   | `/api/v1/authentication/register`     | Registers a new user (with IP eligibility check) |
| POST   | `/api/v1/authentication/authenticate` | Authenticates a user and returns a JWT token     |
| POST   | `/api/v1/clients`                     | Creates a new client (authenticated)             |
| DELETE | `/api/v1/clients/{id}`                | Deletes a client by ID (authenticated)           |

## Getting Started

### Prerequisites

* Docker & Docker Compose
* Java 17 (for local development)
* Maven (or use the included wrapper)

### Running with Docker Compose

```bash
docker-compose up --build
```

This starts:
- PostgreSQL 15 on port `5432`
- ClientHub Gateway on port `8081`

Environment variables are loaded from the `.env` file:

```env
POSTGRES_DB=gateway_auth
POSTGRES_USER=gateway_svc
POSTGRES_PASSWORD=gateway_pass
```

### Running Locally

1. Start a PostgreSQL instance (or use the Docker Compose database only):
   ```bash
   docker-compose up client-gateway-db
   ```

2. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

## Database Migrations

The application uses **Flyway** for schema management. Migration scripts are located in:

```
src/main/resources/db/migration/
```

Migrations run automatically on application startup. Hibernate is configured with `ddl-auto: validate` to ensure the schema matches the entity mappings without modifying it.

## Configuration

### application.yml

```yml
server:
  port: 8080

spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/gateway_auth}
    username: ${SPRING_DATASOURCE_USERNAME:gateway_svc}
    password: ${SPRING_DATASOURCE_PASSWORD:gateway_pass}

  jpa:
    hibernate:
      ddl-auto: validate

app:
  geolocation:
    enabled: true

client-hub-api:
  base-url: ${CLIENT_HUB_API_BASE_URL:http://localhost:8081}
```

### Profiles

- **default** — geolocation enabled, connects to external Client Management API
- **dev** — geolocation disabled

## Security

The application uses Spring Security with JWT (JJWT 0.11.5) authentication.

After a successful login, a JWT token is returned. Include it in the `Authorization` header for protected endpoints:

```http
Authorization: Bearer <jwt-token>
```

**Note:** Role definitions (USER, ADMIN) exist in the data model but are not currently enforced in the security filter chain. All authenticated users have equal access to protected endpoints.

Password validation is handled by a custom regex-based service (`PasswordValidationService`), not via Hibernate Validator annotations. Hibernate Validator is included as a dependency but is not used for password rules.

## Project Structure

```text
src/main/java/com/clienthub/gateway
├── auth/              # Authentication controller, service, DTOs
├── client/            # Client management controller, service, DTOs
├── config/            # Security, JWT, WebClient configuration
├── exception/         # Global exception handler and custom exceptions
├── ipapi/             # IP geolocation service integration
├── user/              # User entity, repository, service, roles
├── utils/             # Validation utilities
└── ClientHubApplication.java

src/main/resources
├── db/migration/      # Flyway migration scripts
├── application.yml
├── application-dev.yml
└── log4j2.xml
```

## Docker

### Dockerfile (multi-stage)

- **Stage 1**: Maven build with Java 17
- **Stage 2**: Lightweight JRE-only runtime

### docker-compose.yml

- `client-gateway-db` — PostgreSQL 15
- `client-gateway-service` — The gateway application

## Author

Marlon Braga
