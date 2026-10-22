# 07 · Conocimientos JSON

Guía: [Conocimientos JSON](https://www.nylas.com/blog/the-complete-guide-to-working-with-json/) — repasar tipos, estructuras, parseo y construcción de JSON.

## Qué aporta esta guía

Repaso de los fundamentos de **JSON** (RFC 8259) y de las herramientas para
trabajar con él en Quarkus:

- Los **seis tipos** de dato de JSON: object, array, string, number, boolean y
  null.
- **Construcción** de documentos con `JsonObjectBuilder` / `JsonArrayBuilder`
  (API JSON-P, `jakarta.json`).
- **Parseo** de texto JSON a estructuras y de estructuras a texto (compacto o
  con sangría).
- **JSON Pointer** (RFC 6901) para acceder a valores anidados.
- Diferencia entre **JSON-P** (API de objetos), **JSON-B** (binding a POJOs)
  y **Jackson** (binding y anotaciones, que es lo que usa Quarkus REST por
  defecto).

## Contenido acumulado en esta carpeta

Todo lo de las guías 01–06 más lo de esta guía. También conserva el CRUD de
actividades con filtro, errores HTTP y ejemplos JSON.

## Archivos nuevos de esta guía

| Archivo | Concepto |
|---|---|
| `json/JsonUtil.java` | Utilidades JSON-P: documento de ejemplo, parseo, texto bonito/compacto, JSON Pointer, conversión a `Map`/`List` |
| `api/JsonResource.java` | Endpoints: estructura y tipos, constructor, texto bonito, compacto, puntero, parseo y arrays |
| `src/test/.../JsonResourceTest.java` | 8 tests del guía |

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/json/estructura` | Documento de ejemplo + tipo JSON detectado de cada clave |
| GET | `/api/json/constructor` | Documento anidado construido con la API fluida |
| GET | `/api/json/bonito` | Texto JSON con sangría (`JsonGenerator.PRETTY_PRINTING`) |
| GET | `/api/json/compactar` | Texto JSON sin espacios |
| GET | `/api/json/puntero?ruta=/sede/ciudad` | Acceso anidado con JSON Pointer |
| POST | `/api/json/parsear` | Recibe texto JSON (`text/plain`) y devuelve su estructura |
| POST | `/api/json/objeto` | Recibe un `JsonObject` directamente como cuerpo |
| GET | `/api/json/array` | Arrays homogeneos y heterogeneos |

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
curl.exe http://localhost:8080/api/json/estructura
curl.exe "http://localhost:8080/api/json/puntero?ruta=/sede/pistas"
curl.exe -X POST http://localhost:8080/api/json/parsear -H "Content-Type: text/plain; charset=UTF-8" -d '{"nombre":"Torneo"}'
```

## Pruebas

```powershell
mvn -o test
```

Resultado actual: **31 tests, 0 fallos**.

## Siguiente paso

[08 · Writing JSON REST Services](../08-rest-json) — controlar nombres,
formatos y valores de JSON producido o recibido por endpoints REST.
