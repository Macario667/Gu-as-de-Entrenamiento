# 03 · RESTEasy Classic

Guía oficial: [RESTEasy Classic](https://es.quarkus.io/guides/resteasy) — crear recursos HTTP con la implementación REST clásica de Quarkus.

## Qué aporta esta guía

El **motor clásico** de REST sobre Jakarta REST. Sirve para entender el modelo
"de manual" (se construye la respuesta entera con `Response`) y poder comparar
con el motor reactivo que se usa desde la guía 05.

> En esta carpeta el motor cambia a propósito: `quarkus-resteasy` en lugar de
> `quarkus-rest`. No se pueden usar los dos a la vez.

| Concepto clásico | Dónde se ve |
|---|---|
| `jakarta.ws.rs.core.Response` construido a mano (status, cabeceras, cuerpo) | `src/main/java/com/inklusport/api/EventoResource.java` |
| Parámetros con `@QueryParam`, `@PathParam`, `@HeaderParam`, `@CookieParam`, `@MatrixParam` | `src/main/java/com/inklusport/api/ParametrosResource.java` |
| `ExceptionMapper` para traducir excepciones de dominio | `src/main/java/com/inklusport/api/EventoNoEncontradoMapper.java` |
| `StreamingOutput` para respuestas por chunks | `src/main/java/com/inklusport/api/EventoStreamingResource.java` |
| JSON con Jackson (`quarkus-resteasy-jackson`) | `src/main/java/com/inklusport/model/Evento.java` |

## Estado acumulado

Hereda de [02 · Configuring Your Application](./../02-configuring-your-application):
`GreetingResource`, `GreetingService`, `AppConfig`, `InfoResource`,
`SaludoResource`, el CRUD acumulado de actividades y los perfiles `dev`/`prod`.
Todo eso sigue funcionando igual
porque el modelo de programación (`jakarta.ws.rs`) es el mismo.

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/hello`, `/hello/greeting/{name}` | Heredado de la guía 01 |
| `GET` | `/info`, `/saludo` | Heredado de la guía 02 |
| `GET` | `/api/eventos?deporte=` | Lista eventos (cabecera `X-Total-Count`) |
| `GET` | `/api/eventos/{id}` | Un evento |
| `POST` | `/api/eventos` | Crea evento (201 + `Location`) |
| `PUT` | `/api/eventos/{id}` | Actualiza evento |
| `DELETE` | `/api/eventos/{id}` | Elimina evento (204) |
| `GET` | `/api/eventos/informe` | Informe en streaming (chunked) |
| `GET` | `/api/parametros` | Parámetros de query |
| `GET` | `/api/parametros/cabeceras` | Parámetros de cabecera |
| `GET` | `/api/parametros/cookies` | Parámetros de cookie |
| `GET` | `/api/parametros/matriz;formato={formato}` | Parámetros de matriz |

## Ejecutar

```powershell
mvn quarkus:dev
```

```powershell
curl.exe http://localhost:8080/api/eventos
curl.exe -X POST http://localhost:8080/api/eventos `
  -H "Content-Type: application/json" `
  -d '{"nombre":"Torneo deGoal","deporte":"fútbol","capacidad":30}'
```

## Pruebas

```powershell
mvn test
```

## Siguiente paso

[04 · Configuration Reference Guide](./../04-configuration-reference) → todas las
propiedades de Quarkus y cómo se combinan sus fuentes.
