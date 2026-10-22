# 10 · Using OpenAPI and Swagger UI

Guía oficial: [Using OpenAPI and Swagger UI](https://quarkus.io/guides/openapi-swaggerui) — generar la especificación OpenAPI y explorar la API desde Swagger UI.

## Progresión acumulada

Esta carpeta parte de la guía 09 y conserva sus ocho guías anteriores: aplicación
inicial, configuración, RESTEasy Classic, referencia de configuración, Quarkus
REST, logging, fundamentos JSON, servicios JSON y validación de entradas.
También incluye el CRUD de actividades y su filtro como ejemplo práctico
documentable en OpenAPI.

## Qué aporta esta guía

- Generación automática de la especificación OpenAPI a partir de los endpoints.
- Metadatos de la API y anotaciones como `@Operation`, `@APIResponse` y `@Tag`.
- Interfaz Swagger UI para explorar y probar los endpoints.

## Archivos nuevos

| Archivo | Qué demuestra |
|---|---|
| `api/DocumentacionResource.java` | Metadatos OpenAPI y documentación de un endpoint |
| `api/ActividadResource.java` | Etiquetas, operaciones y respuestas HTTP de un CRUD práctico |
| `src/test/java/com/inklusport/api/OpenApiResourceTest.java` | Comprueba que la especificación incluye endpoints previos |

## Rutas

| Ruta | Uso |
|---|---|
| `/q/openapi` | Especificación generada en YAML o JSON |
| `/q/swagger-ui` | Interfaz web para explorar la API en desarrollo |
| `/api/documentacion` | Devuelve enlaces a las dos herramientas |

## Ejecutar

```powershell
mvn quarkus:dev
```

Abre `http://localhost:8080/q/swagger-ui` en el navegador o consulta la
especificación con `curl.exe http://localhost:8080/q/openapi`.

```powershell
mvn -o test
```

## Siguiente paso

[11 · Using the REST Client](../11-rest-client) consume los endpoints desde
otro componente y muestra llamadas síncronas y asíncronas.
