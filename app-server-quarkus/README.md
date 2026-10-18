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

## Related guides

- Quarkus REST guide: <https://quarkus.io/guides/rest>
- Quarkus OpenAPI guide: <https://quarkus.io/guides/openapi-swaggerui>
- Quarkiverse OpenAPI Generator server docs: <https://docs.quarkiverse.io/quarkus-openapi-generator/dev/server.html>

## Development notes

This project is a contract-first example using the Quarkiverse OpenAPI Server Generator and Quarkus 3.40.1.
