# 06 · Configuring Logging

Guía oficial: [Configuring Logging](https://quarkus.io/guides/logging) — configurar niveles, categorías, formatos, destinos y correlación de logs.

## Qué aporta esta guía

El logging en Quarkus se apoya en **JBoss LogManager** y se configura con
propiedades `quarkus.log.*`. Esta guía cubre:

- **Niveles** (TRACE, DEBUG, INFO, WARN, ERROR, FATAL) globales y por categoría.
- **Formatos** de salida: texto plano en consola y **JSON** (con la extensión
  `quarkus-logging-json`), pensado para ELK/Loki/Datadog.
- **Salida a fichero** con rotación por tamaño.
- **MDC** (Mapped Diagnostic Context) para correlacionar todos los logs de una
  misma petición con un identificador.

## Contenido acumulado en esta carpeta

Todo lo de las guías 01–05 más lo de esta guía:

- **01** `GreetingResource` / `GreetingService` · **02** `AppConfig` y perfiles
  · **03** CRUD classic con `ExceptionMapper` · **04** fuente de configuración
  propia · **05** motor reactivo, `RestResponse`, `Multi`, SSE, `Uni`.
- También conserva el CRUD de actividades de la guía 01 para probar el resto
  de los temas sobre una API de dominio.

## Archivos nuevos de esta guía

| Archivo | Concepto |
|---|---|
| `logging/RequestIdFilter.java` | `ContainerRequestFilter` + `ContainerResponseFilter` que asignan un id de petición, lo propagan en la cabecera `X-Request-Id` y lo guardan en el MDC |
| `logging/LogFiltro.java` | Una línea de log por petición: método, ruta, estado y duración |
| `api/LoggingResource.java` | Endpoints para probar niveles, MDC, log estructurado y errores |
| `src/test/.../LoggingResourceTest.java` | 5 tests del guía |

## Configuración aplicada

```properties
quarkus.log.level=INFO
quarkus.log.category."com.inklusport".level=DEBUG
quarkus.log.category."com.inklusport.service".level=INFO
quarkus.log.console.format=%d{HH:mm:ss,SSS} %-5p [%c{2.}] (%t) %s%e%n
quarkus.log.file.enable=true
quarkus.log.file.path=logs/inklusport.log
quarkus.log.file.rotation.max-file-size=10M
quarkus.log.file.rotation.max-backup-index=5
quarkus.log.console.json.enabled=false
quarkus.log.console.json.mdc.flat-fields=true
quarkus.log.console.json.additional-field."servicio"=inklusport
```

Notas importantes:

- `quarkus.log.console.json.excluded-keys` (no `exclude-keys`) es la clave real
  para excluir campos del JSON.
- `quarkus.log.file.rotation.file-suffix` es un **patrón de fecha**, no un
  nombre de fichero: un valor como `.%i.gz` provoca un
  `Illegal pattern character 'i'` al arrancar.
- El formato de texto de consola no incluye el MDC; para verlo hay que activar
  el formato JSON (`quarkus.log.console.json.enabled=true`), que lo añade como
  campos (`mdc.flat-fields=true`) o como objeto `mdc`.

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/logging/niveles?nivel=DEBUG` | Escribe en el nivel indicado (TRACE…FATAL) |
| GET | `/api/logging/mdc?usuario=ana` | Añade claves al MDC y las devuelve |
| GET | `/api/logging/estructurado` | Log `clave=valor` indexable |
| GET | `/api/logging/error` | Lanza una excepción para ver la traza |
| GET | `/api/logging/eventos` | Log con datos de negocio |

## Ejecución

```powershell
mvn quarkus:dev
curl.exe "http://localhost:8080/api/logging/niveles?nivel=DEBUG"
curl.exe "http://localhost:8080/api/logging/mdc?usuario=ana" -i   # fíjate en X-Request-Id
Get-Content logs\inklusport.log -Tail 20
```

## Pruebas

```powershell
mvn -o test
```

Resultado actual: **23 tests, 0 fallos**.

## Siguiente paso

[07 · Conocimientos JSON](../07-json) — comprender estructuras, tipos y
operaciones de lectura y construcción de documentos JSON.
