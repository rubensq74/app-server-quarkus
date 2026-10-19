# app-server-quarkus

This project uses Quarkus, the Supersonic Subatomic Java Framework.

It implements a contract-first REST API using the Quarkiverse OpenAPI Server Generator.

Official documentation:
- Quarkus: <https://quarkus.io/>
- Quarkiverse OpenAPI Generator: <https://docs.quarkiverse.io/quarkus-openapi-generator/dev/index.html>
- Server generator docs: <https://docs.quarkiverse.io/quarkus-openapi-generator/dev/server.html>

## Architecture

The API contract is defined in:
- `src/main/resources/openapi/openapi.yaml`

The project uses the Quarkiverse server generator:
- `io.quarkiverse.openapi.generator:quarkus-openapi-generator-server`
- configured via `src/main/resources/application.properties`

The generated contract produces server resources such as:
- `com.example.appserver.resources.BooksResource`
- `com.example.appserver.resources.SystemResource`
- `com.example.appserver.resources.ReportesResource`

The implementation classes live under:
- `src/main/java/com/example/appserver/api/`

## Runtime configuration

The generator is configured with the following Quarkus properties:

```properties
quarkus.openapi.generator.server.use=openapitools
quarkus.openapi.generator.server.spec=openapi.yaml
quarkus.openapi.generator.server.input-base-dir=src/main/resources/openapi
quarkus.openapi.generator.server.base-package=com.example.appserver
```

These properties are read by Quarkus at startup and the generator then creates the JAX-RS interfaces under the generated sources directory.

## External configuration with a Config Server

This project also demonstrates loading configuration values from an external Config Server, using Quarkus Spring Cloud Config support.

The configuration currently used is:

```properties
parametros.config.valor1=valor1-local
parametros.config.valor2=valor2-local
parametros.config.valor3=valor3-local
```

and the app expects these values to be injected through:

```java
@ConfigProperty(name = "parametros.config.valor1")
String valor1;
```

The project is prepared for a remote Config Server with:

```properties
quarkus.spring-cloud-config.enabled=true
quarkus.spring-cloud-config.url=http://localhost:8888
quarkus.spring-cloud-config.name=books-config
quarkus.spring-cloud-config.fail-fast=true
```

This means the app can read external configuration values from a Spring Cloud Config server if it is available. In local development, the explicit values in `application.properties` act as a fallback and keep the app bootable without that external dependency.

### Why this is useful

- centralizes runtime values outside the codebase
- allows different environments (`dev`, `qa`, `prod`) to use different settings
- keeps secrets and environment-specific data out of source control

### Important note

The current project is intentionally configured to use local values by default for tests and local execution. If you start a real Config Server at `http://localhost:8888`, the values may be overridden by the remote config source.

## Running the application in dev mode

```shell script
./mvnw compile quarkus:dev
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
