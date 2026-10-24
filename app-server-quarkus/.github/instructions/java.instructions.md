---
applyTo: "app-server-quarkus/src/**/*.java"
---

# Lineamientos para Java

- Mantén las clases de recursos REST delgadas: delega la lógica de negocio a servicios CDI.
- Usa las APIs Jakarta y los patrones CDI/Quarkus que ya utiliza el proyecto.
- Para persistencia, usa entidades JPA en `persistence/` y repositorios `PanacheRepository` en `repository/`; deja las reglas de negocio y las transacciones en `service/`.
- Marca con `@Transactional` las operaciones de servicio que escriben en la base de datos. No accedas a entidades directamente desde recursos REST ni expongas entidades como respuestas de API.
- Prefiere Lombok para getters, setters y constructores repetitivos cuando sea compatible con JPA/CDI. En entidades usa anotaciones específicas como `@Getter`, `@Setter` y `@NoArgsConstructor`; evita `@Data` cuando genere métodos que incluyan estado mutable o relaciones.
- Los datos iniciales deben cargarse desde `app-server-quarkus/src/main/resources/import.sql` al crearse el esquema, no desde código Java. Mantén el SQL compatible con las entidades y la base configurada, y prueba que se cargue correctamente.
- Correlaciona los logs de cada petición con `request-id` usando MDC. Registra metadatos operativos (método, ruta, estado y duración), nunca cuerpos ni valores sensibles de encabezados; sanea datos controlados por el cliente antes de registrarlos.
- No implementes ni edites directamente interfaces o modelos generados por OpenAPI.
- Maneja los casos de error de forma explícita y coherente con el contrato HTTP; no ocultes errores con valores de respaldo silenciosos.
- Agrega o actualiza pruebas JUnit/Rest Assured para cambios observables en la API o en la lógica de negocio.
- Evita registrar datos sensibles o valores de configuración que pudieran contener secretos.
