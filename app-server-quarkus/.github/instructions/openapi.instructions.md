---
applyTo: "app-server-quarkus/src/main/resources/openapi/**/*.yaml"
---

# Lineamientos para OpenAPI

- Trata el documento OpenAPI como la fuente de verdad de los endpoints, parámetros, esquemas y respuestas HTTP.
- Conserva el estilo OpenAPI 3.0.3 y las convenciones de nombres existentes.
- Los endpoints que ya los requieren deben declarar los encabezados `aplicacion-id`, `user-id` y `request-id`.
- Describe los códigos HTTP y los esquemas de respuesta con precisión. No asumas que una respuesta declarada como `201` se implementa automáticamente con ese estado: confirma el comportamiento de la interfaz generada y de la implementación.
- Mantén los detalles de persistencia (base de datos, tablas, entidades JPA, repositorios y SQL de carga inicial) fuera del contrato; solo actualízalo cuando cambie el comportamiento observable de la API.
- No expongas entidades JPA como esquemas REST. Usa los modelos OpenAPI existentes y mapea entre ellos y las entidades en la capa de servicio.
- Tras cambiar el contrato, revisa y actualiza su implementación y las pruebas relacionadas; valida con
`cd app-server-quarkus && ./mvnw -q -Dquarkus.spring-cloud-config.enabled=false -Dparametros.config.valor1=test -Dparametros.config.valor2=test -Dparametros.config.valor3=test test`.
- No edites fuentes generadas para reflejar cambios en el contrato.
