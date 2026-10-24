# Instrucciones para GitHub Copilot

## Contexto del proyecto

- Este repositorio implementa una API REST con Quarkus 3 y Java 21. El código de la aplicación está en `app-server-quarkus/`.
- La API sigue un enfoque contract-first: `app-server-quarkus/src/main/resources/openapi/openapi.yaml` es la fuente de verdad del contrato.
- Quarkiverse OpenAPI Server Generator genera interfaces y modelos. No edites archivos generados; modifica el contrato o el código fuente y deja que Maven regenere lo necesario.
- Las implementaciones REST están en `app-server-quarkus/src/main/java/com/example/appserver/api/`, la lógica de negocio en `service/`, las entidades JPA en `persistence/`, los repositorios en `repository/` y la configuración tipada en `config/`.
- La persistencia usa Hibernate ORM con Panache y H2 en memoria. La configuración de datasource y esquema está en `app-server-quarkus/src/main/resources/application.yaml`.
- Los datos iniciales se definen en `app-server-quarkus/src/main/resources/import.sql` y se cargan al crear el esquema; no agregues inicializadores Java equivalentes.
- Las pruebas están en `app-server-quarkus/src/test/java/` y usan JUnit 5, `@QuarkusTest` y Rest Assured.

## Lineamientos de cambios

- Antes de cambiar un endpoint, revisa el contrato OpenAPI y la implementación correspondiente. Mantén alineados contrato, implementación y pruebas.
- Conserva los encabezados requeridos `aplicacion-id`, `user-id` y `request-id` en los endpoints que los declaran.
- Respeta los límites existentes entre API, servicio y configuración; evita duplicar lógica de negocio en recursos REST.
- Para persistencia, modela tablas con entidades JPA, encapsula el acceso con repositorios Panache y delega las operaciones transaccionales a servicios. Mantén las entidades separadas de los modelos OpenAPI.
- Usa `@Transactional` en las operaciones que modifican datos y valida en pruebas las consultas, escrituras y datos iniciales.
- Prefiere Lombok para reducir getters, setters y constructores repetitivos cuando no afecte la claridad ni el comportamiento del framework. En entidades JPA usa anotaciones específicas como `@Getter`, `@Setter` y `@NoArgsConstructor`; evita `@Data` si puede generar `equals`, `hashCode` o `toString` inseguros para entidades o relaciones.
- Mantén `import.sql` coherente con las entidades y compatible con H2. El esquema y los datos iniciales son detalles de implementación: no los agregues al contrato OpenAPI salvo que cambien el comportamiento observable de la API.
- Usa Java 21 y las convenciones de nombres y formato ya presentes. Mantén los nombres de código en inglés, salvo términos propios del dominio ya establecidos.
- No agregues dependencias, configuración, abstracciones ni cambios de alcance que no sean necesarios para el requerimiento.
- No registres secretos ni valores específicos de entornos en el repositorio. No registres valores de configuración que puedan contener secretos.
- Para cambios de comportamiento, agrega o ajusta pruebas que cubran el caso y las respuestas HTTP esperadas.
- Conserva el estilo y el idioma de los documentos que modifiques; responde al usuario en el idioma en que solicitó el trabajo.

## Validación

- Ejecuta `cd app-server-quarkus && ./mvnw -q -Dquarkus.spring-cloud-config.enabled=false -Dparametros.config.valor1=test -Dparametros.config.valor2=test -Dparametros.config.valor3=test test` para validar localmente sin depender de un Config Server.
- Si no puedes ejecutar una validación, indícalo explícitamente y explica el motivo.
- Revisa el diff final para confirmar que no se modificaron archivos generados ni artefactos de compilación.
