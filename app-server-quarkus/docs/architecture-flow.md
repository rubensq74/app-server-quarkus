# Diagrama de flujo de la aplicación

```mermaid
flowchart LR
    A[GitHub Repository] --> B[Config Server]
    B --> C[books-config]
    C --> D[Quarkus App]
    D --> E[BooksService]
    E --> F[OpenAPI Contract]
    F --> G[Quarkiverse OpenAPI Generator]
    G --> H[Generated Resources]
    H --> I[BooksResource / SystemResource / ReportesResource]
    D --> J[HTTP Clients / Consumers]
    J --> K[REST API /api/books]
    J --> L[REST API /api/health]
    J --> M[REST API /api/reports]

    subgraph ExternalConfig[Configuración externa]
        A
        B
        C
    end

    subgraph App[Aplicación Quarkus]
        D
        E
        F
        G
        H
        I
    end

    subgraph APIs[Endpoints expuestos]
        K
        L
        M
    end
```

## Descripción del flujo

1. El repositorio de GitHub guarda la configuración externa del servicio.
2. El Config Server consulta y sirve ese archivo de configuración, por ejemplo `books-config`.
3. La aplicación Quarkus se conecta al Config Server usando la propiedad:
   `quarkus.spring-cloud-config.url=http://localhost:8888`
4. La app inyecta esos valores con `@ConfigProperty` y los usa en `BooksConfig` y `BookService`.
5. La API REST se entrega desde el contrato OpenAPI ubicado en `src/main/resources/openapi/openapi.yaml`.
6. El Quarkiverse OpenAPI Generator crea las interfaces y modelos JAX-RS a partir del contrato.
7. La aplicación implementa la lógica en clases como `BooksResourceImpl`, `SystemResourceImpl` y `ReportesResourceImpl`.
8. Los clientes consumen los endpoints REST generados por la aplicación.

## Nota importante

En desarrollo local, la aplicación puede usar valores locales definidos en `application.properties` si el Config Server no está disponible. En producción o entorno real, la configuración remota del Config Server suele sobrescribir esos valores.
