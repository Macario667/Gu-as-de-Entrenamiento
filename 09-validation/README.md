# 09 · Validation with Hibernate Validator

Guía oficial: [Validation with Hibernate Validator](https://quarkus.io/guides/validation) — declarar restricciones en Java y validar automáticamente solicitudes.

## Progresión acumulada

Esta carpeta se copia de la guía 08 y añade validación de entrada. Conserva
todo lo anterior: aplicación inicial, configuración, RESTEasy Classic,
referencia de configuración, Quarkus REST, logging, JSON y serialización JSON
en servicios REST. Se conserva además el CRUD acumulado de actividades, con
filtro, operaciones de escritura y respuestas de error.

## Qué aporta esta guía

- La extensión `quarkus-hibernate-validator`.
- Restricciones declarativas como `@NotBlank`, `@Size`, `@Min` y `@Max`.
- `@Valid` en el parámetro del endpoint para activar la validación del cuerpo.
- Respuestas HTTP 400 cuando los datos recibidos no cumplen las restricciones.

## Archivos nuevos

| Archivo | Qué demuestra |
|---|---|
| `model/EventoValidadoInput.java` | Restricciones sobre los datos de entrada |
| `api/ValidacionResource.java` | Validación automática del cuerpo JSON con `@Valid` |
| `src/test/java/com/inklusport/api/ValidacionResourceTest.java` | Casos válido, nombre vacío y capacidad fuera de rango |

## Endpoint

| Método | Ruta | Resultado |
|---|---|---|
| POST | `/api/validacion/eventos` | Devuelve el evento si es válido; si no, responde 400 |

## Ejecutar y probar

```powershell
mvn quarkus:dev
```

```powershell
curl.exe -X POST http://localhost:8080/api/validacion/eventos `
  -H "Content-Type: application/json" `
  -d '{"nombre":"Torneo inclusivo","deporte":"baloncesto","capacidad":40}'
```

```powershell
mvn -o test
```

## Siguiente paso

[10 · Using OpenAPI and Swagger UI](../10-openapi-swaggerui) documenta y permite
explorar los endpoints que ya existen.
