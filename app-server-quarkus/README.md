# app-server-quarkus

This project uses Quarkus, the Supersonic Subatomic Java Framework.

It implements a contract-first REST API using the Quarkiverse OpenAPI Server Generator.

Official documentation:
- Quarkus: <https://quarkus.io/>
- Quarkiverse OpenAPI Generator: <https://docs.quarkiverse.io/quarkus-openapi-generator/dev/index.html>
- Server generator docs: <https://docs.quarkiverse.io/quarkus-openapi-generator/dev/server.html>

## Architecture

The application follows a contract-first, layered architecture:

1. `src/main/resources/openapi/openapi.yaml` is the source of truth for REST paths, headers, request schemas, and responses.
2. Quarkiverse OpenAPI Generator generates the server interfaces and models during the Maven build. Generated files are not maintained by hand.
3. `api/` implements the generated resource interfaces and delegates business operations to `service/`.
4. `service/` handles book operations and reports, using `repository/` for persistence.
5. `repository/` uses Hibernate ORM with Panache to access JPA entities in `persistence/`.
6. Hibernate connects to the H2 in-memory database. `src/main/resources/import.sql` inserts the initial books when the schema is created.
7. `ApiHeaderValidationFilter` correlates request logs with the caller's `request-id`; completion logs include the method, path, HTTP status, and elapsed time without logging request bodies or header values.

The request flow and component relationships are documented in:
- [Architecture flow](docs/architecture-flow.md)
- [Visual architecture](docs/architecture-flow-visual.md)
- [Request sequence](docs/architecture-flow-sequence.md)

Source layout:
- `src/main/java/com/example/appserver/api/` — REST implementations and header validation.
- `src/main/java/com/example/appserver/service/` — business logic and transactions.
- `src/main/java/com/example/appserver/repository/` — Panache repositories.
- `src/main/java/com/example/appserver/persistence/` — JPA entities.
- `src/main/resources/openapi/` — OpenAPI contract used for generation.
- `src/main/resources/import.sql` — initial database records.
- `src/test/java/` — Quarkus integration tests using JUnit 5 and Rest Assured.

## Runtime configuration

The generator is configured with the following Quarkus properties:

```properties
quarkus.openapi.generator.server.use=openapitools
quarkus.openapi.generator.server.spec=openapi.yaml
quarkus.openapi.generator.server.input-base-dir=src/main/resources/openapi
quarkus.openapi.generator.server.base-package=com.example.appserver
```

These properties are read by Quarkus at startup and the generator then creates the JAX-RS interfaces under the generated sources directory.

## Database persistence

Book data is persisted through Hibernate ORM with Panache and an H2 in-memory database. The datasource and schema lifecycle are configured in `src/main/resources/application.yaml`:

```yaml
quarkus:
  datasource:
    db-kind: h2
    jdbc:
      url: "jdbc:h2:mem:appserver;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"
  hibernate-orm:
    schema-management:
      strategy: drop-and-create
```

The database exists only for the lifetime of the application process, and the schema is recreated on startup. Hibernate loads initial records from `src/main/resources/import.sql` when it creates the schema. `BookService` uses a Panache repository for CRUD operations; availability and author reports query persisted data. API response IDs retain the `bk-1001` format.

## External configuration with a Config Server

The application includes Quarkus Spring Cloud Config support. It is enabled in `application.yaml`, points to `http://localhost:8888/`, and is configured to fail fast when the server is unavailable. `BooksConfig` declares the `parametros.config.valor1`, `valor2`, and `valor3` properties.

For local development, `src/main/resources/application-local.yaml` supplies local values and disables remote configuration. Activate the `local` Quarkus profile to load it. Also pass `quarkus.spring-cloud-config.enabled=false` as a system property so the build-time Config Client setting is disabled before Quarkus starts:

```shell
./mvnw compile quarkus:dev \
  -Dquarkus.profile=local \
  -Dquarkus.spring-cloud-config.enabled=false
```

## Running the application in dev mode

