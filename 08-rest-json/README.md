# 08 · Writing JSON REST Services

Guía oficial: [Writing JSON REST Services](https://quarkus.io/guides/rest-json) — personalizar el binding JSON de endpoints con Jackson.

## Qué aporta esta guía

Personalización del JSON que entra y sale de los servicios REST con Jackson:
renombrar propiedades, omitir nulos, formatear fechas, ignorar campos
desconocidos, registrar serializadores propios y configurar el `ObjectMapper`.

## Contenido acumulado en esta carpeta

Todo lo de las guías 01–07: aplicación inicial, CRUD de actividades,
configuración, RESTEasy Classic, referencia de configuración, Quarkus REST,
logging y fundamentos JSON.

## Archivos nuevos de esta guía

| Archivo | Concepto |
|---|---|
| `model/EventoJson.java` | DTO con nombres y formato de campos controlados por Jackson |
| `json/JacksonConfig.java` | Configuración global del `ObjectMapper` |
| `json/PrecioSerializer.java` | Serializador y deserializador personalizados |
| `api/JsonRestResource.java` | Endpoints que muestran la serialización JSON en un servicio REST |
| `src/test/java/com/inklusport/json/JsonRestResourceTest.java` | Pruebas de los comportamientos JSON |

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/json-rest/eventos` | DTOs con propiedades renombradas y fechas formateadas |
| POST | `/api/json-rest/eventos` | Campos desconocidos ignorados y valores nulos omitidos |
| GET | `/api/json-rest/precio` | Serializador personalizado |
| GET | `/api/json-rest/config` | Configuración efectiva de Jackson |
| GET | `/api/json-rest/json-crudo` | JSON escrito directamente |

## Detalles que conviene recordar

- `Json.createWriter(...)` **no** produce salida con sangría: para eso hay que
  usar `Json.createWriterFactory(Map.of(JsonGenerator.PRETTY_PRINTING, true))`.
- `JsonArray.getValuesAs(String.class)` no compila: el tipo debe ser un
  `JsonValue` (`JsonString.class`), y luego se convierte con
  `JsonString::getString`.
- Un `JsonObject` devuelto dentro de un `Map` **no** lo serializa Jackson
  como JSON: hay que convertirlo a `Map`/`List` (lo hace `JsonUtil.objetoAMapa`).
- En las peticiones de texto, indica siempre el charset:
  `Content-Type: text/plain; charset=UTF-8`, si no los acentos se corrompen.

## Ejecución

```powershell
mvn quarkus:dev
curl.exe http://localhost:8080/api/json-rest/eventos
curl.exe http://localhost:8080/api/json-rest/precio
curl.exe http://localhost:8080/api/json-rest/config
curl.exe -X POST http://localhost:8080/api/json-rest/eventos -H "Content-Type: application/json" -d '{"nombre":"Torneo","disciplina":"baloncesto","plazas":20,"campoDesconocido":"se ignora"}'
```

## Pruebas

```powershell
mvn -o test
```

Resultado actual: **37 pruebas, 0 fallos**.

## Siguiente paso

Siguiente: [09 · Validation with Hibernate Validator](../09-validation).
