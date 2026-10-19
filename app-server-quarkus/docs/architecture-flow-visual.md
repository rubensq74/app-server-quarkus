# Diagrama visual del flujo de la aplicación

```mermaid
flowchart TB
    subgraph GitHub[GitHub]
        GH[Repositorio de configuración\nbooks-config]
    end

    subgraph Config[Config Server]
        CS[Spring Cloud Config Server\nhttp://localhost:8888]
    end

    subgraph Quarkus[Aplicación Quarkus]
        APP[Quarkus App\napp-server-quarkus]
        CFG[BooksConfig\n@ConfigProperty]
        SVC[BookService\nLógica de negocio]
        OAPI[OpenAPI Contract\nopenapi.yaml]
        GEN[Quarkiverse Generator\nquarkus-openapi-generator-server]
        RES[Generated Resources\nBooksResource / SystemResource / ReportesResource]
        API[Endpoints REST\n/api/books /api/health /api/reports]
    end

    subgraph Client[Clientes]
        C1[Cliente HTTP]
    end

    GH -->|config| CS
    CS -->|properties| CFG
    CFG -->|inject| APP
    OAPI -->|contract| GEN
    GEN -->|generates| RES
    RES -->|implements| SVC
    APP -->|serves| API
    C1 -->|HTTP request| API
    API -->|calls| SVC
    SVC -->|returns| API
    API -->|JSON response| C1
```

## Interpretación visual

- GitHub alberga la configuración externa.
- El Config Server obtiene esos valores y los entrega a la app.
- Quarkus inyecta la configuración con `@ConfigProperty`.
- El contrato OpenAPI define la API.
- El generator crea los recursos REST.
- La app implementa la lógica real de negocio.
- Los clientes consumen la API final.

## Observación

Este flujo es válido para entorno real con Config Server. En local, la app también puede usar propiedades declaradas en `application.properties` como fallback.