```shell script
./mvnw compile quarkus:dev \
  -Dquarkus.profile=local \
  -Dquarkus.spring-cloud-config.enabled=false
```

> **_NOTE:_** Quarkus Dev UI is available at <http://localhost:8080/q/dev/>.

## Packaging and running the application

```shell script
./mvnw package
```

Then run:

```shell script
java -jar target/quarkus-app/quarkus-run.jar
```

## Current API contract

The OpenAPI contract includes:
- `GET /api/health`
- `GET /api/books`
- `POST /api/books`
- `GET /api/books/{id}`
- `PATCH /api/books/{id}`
- `GET /api/reports/books-availability`
- `GET /api/reports/books-by-author`

Endpoints under the `Reportes` tag are intentionally grouped together and generated as a separate server resource.

## Important note about generated methods

This project uses the Quarkiverse server generator, not the classic `openapi-generator-maven-plugin`.

That means the generated interfaces are designed as resource contracts, and the runtime behavior follows the generated JAX-RS signatures. In particular, a generated method returning a POJO (for example `BookResponse`) will respond with `200 OK` by default, not necessarily `201 Created`, even if the OpenAPI spec declares `201`.

If strict `201` semantics are required, the implementation must return `jakarta.ws.rs.core.Response` explicitly.

## Dependencies and purpose

This project uses a small set of Quarkus and Quarkiverse dependencies to support contract-first REST development.

### Core runtime

- `io.quarkus:quarkus-rest`  
  Provides the REST layer for the app. This is the main runtime for JAX-RS style resources and HTTP endpoints.

- `io.quarkus:quarkus-rest-jackson`  
  Adds Jackson JSON serialization/deserialization for REST resources and model objects.

- `io.quarkus:quarkus-arc`  
  Provides CDI / dependency injection used throughout the application.

- `io.quarkus:quarkus-hibernate-orm-panache` and `io.quarkus:quarkus-jdbc-h2`
  Provide the Panache repository layer, Hibernate ORM, and the in-memory H2 JDBC driver.

- `io.quarkus:quarkus-hibernate-validator`  
  Adds bean validation support for request validation and constraints.

### OpenAPI contract generation

- `io.quarkiverse.openapi.generator:quarkus-openapi-generator-server`  
  Generates server-side interfaces and model classes from the OpenAPI contract. This is the key dependency for the contract-first workflow used in this project.

### Testing

- `io.quarkus:quarkus-junit5`  
  JUnit 5 support for Quarkus tests.

- `io.rest-assured:rest-assured`  
  Used to perform HTTP-level tests against the running app.

### Build and platform alignment

- `io.quarkus.platform:quarkus-bom`  
  Makes sure the project uses a compatible Quarkus platform version and aligns all Quarkus dependencies to the same version: `3.40.1`.

## Why this approach is recommended for contract-first

The recommended flow is:

1. Define the contract in `src/main/resources/openapi/openapi.yaml`.
2. Generate the server interfaces/models from that contract.
3. Implement the resource logic in separate classes.
4. Regenerate whenever the contract changes.

This keeps the API contract as the source of truth and reduces drift between documentation and implementation.

This project follows that model using the Quarkiverse server generator, which is designed for contract-first server code generation in Quarkus.

## Related guides

- Quarkus REST guide: <https://quarkus.io/guides/rest>
- Quarkus OpenAPI guide: <https://quarkus.io/guides/openapi-swaggerui>
- Quarkiverse OpenAPI Generator server docs: <https://docs.quarkiverse.io/quarkus-openapi-generator/dev/server.html>

## Development notes

This project is a contract-first example using the Quarkiverse OpenAPI Server Generator and Quarkus 3.40.1.

## GitHub Copilot guidelines

Repository-wide guidance for GitHub Copilot is in the repository-root `.github/copilot-instructions.md`. Path-specific Java and OpenAPI guidance is in `.github/instructions/`, and the `contract-first-api` skill in `.github/skills/` describes the workflow for API contract changes. Pull requests use the checklist in `.github/pull_request_template.md`.
