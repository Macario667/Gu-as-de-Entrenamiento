# 05 · Writing REST Services with RESTEasy Reactive (Quarkus REST)

Guía oficial: [Writing REST Services with RESTEasy Reactive](https://quarkus.io/guides/rest) — crear endpoints con Quarkus REST, incluyendo respuestas reactivas y streaming.

## Qué aporta esta guía

La guía 03 introdujo **RESTEasy Classic**, el motor clásico de Jakarta REST en
Quarkus. Esta guía presenta **RESTEasy Reactive**, que desde Quarkus 3.9 se
llama **Quarkus REST** (`quarkus-rest`): el motor recomendado y el que se usa
a partir de aquí en todas las guías siguientes.

Las diferencias clave son:

| Tema | RESTEasy Classic (guía 03) | Quarkus REST (esta guía) |
|---|---|---|
| Dependencias | `quarkus-resteasy` | `quarkus-rest` |
| Modelo | Bloqueante (hilo por petición) | Reactivo / no bloqueante |
| Respuesta | `jakarta.ws.rs.core.Response` | `org.jboss.resteasy.reactive.RestResponse` |
| Parámetros | `@QueryParam`, `@PathParam`… | `@RestQuery`, `@RestPath`, `@RestHeader`, `@RestCookie`, `@RestMatrix` |
| Streaming | `StreamingOutput` | `Multi<T>` (Mutiny) + `@RestMulti` |
| Asíncrono | callbacks | `Uni<T>` / `Multi<T>` |

> **Importante:** los dos motores (`quarkus-resteasy` y `quarkus-rest`) no pueden
> coexistir en el mismo proyecto. Por eso esta carpeta ya **no** incluye
> `quarkus-resteasy` y todas las guías siguientes heredan esta decisión.

## Contenido acumulado en esta carpeta

Esta carpeta contiene **todo lo de las guías 01–04** más lo de esta guía:

- **01 Getting Started** — `GreetingResource`, `GreetingService`, primer test.
- **01 Ejemplo práctico** — CRUD de actividades con búsqueda, validación básica
  y pruebas de ciclo de vida; se conserva al avanzar por las siguientes guías.
- **02 Configuring Your Application** — `AppConfig` (`@ConfigMapping`), perfiles
  `dev`/`prod`, `InfoResource`, `SaludoResource`.
- **03 RESTEasy Classic** — modelo `Evento`/`EventoInput`, `EventoService`,
  `EventoResource`, `EventoNoEncontradoException` + `ExceptionMapper`.
- **04 Configuration Reference** — `EntornoConfigSource` (fuente de
  configuración propia), `EntornoConfigSourceProvider`, `ConfigReferenceResource`.
- **05 RESTEasy Reactive** — motor `quarkus-rest`, `RestResponse`, streaming
  con `Multi`, SSE, `Uni` y parámetros reactivos.

## Archivos nuevos de esta guía

| Archivo | Concepto |
|---|---|
| `api/EventoResource.java` | CRUD con `RestResponse`, `@RestQuery`, `@Cache`, `@NoCache`, `@ResponseStatus`, `@ResponseHeader` |
| `api/EventoStreamingResource.java` | `Multi<T>` (Mutiny) y `RestMulti` con `encodeAsJsonArray`, `status` y `header` |
| `api/EventoSseResource.java` | Server-Sent Events con `jakarta.ws.rs.sse.Sse` y `OutboundSseEvent` |
| `api/BusquedaResource.java` | Respuestas asíncronas con `Uni<T>` y anotación `@Blocking` |
| `api/ParametrosResource.java` | `@RestQuery`, `@RestHeader`, `@RestCookie`, `@RestMatrix` y agrupación con `@BeanParam` |
| `service/EventoService.java` | Nuevo `buscarAsync(Long)` que devuelve un `Uni<Evento>` |
| `api/EventoResourceTest.java` | Tests del CRUD reactivo (paginación, caché, 303, 404) |
| `api/EventoStreamingResourceTest.java` | Tests de streaming, SSE y respuestas `Uni` |

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/eventos?offset=0&limit=20` | Listado paginado, con `Cache-Control: max-age=60` y `X-Total-Count` |
| GET | `/api/eventos/buscar?deporte=baloncesto` | Filtro por deporte |
| GET | `/api/eventos/{id}` | Evento por id (404 JSON si no existe) |
| GET | `/api/eventos/{id}/detalle` | Detalle sin caché (`@NoCache`) |
| POST | `/api/eventos` | Alta (201 + `X-Created-By`) |
| PUT | `/api/eventos/{id}` | Actualización |
| DELETE | `/api/eventos/{id}` | Baja (204) |
| POST | `/api/eventos/{id}/acciones/crear` | Redirección 303 See Other con `Location` |
| GET | `/api/stream/eventos` | `Multi<Evento>` serializado como array JSON |
| GET | `/api/stream/eventos-plano` | Un objeto JSON por elemento (`encodeAsJsonArray(false)`) |
| GET | `/api/stream/eventos-personalizado` | Streaming con status 207 y cabecera propia |
| GET | `/api/stream/secuencia` | Secuencia retardada elemento a elemento |
| GET | `/api/sse/reloj`, `/api/sse/eventos` | Server-Sent Events |
| GET | `/api/reactivo/eventos/{id}` | Respuesta asíncrona con `Uni` |
| GET | `/api/parametros/**` | Ejemplos de query, header, cookie, matriz y `@BeanParam` |

## Ejecución

```powershell
mvn quarkus:dev            # http://localhost:8080
curl.exe -X POST http://localhost:8080/api/eventos -H "Content-Type: application/json" -d '{"nombre":"Maratón","deporte":"atletismo","capacidad":50}'
curl.exe -N http://localhost:8080/api/sse/reloj
curl.exe http://localhost:8080/api/stream/eventos
```

## Pruebas

```powershell
mvn -o test
```

Resultado actual: **18 tests, 0 fallos** (4 de la guía 03/04 heredados + los
nuevos de esta guía).

## Siguiente paso

[06 · Configuring Logging](../06-logging) — configurar niveles, formato,
archivos y contexto de petición para los logs.
