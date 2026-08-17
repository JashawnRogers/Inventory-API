# Inventory API

Inventory API is a Spring Boot backend for managing products, warehouse stock, and inventory movement history. It models common inventory workflows such as receiving stock, issuing stock to departments, reserving inventory, releasing reservations, making manual adjustments, and transferring stock between warehouses.

The project is built as a REST API with HATEOAS response models, PostgreSQL persistence, custom JPQL reporting queries, and OpenAPI documentation. It is intended to show a practical backend application with domain rules, transactional service methods, repository-level reporting, and tests around the core inventory behavior.

## Key Features

- Product, category, supplier, department, employee, and warehouse management.
- Stock item tracking for a specific product and warehouse combination.
- Inventory workflows:
  - receive inventory into a warehouse;
  - issue inventory to a department;
  - reserve stock for a department;
  - release reserved stock;
  - increase or decrease inventory through manual adjustments;
  - transfer inventory between warehouses.
- Stock movement audit records for inventory operations, including movement type, quantity, employee, department when applicable, reason, reference, unit cost, and total cost.
- Business rules enforced in the domain and service layers:
  - stock quantities must be positive for movement operations;
  - reserved quantity cannot exceed quantity on hand;
  - issue, reserve, decrease adjustment, and transfer operations cannot consume more than available stock;
  - stock items are unique by product and warehouse;
  - inactive related resources are rejected for relevant operations.
- Low-stock detection based on available stock and product reorder point.
- Server-sent event endpoint for low-stock notifications.
- Reporting endpoints for:
  - stock movement history by date range;
  - stock movement history by department;
  - department cost totals;
  - stock items created within a date range;
  - inventory value by product and warehouse;
  - global inventory value by product.
- Paged responses for collection and reporting endpoints where implemented.
- HATEOAS assemblers for API responses.
- Centralized exception handling using Spring `ProblemDetail`.
- Optimistic locking on `StockItem` through a JPA `@Version` field.

## Architecture and Design

The codebase is organized by business area rather than by technical layer alone. Packages such as `product`, `warehouse`, `stockItem`, `stockMovement`, `inventory`, and `reports` contain the controllers, services, repositories, DTOs, assemblers, and domain models relevant to that area.

The main flow is:

- Controllers expose REST endpoints and convert service results into HATEOAS response models.
- Services contain application workflow logic and transaction boundaries.
- Domain entities enforce business rules such as valid stock quantities, active resource checks, and state changes.
- Repositories use Spring Data JPA for persistence, specifications for filtered resource lists, and custom JPQL queries for reporting.
- DTOs and projection interfaces shape API responses and reporting query results.
- `GlobalExceptionHandler` maps custom domain exceptions into `ProblemDetail` responses with an `errorCode` and timestamp.

The inventory service is the core workflow layer. It coordinates products, warehouses, employees, departments, stock items, stock movements, and low-stock notification checks inside transactional methods.

## Technology Stack

- Java 21
- Spring Boot 4.1
- Spring Web MVC
- Spring Data JPA
- Spring HATEOAS
- PostgreSQL
- Hibernate ORM
- Jakarta Validation
- Springdoc OpenAPI / Swagger UI
- Maven
- Docker
- Railway for deployment
- JUnit
- Mockito
- Spring Boot test support
- Spring Data JPA slice tests

## API Documentation

Live Swagger UI:

[View API Documentation](https://inventory-api-production-543f.up.railway.app/swagger-ui.html)

When running locally, Swagger UI is available at:

```text
http://localhost:8080/swagger-ui.html
```

The OpenAPI JSON endpoint is:

```text
http://localhost:8080/v3/api-docs
```

## Running Locally

### Prerequisites

- JDK 21
- Maven wrapper included in the repository
- PostgreSQL running locally
- Databases created for the profiles you plan to use, such as:
  - `inventory_dev`
  - `inventory_test`

### Environment Variables

The project reads database credentials from environment variables. Use `.env.example` as the template and create a local `.env` file with your own values.

Required development variables:

```text
DEV_DB_USERNAME
DEV_DB_PASSWORD
```

Required test variables:

```text
TEST_DB_USERNAME
TEST_DB_PASSWORD
```

Required production variables:

```text
PROD_DB_URL
PROD_DB_USERNAME
PROD_DB_PASSWORD
```

Do not commit `.env` or real credential values.

### Start the App with the Dev Profile

Load the local environment variables:

```zsh
set -a
source .env
set +a
```

Run the application:

```zsh
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```

The API will be available at:

```text
http://localhost:8080
```

## Configuration

The application uses Spring profiles for environment-specific database configuration.

- `dev`: connects to local PostgreSQL database `inventory_dev`.
- `test`: connects to local PostgreSQL database `inventory_test`.
- `prod`: reads PostgreSQL connection settings from production environment variables.

Base application configuration is kept in `application.properties`. Profile-specific datasource settings are in:

- `application-dev.properties`
- `application-test.properties`
- `application-prod.properties`

Hibernate is currently configured with:

```properties
spring.jpa.hibernate.ddl-auto=update
```

Production secrets are expected to be supplied through environment variables rather than committed configuration files.

## Testing

The project includes:

- service-level unit tests for inventory business rules using Mockito;
- JPA repository tests for stock item reporting queries;
- JPA repository tests for stock movement reporting queries;
- a Spring Boot context test;
- a server-sent event test for low-stock notification output.

Repository tests use the `test` Spring profile and are configured not to replace the datasource with an embedded database, so they expect the local PostgreSQL test database to be available.

Run tests with:

```zsh
set -a
source .env
set +a
SPRING_PROFILES_ACTIVE=test ./mvnw test
```

## Docker

The project includes a multi-stage Dockerfile:

- build stage: uses Eclipse Temurin JDK 21 and Maven to package the application;
- runtime stage: uses Eclipse Temurin JRE 21;
- the final image runs the packaged Spring Boot jar;
- tests are skipped during the Docker image build.

Build the image:

```zsh
docker buildx build --tag inventory-api .
```

Run the image only with database environment variables that point to a reachable PostgreSQL instance:

```zsh
docker run --env-file .env -p 8080:8080 inventory-api
```

The Dockerfile sets `SPRING_PROFILES_ACTIVE=prod` by default.

## Deployment

The application is containerized with Docker and deployed on Railway. In production, the app uses the `prod` Spring profile and connects to Railway-hosted PostgreSQL through environment variables supplied by Railway.

The production deployment exposes the API and Swagger UI through the Railway application domain.

## Future Improvements

Planned enhancements:

- Excel export functionality for reports.
- PDF export functionality for reports.

These are future features and are not currently implemented.

## Project Status

The core REST API, inventory workflows, reporting queries, OpenAPI documentation, Docker packaging, and Railway deployment configuration are in place. Export functionality and any dedicated frontend client are outside the current implementation.
