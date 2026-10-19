# Diagrama de secuencia del flujo de configuración y API

```mermaid
sequenceDiagram
    actor Client as Cliente HTTP
    participant GitHub as GitHub
    participant Config as Config Server
    participant QApp as Quarkus App
    participant BooksConfig as BooksConfig
    participant BookService as BookService
    participant OpenAPI as OpenAPI Contract
    participant Generator as Quarkiverse Generator
    participant Resources as Generated Resources

    Client->>QApp: GET /api/health
    QApp->>BooksConfig: inyecta parámetros
    BooksConfig->>Config: solicita configuración remota
    Config->>GitHub: consulta repo/config
    GitHub-->>Config: application.yml / books-config
    Config-->>BooksConfig: valor1, valor2, valor3
    BooksConfig-->>QApp: propiedades resueltas

    OpenAPI->>Generator: define spec openapi.yaml
    Generator->>Resources: genera interfaces/modelos
    Resources-->>QApp: BooksResource / SystemResource / ReportesResource

    QApp->>BookService: invoca lógica del negocio
    BookService-->>QApp: respuesta del endpoint
    QApp-->>Client: JSON con el resultado
```

## Descripción

1. El cliente hace una petición HTTP a la API.
2. La app Quarkus necesita configuración y la obtiene del Config Server.
3. El Config Server puede leer el contenido desde GitHub.
4. La app inyecta esos valores a través de `BooksConfig` usando `@ConfigProperty`.
5. El contrato OpenAPI se usa como fuente de verdad para generar la API.
6. El generator crea las interfaces JAX-RS y modelos.
7. La lógica de negocio se ejecuta en `BookService` y responde al cliente.

## Observación

Si el Config Server no está disponible, la app puede usar los valores locales definidos en `application.properties` como fallback. En entorno real, la configuración remota suele encargarse de proveer los valores del sistema.
