# Diagrama de secuencia de una petición REST con persistencia

```mermaid
sequenceDiagram
    actor Client as Cliente HTTP
    participant Filter as ApiHeaderValidationFilter
    participant Resource as Recurso REST generado
    participant Impl as Implementación api
    participant Service as BookService
    participant Repository as BookRepository Panache
    participant ORM as Hibernate ORM
    participant DB as H2 en memoria
    participant MDC as Contexto de logs

    Client->>Filter: HTTP request + encabezados
    Filter->>Filter: valida encabezados requeridos
    Filter->>MDC: agrega request-id para correlación
    Filter->>Resource: petición válida
    Resource->>Impl: invoca operación REST
    Impl->>Service: delega lógica de negocio
    Service->>Repository: consulta o modifica libros
    Repository->>ORM: operación JPA
    ORM->>DB: SQL
    DB-->>ORM: filas / resultado
    ORM-->>Repository: entidades
    Repository-->>Service: resultado persistido
    Service-->>Impl: mapea entidad a modelo API
    Impl-->>Filter: respuesta y estado HTTP
    Filter->>MDC: registra método, ruta, estado y duración
    Filter->>MDC: elimina request-id del contexto
    Filter-->>Client: respuesta HTTP JSON
```

## Flujo de inicialización

1. Quarkus crea el esquema a partir de las entidades JPA conforme a `quarkus.hibernate-orm.schema-management.strategy`.
2. Hibernate ejecuta `src/main/resources/import.sql` e inserta los registros iniciales.
3. Al procesar una petición, el servicio consulta o modifica esas filas mediante el repositorio Panache.
