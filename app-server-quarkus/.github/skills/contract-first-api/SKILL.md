---
name: contract-first-api
description: Use when adding or changing REST endpoints, schemas, headers, or HTTP responses in this Quarkus project.
---

# Desarrollo de API contract-first

Usa este flujo al añadir o modificar una operación REST, un esquema, un parámetro o una respuesta HTTP.

1. Lee la operación relacionada en `app-server-quarkus/src/main/resources/openapi/openapi.yaml` y confirma sus encabezados, cuerpo, respuestas y modelos.
2. Actualiza primero el contrato OpenAPI. Mantén la versión 3.0.3 y reutiliza componentes compartidos cuando corresponda.
3. Implementa el comportamiento en las clases fuente de `app-server-quarkus/src/main/java/com/example/appserver/api/` y delega la lógica de negocio a `service/`.
4. Si el cambio requiere persistencia, usa entidades JPA en `persistence/`, repositorios Panache en `repository/` y operaciones transaccionales en `service/`. Mantén las entidades separadas de los modelos generados por OpenAPI.
5. Define los datos iniciales en `app-server-quarkus/src/main/resources/import.sql`; no los cargues desde constructores, observers CDI ni inicializadores Java. Verifica que el SQL corresponda al esquema JPA y a la estrategia de creación del esquema.
6. Prefiere Lombok para código repetitivo cuando sea compatible con JPA/CDI; usa anotaciones específicas y conserva los constructores requeridos por el framework.
7. Comprueba cómo se genera la interfaz del endpoint y qué estado HTTP devuelve realmente. El generador no garantiza que una respuesta POJO use el mismo código de estado que declara el contrato.
8. Añade o modifica pruebas en `app-server-quarkus/src/test/java/` para cubrir el comportamiento, los encabezados, los estados HTTP y la persistencia/datos iniciales cuando corresponda.
9. Ejecuta `cd app-server-quarkus && ./mvnw -q -Dquarkus.spring-cloud-config.enabled=false -Dparametros.config.valor1=test -Dparametros.config.valor2=test -Dparametros.config.valor3=test test` para probar sin un Config Server local, y revisa el diff para descartar ediciones de código generado o de `target/`.

No cambies comportamiento contractual sin actualizar el contrato, la implementación y las pruebas en conjunto.
