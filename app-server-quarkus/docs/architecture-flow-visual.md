# Diagrama visual del flujo de la aplicación

```mermaid
flowchart TB
    subgraph ContractBuild[Contrato y generación]
        Spec[openapi.yaml]
        Generator[Quarkiverse OpenAPI Generator]
        Resources[Interfaces y modelos REST generados]
        Spec --> Generator --> Resources
    end

    subgraph Quarkus[Aplicación Quarkus]
        Client[Cliente HTTP]
        Filter[Validación de encabezados]
        Api[Implementaciones REST api]
        Service[BookService]
        Repository[BookRepository<br/>Panache]
        Entity[BookEntity<br/>JPA]
        Hibernate[Hibernate ORM]
        H2[(H2 en memoria)]
        MDC[Correlación MDC<br/>request-id]
        Logs[Logs HTTP<br/>status / duración]
        Client -->|petición HTTP| Filter --> Api
        Resources --> Api
        Api --> Service --> Repository --> Hibernate --> H2
        Repository --- Entity
        Filter -. request-id .-> MDC
        MDC -. correlaciona logs .-> Service
        Filter --> Logs
    end

    Seed[src/main/resources/import.sql] -->|carga al crear el esquema| H2
```

## Interpretación visual

- El contrato OpenAPI genera las interfaces REST y modelos usados por las implementaciones.
- La petición pasa por el filtro de encabezados y llega a la capa `api/`.
- `BookService` delega las operaciones a `BookRepository`, que usa Hibernate ORM y entidades JPA.
- H2 almacena libros; los datos iniciales se insertan desde `import.sql` al crear el esquema.
- La base H2 es en memoria y sus datos duran solo mientras vive el proceso.
