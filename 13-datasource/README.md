# 13 · Proyecto integrador Quarkus

Guía oficial de referencia: [Configure data sources in Quarkus](https://quarkus.io/guides/datasource). El tema datasource queda pendiente de estudio e implementación.

La guía de inicio de Quarkus ([Creating Your First Application](https://quarkus.io/guides/getting-started) — crear, ejecutar y probar una aplicación) explica el punto de partida. Esta carpeta reúne ese fundamento y los temas trabajados en las guías 01–12 en **una sola aplicación ejecutable**, con APIs de ejemplo, configuración, logs, validación, JSON, OpenAPI, cliente REST y la extensión de tolerancia a fallos preparada.

## Qué incluye el proyecto integrador

| Tema de las guías anteriores | Dónde practicarlo |
|---|---|
| Aplicación Quarkus, CDI, recursos e inyección | `/hello`, `GreetingResource`, `GreetingService` |
| Configuración tipada, perfiles y fuentes | `/info`, `/saludo`, `/config`, `AppConfig`, perfiles `dev` y `prod`, `EntornoConfigSource` |
| RESTEasy Classic frente a Quarkus REST | `EventoResource` muestra Quarkus REST; la sección de comparación explica por qué no se pueden activar ambos motores a la vez |
| CRUD, parámetros, estados HTTP y errores | `/api/actividades`, `/api/eventos`, `/api/parametros/**`, `EventoNoEncontradoMapper` |
| Reactividad, streaming y SSE | `/api/reactivo/**`, `/api/stream/**`, `/api/sse/**` |
| Logging, niveles, filtros y correlación | `/api/logging/**`, consola y `logs/inklusport.log`, cabecera `X-Request-Id` |
| JSON y JSON-P | `/api/json/**`, `JsonUtil` |
| Serialización/deserialización JSON con Jackson | `/api/json-rest/**`, `EventoJson`, `PrecioSerializer`, `JacksonConfig` |
| Validación automática | `POST /api/validacion/eventos`, `EventoValidadoInput` |
| OpenAPI y explorador interactivo | `/q/openapi`, `/q/swagger-ui` |
| REST Client síncrono y asíncrono | `/api/cliente/eventos/sync` y `/async` |
| SmallRye Fault Tolerance | `/api/resiliencia/**`, `ResilienciaService`, políticas en `EventosClient` y pruebas de resiliencia |

La aplicación usa datos en memoria: al reiniciarla se restauran los eventos y
actividades iniciales. **La etapa de datasource todavía no está implementada**;
la guía de referencia se usará para estudiarla por separado, mientras esta
estructura conserva los conceptos de las doce guías anteriores.

## Requisitos y cómo ejecutar

Se requiere JDK 17 o compatible con `maven.compiler.release` y Maven instalado.
Este proyecto no incluye Maven Wrapper.

En PowerShell, entra en esta carpeta:

```powershell
Set-Location "C:\Users\Samu\OneDrive\Desktop\Guías de Entrenamiento\13-datasource"
```

Inicia Quarkus en modo desarrollo:

```powershell
mvn quarkus:dev
```

Espera a que la consola confirme que Quarkus está escuchando en
`http://localhost:8080`. Mantén esa terminal abierta; Quarkus recarga los
cambios mientras desarrollas. Haz las peticiones desde otra terminal y detén
la aplicación con **Ctrl+C**.

## Probar el CRUD principal

Lista las actividades, filtra por deporte y crea una:

```powershell
curl.exe http://localhost:8080/api/actividades
curl.exe "http://localhost:8080/api/actividades?deporte=balon"
curl.exe -i -X POST http://localhost:8080/api/actividades `
  -H "Content-Type: application/json" `
  -d '{"nombre":"Torneo inclusivo","deporte":"baloncesto","capacidad":32}'
```

La creación responde `201 Created` y devuelve una cabecera `Location` con el
identificador. Usa ese id para probar `GET /api/actividades/{id}`,
`PUT /api/actividades/{id}` y `DELETE /api/actividades/{id}`. Por ejemplo, con
el id `4`:

```powershell
curl.exe http://localhost:8080/api/actividades/4
curl.exe -i -X PUT http://localhost:8080/api/actividades/4 `
  -H "Content-Type: application/json" `
  -d '{"nombre":"Torneo inclusivo 2027","deporte":"baloncesto","capacidad":40}'
curl.exe -i -X DELETE http://localhost:8080/api/actividades/4
```

El CRUD rechaza nombre/deporte vacíos y capacidad fuera de `1..500` con `400`;
un id inexistente responde `404`. La guía también conserva el segundo CRUD,
`/api/eventos`, que permite comparar respuestas Jakarta `Response` con
`RestResponse`, caché, paginación, `ExceptionMapper` y redirecciones.

## Probar la configuración y los perfiles

Consulta el valor efectivo, su fuente y su ordinal; revisa los tipos compuestos:

```powershell
curl.exe http://localhost:8080/info
curl.exe http://localhost:8080/saludo
curl.exe http://localhost:8080/config
curl.exe http://localhost:8080/config/fuentes
curl.exe http://localhost:8080/config/tipos
```

En modo desarrollo, el perfil `dev` se activa automáticamente. También puedes
elegirlo de forma explícita:

```powershell
mvn quarkus:dev "-Dquarkus.profile=dev"
```

Para comprobar la precedencia de fuentes en PowerShell, define una variable
temporal y vuelve a consultar `/config`:

```powershell
$env:APP_NOMBRE = "InkluSport desde entorno"
curl.exe http://localhost:8080/config
Remove-Item Env:APP_NOMBRE
```

Las fuentes de mayor prioridad pueden sobreescribir las de menor prioridad:
fuente personalizada (ordinal 100), propiedades de aplicación (250), variables
de entorno (300) y propiedades Java `-D` (400). Usa valores demostrativos; no
guardes credenciales en el repositorio.

## Modo dev frente a modo prod

| Modo | Para qué sirve | Configuración de este proyecto |
|---|---|---|
| `dev` | Desarrollo local: live reload, mensajes de diagnóstico y Swagger UI disponible para explorar la API. | Quarkus usa `application-dev.properties`; `app.nombre` indica `DEV`. |
| `prod` | Ejecutar el artefacto empaquetado, sin live reload y con el perfil de producción. | `application-prod.properties` cambia `app.nombre` y `app.modo`. |

Para probar de verdad el perfil de producción, primero detén `quarkus:dev` y
empaqueta la aplicación:

```powershell
mvn package
java -Dquarkus.profile=prod -jar target\quarkus-app\quarkus-run.jar
```

En otra terminal consulta `curl.exe http://localhost:8080/info` y
`curl.exe http://localhost:8080/saludo`; deben mostrar el perfil `PROD`. La
interfaz Swagger UI es una herramienta de desarrollo y, por defecto, no se
incluye en producción. No uses el modo dev como servidor de producción.

## Explorar y probar HTTP con Swagger UI

Con `mvn quarkus:dev` ejecutándose, abre
`http://localhost:8080/q/swagger-ui`. Es el **explorador interactivo** de la
API: despliega cada ruta y sus métodos, muestra parámetros, cuerpos JSON,
respuestas y códigos HTTP. Pulsa **Try it out**, completa los datos y luego
**Execute** para enviar una petición real a esta aplicación y ver su estado,
cabeceras y respuesta. Por ejemplo, busca `Actividades`, prueba el filtro y
crea un registro.

La especificación generada se consulta en
`http://localhost:8080/q/openapi`; los enlaces también están en
`GET /api/documentacion`. Si Swagger UI no abre, confirma que estás en modo
dev, que la aplicación arrancó correctamente y que estás usando el puerto 8080.

## JSON, objetos y serialización

**Serializar** significa convertir un objeto Java en JSON para enviarlo en una
respuesta HTTP. **Deserializar** es leer el JSON recibido y convertirlo en un
objeto Java. En Quarkus REST, Jackson hace estas conversiones para los
endpoints JSON:

```powershell
curl.exe http://localhost:8080/api/json/estructura
curl.exe http://localhost:8080/api/json-rest/eventos
curl.exe http://localhost:8080/api/json-rest/precio
curl.exe -X POST http://localhost:8080/api/json-rest/precio/eco `
  -H "Content-Type: application/json" `
  -d '"12.50 EUR"'
```

- **JSON-P** (`JsonUtil`) construye y lee estructuras JSON explícitamente:
  objetos, arrays, tipos, JSON Pointer y texto con o sin sangría.
- **Jackson** enlaza JSON con DTOs Java. `EventoJson` renombra `deporte` como
  `disciplina`, formatea la fecha, omite nulos e ignora propiedades
  desconocidas. `PrecioSerializer` demuestra una conversión personalizada:
  el objeto `Precio` se representa como el texto JSON `"12.50 EUR"` y puede
  reconstruirse al recibirlo.
- **JSON-B** es otra API de binding Java/JSON existente en el ecosistema
  Jakarta; este proyecto usa Jackson, no JSON-B.

Consulta `/api/json-rest/config` para ver la configuración efectiva del
`ObjectMapper`. El formato JSON de logs es independiente del JSON de la API:
`quarkus-logging-json` convierte **líneas de log** a JSON; Jackson convierte
**datos de la aplicación** a JSON.

## Validación, logging, streaming y REST Client

Envíale una entrada inválida al endpoint de validación; espera `400`:

```powershell
curl.exe -i -X POST http://localhost:8080/api/validacion/eventos `
  -H "Content-Type: application/json" `
  -d '{"nombre":" ","deporte":"baloncesto","capacidad":0}'
```

Las restricciones viven en `EventoValidadoInput`; `@Valid` en el recurso activa
la validación antes de ejecutar su método. Para comparar con los chequeos
manuales del CRUD, prueba también una capacidad inválida en `/api/actividades`.

Observa logs y la cabecera de correlación:

```powershell
curl.exe -i "http://localhost:8080/api/logging/niveles?nivel=DEBUG"
curl.exe -i "http://localhost:8080/api/logging/mdc?usuario=ana"
Get-Content logs\inklusport.log -Tail 30
```

El filtro genera o conserva `X-Request-Id`; el MDC adjunta contexto a los
mensajes de esa petición. Las propiedades en `application.properties`
configuran el nivel, las categorías, la salida a fichero y el formato JSON
(desactivado por defecto para mantener legible la consola).

Prueba transmisión, Server-Sent Events (SSE) y las variantes de consumo REST:

```powershell
curl.exe http://localhost:8080/api/stream/eventos
curl.exe http://localhost:8080/api/reactivo/eventos/1
curl.exe http://localhost:8080/api/cliente/eventos/sync
curl.exe http://localhost:8080/api/cliente/eventos/async
curl.exe -N http://localhost:8080/api/sse/reloj
```

`Uni` representa un resultado que llegará después; `Multi` transmite varios
elementos; SSE mantiene una conexión para que el servidor envíe eventos. El
REST Client muestra una interfaz tipada (`EventosClient`) con una llamada que
espera el resultado y otra que devuelve `CompletionStage`. En esta práctica,
ambas consumen `/api/eventos` de la propia aplicación; no se necesita un
servicio externo.

## Qué significa RESTEasy Classic en este recorrido

RESTEasy Classic y Quarkus REST son motores distintos y **no se deben incluir
juntos en la misma aplicación**. Las etapas anteriores tienen un proyecto
Classic (`03-resteasy-classic`) para probar ese motor. Esta aplicación final
usa Quarkus REST (`quarkus-rest`) para que streaming, `Uni`, `Multi` y el
cliente REST funcionen en un único runtime.

Jakarta REST (`@Path`, `@GET`, `@QueryParam`, `Response`) es la API común que
permite reconocer código compartido entre motores; no significa que el motor
Classic esté instalado aquí. Compara `03-resteasy-classic` con
`05-rest-resteasy-reactive` si quieres arrancar cada implementación por
separado.

## Pruebas automatizadas

Detén el modo dev antes de correr la suite completa:

```powershell
mvn -o test
```

Las pruebas usan `@QuarkusTest` y RestAssured. Cubren configuración, saludo,
CRUD, errores HTTP, validación, OpenAPI, JSON, logs, streaming, SSE y los dos
métodos del REST Client. `-o` solicita modo sin conexión y usa las dependencias
ya descargadas; si Maven indica que falta alguna, ejecútalo como `mvn test`.

## Para qué sirve cada archivo

### Raíz, dependencias y configuración

| Archivo | Función |
|---|---|
| `pom.xml` | Dependencias Quarkus, extensiones REST/Jackson/JSON-P/validación/OpenAPI/REST Client, Java 17 y comandos Maven. |
| `.gitignore` | Evita guardar artefactos de compilación y archivos locales en el control de versiones. |
| `src/main/resources/application.properties` | Configuración base, perfiles, logging, Jackson, puerto, OpenAPI y URL del REST Client. |
| `src/main/resources/application-dev.properties` | Valores que sustituyen a los base en desarrollo (`DEV`). |
| `src/main/resources/application-prod.properties` | Valores del perfil de producción (`PROD`). |
| `src/main/resources/META-INF/services/org.eclipse.microprofile.config.spi.ConfigSourceProvider` | Registra la fuente de configuración personalizada para que SmallRye Config la descubra. |

### Java de la aplicación

El código está separado por responsabilidades. Una petición normalmente entra
por **`api`**; el recurso lee parámetros o el cuerpo HTTP y llama a **`service`**.
El servicio aplica la lógica de negocio y usa los tipos de **`model`** para los
datos. Quarkus convierte esos modelos a/desde JSON con Jackson, salvo los
ejercicios que manipulan JSON-P explícitamente en **`json`**. Los filtros de
**`logging`** rodean la petición y añaden observabilidad, y **`client`** contiene
las interfaces para llamar a otra API.

| Capa | Para qué sirve |
|---|---|
| `api/` | Adaptadores HTTP: rutas, verbos, parámetros, cuerpos, códigos de estado y cabeceras. Evita poner la lógica de negocio principal en los endpoints. |
| `service/` | Lógica de aplicación y acceso a los datos de demostración en memoria; no conoce detalles de cómo el cliente envió HTTP. |
| `model/` | Modelos y DTOs: representan los datos internos, los cuerpos de entrada/salida y las reglas de conversión o validación asociadas a esos datos. |
| `client/` | Contratos REST tipados para consumir otros servicios HTTP; declara rutas y tipos de respuesta sin mezclar esa llamada con el recurso que atiende al usuario. |
| `config/` | Fuentes de configuración y endpoints de aprendizaje que permiten inspeccionar su valor y prioridad. |
| `json/` | Operaciones explícitas JSON-P y personalización de Jackson: serialización, deserialización, fechas, propiedades y formatos propios. |
| `logging/` | Filtros que observan las peticiones, miden su duración, propagan el identificador de petición y agregan campos MDC a los logs. |
| `com.inklusport/` | Ejemplos iniciales CDI/configuración (`GreetingService`, `AppConfig`) y recursos pequeños que exponen esa configuración. |
| `src/test/` | Pruebas HTTP automatizadas que verifican rutas, estados, encabezados y comportamientos de cada capa. |

### Cómo se conectan las capas: flujo de una petición

Las capas colaboran en una petición, pero cada una tiene una responsabilidad:
HTTP entra por `api`, la lógica y los datos se resuelven en `service` y `model`,
y Quarkus convierte el resultado a la respuesta HTTP. En esta aplicación, un
ejemplo completo es crear una actividad:

```text
Cliente HTTP / Swagger UI
        │ POST /api/actividades + JSON
        ▼
Filtros logging (RequestIdFilter y LogFiltro)
        │ correlacionan y registran la petición
        ▼
Quarkus REST + Jackson
        │ deserializa JSON → ActividadInput
        ▼
api/ActividadResource
        │ comprueba datos y delega la operación
        ▼
service/ActividadService
        │ aplica lógica y crea el registro
        ▼
model/Actividad
        │ objeto con el resultado
        ▼
Jackson serializa Actividad → JSON
        │ respuesta HTTP 201 + Location
        ▼
Cliente HTTP / Swagger UI
```

En detalle:

1. El cliente —`curl.exe`, Swagger UI u otro programa— envía método, ruta,
   cabeceras y, para un `POST`, un cuerpo JSON.
2. Los filtros de `logging/` observan la petición: conservan o generan
   `X-Request-Id`, añaden datos al MDC y registran método, ruta, estado y
   duración. No deciden el resultado funcional del CRUD.
3. Quarkus REST resuelve `@Path`, el verbo HTTP y los parámetros. Jackson
   convierte el cuerpo JSON en el DTO `ActividadInput`. Si el tipo de contenido
   o el cuerpo no son adecuados, la petición puede rechazarse antes de la
   lógica del recurso.
4. `api/ActividadResource` aplica las validaciones manuales de este CRUD.
   Cuando son correctas, llama a `service/ActividadService`; si no, construye
   HTTP 400. Una consulta de id inexistente produce HTTP 404.
5. `ActividadService` aplica las reglas de la operación y accede al mapa en
   memoria. Crea o busca un `model/Actividad` y lo devuelve al recurso; el
   servicio no construye la respuesta HTTP.
6. El recurso traduce el resultado al protocolo: por ejemplo, `201 Created`,
   cuerpo JSON y cabecera `Location`. Jackson serializa el objeto Java de
   salida como JSON y Quarkus REST envía la respuesta. Los filtros completan
   sus registros de salida.

Otros recorridos muestran variaciones de esta misma colaboración:

- **Validación declarativa:** `POST /api/validacion/eventos` deserializa el
  cuerpo a `EventoValidadoInput`. `@Valid` hace que Hibernate Validator revise
  las restricciones antes de ejecutar `ValidacionResource`; si incumple una,
  Quarkus responde HTTP 400 sin pasar por el método del recurso.
- **Errores de eventos:** `EventoResource` llama al servicio. Si el evento no
  existe, se lanza `EventoNoEncontradoException`; `EventoNoEncontradoMapper`
  la traduce a HTTP 404 JSON. El mapper es el adaptador entre una excepción de
  dominio y el protocolo HTTP.
- **Serialización personalizada:** `JsonRestResource` recibe o devuelve DTOs.
  Jackson consulta `JacksonConfig` y `PrecioSerializer` para convertir
  `Precio` entre objeto Java y la cadena JSON `"12.50 EUR"`. En cambio,
  `JsonResource` usa `JsonUtil` para construir y recorrer estructuras mediante
  JSON-P directamente.
- **Consumo de otro endpoint:** `ClienteEventosResource` recibe la petición
  local y llama a `EventosClient`. El cliente REST construye una llamada HTTP a
  la URL configurada, Jackson transforma el JSON remoto a `Evento` y el
  resultado vuelve por el recurso local al cliente original. En esta práctica
  el destino es `/api/eventos` de la propia aplicación.
- **Configuración transversal:** `AppConfig`, las propiedades del perfil activo
  y `EntornoConfigSource` proveen valores a los componentes que los inyectan;
  no son una etapa obligatoria de cada operación CRUD. `application-dev.properties`
  y `application-prod.properties` sustituyen valores según el perfil.
- **Documentación interactiva:** SmallRye OpenAPI lee las anotaciones de los
  recursos y genera `/q/openapi`; Swagger UI muestra esa especificación y, al
  pulsar **Execute**, envía una petición HTTP real al mismo flujo descrito
  arriba. No sustituye al recurso ni al cliente REST.

Resumen del camino habitual:

```text
Petición → filtros → Quarkus REST/Jackson → api → service ↔ model
         → api/estado HTTP → Jackson → respuesta
```

`client/` se usa cuando la aplicación necesita iniciar otra petición HTTP;
`config/` suministra valores, `json/` personaliza el procesamiento JSON y
`logging/` observa el recorrido. Son colaboradores transversales o salientes,
no capas que deban ejecutarse siempre en ese orden.

Las rutas siguen el concepto REST: `@Path` define la URL, `@GET`/`@POST`/
`@PUT`/`@DELETE` el método HTTP, y las anotaciones de parámetros enlazan la
petición con Java. Los cuerpos JSON se convierten a DTO antes de entrar al
recurso; el objeto que devuelve el recurso se serializa en la respuesta.

#### Recursos raíz: aplicación inicial y configuración

| Archivo | Qué contiene y qué hace |
|---|---|
| `AppConfig.java` | Interfaz `@ConfigMapping(prefix = "app")`. Cada método representa una propiedad; SmallRye convierte valores a enum, `Duration`, listas y objetos anidados de autor. `@WithDefault` documenta y aporta valores cuando no hay otra fuente. |
| `GreetingResource.java` | Recurso `GET /hello` y `GET /hello/greeting/{name}`. Recibe el nombre mediante `@PathParam`, inyecta el servicio CDI y produce texto plano. |
| `GreetingService.java` | Bean `@ApplicationScoped` con la operación que construye el saludo. Demuestra que la lógica puede probarse y reutilizarse fuera del recurso HTTP. |
| `InfoResource.java` | `GET /info`: inyecta `AppConfig` y presenta nombre, versión, modo y autor como JSON. |
| `SaludoResource.java` | `GET /saludo`: combina `@ConfigProperty` para una propiedad individual con `AppConfig`; permite observar el valor del perfil activo. |

#### Capa `api/`: recursos HTTP

| Archivo | Qué contiene y qué hace |
|---|---|
| `api/ActividadResource.java` | CRUD de actividades. Implementa listar/filtrar por deporte, consultar por id, crear (201 y `Location`), actualizar y borrar (204). Comprueba campos y capacidad; contesta 400 ante datos inválidos y 404 ante ids inexistentes. Sus anotaciones OpenAPI describen las operaciones y respuestas. |
| `api/BusquedaResource.java` | Dos variantes de búsqueda por id: `Uni<Evento>` asíncrono y endpoint marcado `@Blocking` que simula trabajo bloqueante. Sirve para contrastar los modelos de ejecución, no como patrón para pausar hilos en producción. |
| `api/ClienteEventosResource.java` | Fachada HTTP local con `/sync` y `/async`. Inyecta `EventosClient`, delega la llamada y devuelve el resultado o el `CompletionStage` al solicitante. |
| `api/DocumentacionResource.java` | Define título, versión, descripción y etiquetas OpenAPI; también publica las rutas de `/q/openapi` y `/q/swagger-ui` en `GET /api/documentacion`. |
| `api/EventoNoEncontradoException.java` | Excepción de dominio que lleva el id del evento inexistente. Se lanza desde la lógica de consulta en lugar de construir la respuesta de error en cada punto. |
| `api/EventoNoEncontradoMapper.java` | `ExceptionMapper` global: captura esa excepción y la convierte en HTTP 404 con un cuerpo JSON. |
| `api/EventoResource.java` | CRUD alternativo de eventos con Quarkus REST. Demuestra paginación, `RestResponse`, `@RestQuery`/`@RestPath`, caché, cabeceras, 204, `ExceptionMapper` y redirección 303. |
| `api/EventoSseResource.java` | `GET /api/sse/reloj` y `/eventos`: emite mensajes SSE identificados por nombre/id a través de una conexión HTTP persistente. |
| `api/EventoStreamingResource.java` | Endpoints `Multi`: transmite eventos como array JSON, como objetos consecutivos o con cabecera/status personalizados; incluye una secuencia retardada. |
| `api/JsonResource.java` | Endpoints didácticos para descubrir tipos JSON, construir documentos, leer JSON Pointer, analizar texto JSON y tratar arrays y objetos con JSON-P. |
| `api/JsonRestResource.java` | Endpoints Jackson para DTOs con propiedades renombradas, fechas y nulos; devuelve JSON crudo, configuración del mapper y un precio serializado como cadena. `POST /precio/eco` comprueba además su deserialización. |
| `api/LoggingResource.java` | Endpoints de demostración que emiten TRACE/DEBUG/INFO/WARN/ERROR, escriben claves MDC, generan logs estructurados y provocan un error simulado para observar la traza. |
| `api/ParametrosResource.java` | Ejemplos de query, cabeceras, cookies y parámetros de matriz; también agrupa valores de query/header en un record con `@BeanParam`. |
| `api/ValidacionResource.java` | `POST /api/validacion/eventos`: recibe JSON, aplica `@Valid` al DTO y solo ejecuta el método si las restricciones se cumplen; Quarkus responde 400 cuando no. |

#### Cliente HTTP: `client/`

| Archivo | Qué contiene y qué hace |
|---|---|
| `client/EventosClient.java` | Interfaz `@RegisterRestClient(configKey = "eventos-api")`, base `/api/eventos` y dos métodos `GET`: uno bloquea hasta obtener `List<Evento>` y el otro devuelve `CompletionStage<List<Evento>>`. El host/puerto se configura en `application.properties`. |

#### Configuración: `config/`

| Archivo | Qué contiene y qué hace |
|---|---|
| `config/ConfigReferenceResource.java` | Rutas `/config`, `/config/fuentes` y `/config/tipos`: inspecciona el valor efectivo de `app.nombre`, el nombre/ordinal de sus fuentes y conversiones a duración, lista y enum. |
| `config/EntornoConfigSource.java` | Implementa `ConfigSource` con los valores didácticos `app.entorno` y `app.despliegue`, ordinal 100; permite ver cómo una fuente con prioridad menor puede ser sobrescrita. |
| `config/EntornoConfigSourceProvider.java` | Implementa `ConfigSourceProvider` y entrega la fuente personalizada a SmallRye Config al iniciar. No contiene la configuración en sí; la crea y registra. |

#### JSON y serialización: `json/`

| Archivo | Qué contiene y qué hace |
|---|---|
| `json/JacksonConfig.java` | Implementa `ObjectMapperCustomizer`. Registra el módulo propio, activa soporte de tipos `java.time`, escribe fechas como texto y permite propiedades de entrada desconocidas. |
| `json/JsonUtil.java` | Métodos reutilizables JSON-P: construye un documento con los seis tipos, parsea texto, genera salida compacta/bonita, resuelve JSON Pointer y convierte objetos/arrays a estructuras Java. |
| `json/PrecioSerializer.java` | `JsonSerializer<Precio>` convierte el valor a una cadena estable como `"12.50 EUR"`; el deserializador hace el recorrido inverso. Su módulo Jackson registra ambos sentidos. También contiene un serializer de fecha de ejemplo. |

#### Logging y diagnóstico: `logging/`

| Archivo | Qué contiene y qué hace |
|---|---|
| `logging/LogFiltro.java` | Filtro Jakarta REST de entrada/salida. Guarda el instante inicial y registra método, ruta, código HTTP y milisegundos al completar la respuesta. |
| `logging/RequestIdFilter.java` | Acepta `X-Request-Id` del cliente o crea uno, lo devuelve como cabecera y guarda id/ruta/método en MDC para correlacionar los logs de la petición. Limpia las claves principales al terminar. |

#### Modelos y DTOs: `model/`

| Archivo | Qué contiene y qué hace |
|---|---|
| `model/Actividad.java` | Datos de salida del CRUD de actividades: id, nombre, deporte y capacidad. El endpoint lo serializa a JSON. |
| `model/ActividadInput.java` | Campos que el CRUD acepta al crear o actualizar. Se separa del modelo de salida para no aceptar el id como dato del cliente. |
| `model/Evento.java` | Datos de evento que se devuelven en JSON y también reconstruye el REST Client a partir de la respuesta del servidor. |
| `model/EventoInput.java` | Cuerpo esperado para crear o actualizar un evento; mantiene explícitos los campos de entrada de la API. |
| `model/EventoJson.java` | DTO Jackson independiente del modelo de dominio: renombra `deporte` a `disciplina`, fija el orden, omite nulos, ignora propiedades desconocidas y formatea `LocalDate`. |
| `model/EventoValidadoInput.java` | DTO de entrada con restricciones `@NotBlank`, `@Size`, `@NotNull`, `@Min` y `@Max`; no ejecuta por sí solo la validación, el recurso debe recibirlo con `@Valid`. |
| `model/Precio.java` | Objeto de valor con importe y moneda. Su serializer evita exponerlo como objeto interno y define una representación JSON textual estable. |

#### Lógica de aplicación y datos: `service/`

| Archivo | Qué contiene y qué hace |
|---|---|
| `service/ActividadService.java` | Mantiene actividades de ejemplo en un `ConcurrentHashMap`, asigna ids, filtra por deporte y ejecuta operaciones CRUD. Semilla tres registros al iniciar; no persiste al disco. |
| `service/EventoService.java` | Lógica CRUD de eventos, búsqueda por deporte/id y tres registros iniciales en memoria. `buscarAsync` demuestra una respuesta Mutiny `Uni` con demora; no representa una base de datos real. |

#### Cómo recorrer las capas con una petición

Al probar `POST /api/actividades`, Quarkus usa Jackson para convertir el cuerpo
JSON en `ActividadInput`; `ActividadResource` valida las reglas sencillas y
delega en `ActividadService`; el servicio crea una `Actividad`; al volver al
recurso, Jackson la convierte a JSON y JAX-RS envía HTTP 201 con `Location`.
`RequestIdFilter` y `LogFiltro` observan la petición a sus lados. Esta
separación permite cambiar el transporte o almacenamiento sin mezclar todas las
responsabilidades en una sola clase.

### Pruebas

| Archivo | Qué verifica |
|---|---|
| `src/test/java/com/inklusport/ConfigTest.java` | Inyección y perfiles de configuración. |
| `src/test/java/com/inklusport/GreetingResourceTest.java` | Endpoints de saludo. |
| `src/test/java/com/inklusport/api/ActividadResourceTest.java` | Filtro, ciclo CRUD, datos inválidos y 404. |
| `src/test/java/com/inklusport/api/ClienteEventosResourceTest.java` | Consumo REST síncrono y asíncrono. |
| `src/test/java/com/inklusport/api/EventoResourceTest.java` | Paginación, operaciones CRUD, estados y cabeceras. |
| `src/test/java/com/inklusport/api/EventoStreamingResourceTest.java` | Streaming, SSE y búsquedas reactivas. |
| `src/test/java/com/inklusport/api/OpenApiResourceTest.java` | Especificación OpenAPI y rutas acumuladas. |
| `src/test/java/com/inklusport/api/ValidacionResourceTest.java` | Entradas válidas y respuestas 400 para restricciones violadas. |
| `src/test/java/com/inklusport/config/ConfigReferenceTest.java` | Fuentes, precedencia y tipos de configuración. |
| `src/test/java/com/inklusport/json/JsonResourceTest.java` | Construcción, parseo, tipos y JSON Pointer. |
| `src/test/java/com/inklusport/json/JsonRestResourceTest.java` | Binding Jackson, fechas, nulos y serialización/deserialización de precio. |
| `src/test/java/com/inklusport/logging/LoggingResourceTest.java` | Niveles de log, MDC e identificador de petición. |

## SmallRye Fault Tolerance

La extensión `quarkus-smallrye-fault-tolerance` y los ejemplos de la guía 12 ya
están implementados. `ResilienciaService` muestra reintentos, fallback, timeout,
circuit breaker, bulkhead y rate limit; `EventosClient` combina tolerancia a
fallos con el REST Client. Las pruebas correspondientes están en
`src/test/java/com/inklusport/resilience/`.

## Guía 13: proyecto acumulativo y estructura de estudio

Guía oficial: [Configure data sources in Quarkus](https://quarkus.io/guides/datasource). Esta carpeta parte del proyecto integrador de la guía 12 y conserva sus aprendizajes: aplicación y CDI, configuración y perfiles, REST, logging, JSON, validación, OpenAPI, REST Client y tolerancia a fallos.

El objetivo de esta etapa es ofrecer la base completa desde la que estudiar la guía datasource. **El tema nuevo todavía no está implementado**: este proyecto mantiene los datos en memoria y no añade drivers JDBC/reactivos, credenciales, URLs, Dev Services, conexiones, SQL ni persistencia. Al estudiar la guía podrás incorporar esos elementos gradualmente sobre las capas ya conocidas.

### Estructura de continuidad

```text
src/main/java/com/inklusport/
├── api/          recursos REST existentes
├── client/       cliente REST y resiliencia
├── config/       fuentes de configuración y perfiles
├── json/         JSON-P y personalización Jackson
├── logging/      filtros, MDC y correlación
├── model/        modelos y DTOs
├── persistence/  espacio reservado para el estudio posterior
├── resilience/  políticas SmallRye Fault Tolerance
└── service/      lógica actual con almacenamiento en memoria

src/test/java/com/inklusport/
├── api/          pruebas HTTP
├── config/       pruebas de configuración
├── json/         pruebas JSON
└── resilience/   pruebas de resiliencia
```

Las rutas HTTP y el comportamiento de las guías anteriores se conservan en esta aplicación independiente. `persistence/README.md` deja documentado el punto de extensión; no contiene código de acceso a base de datos. El pom y la configuración identifican esta etapa como `13-datasource`, sin dependencias específicas del tema nuevo.

Para comenzar, ejecuta `mvn quarkus:dev` y recorre los ejemplos documentados arriba. Cuando estudies el datasource, el flujo existente `api → service → model` y las pruebas te servirán de contexto para conectar el tema nuevo en una etapa posterior.

# Guía 13 — Datasource en Quarkus

## Descripción

Proyecto de entrenamiento para aprender a configurar e integrar un datasource JDBC con MySQL en una aplicación desarrollada con Quarkus.

La práctica utiliza la base de datos `inklusport` y expone un endpoint REST para verificar la conexión.

## Tecnologías

- Java
- Quarkus 3.40.1
- Maven
- MySQL
- JDBC
- JUnit 5
- RestAssured

## Funcionalidades implementadas

- Configuración del datasource MySQL.
- Administración de conexiones mediante el pool JDBC.
- Configuración separada para desarrollo y producción.
- Acceso a la base de datos mediante `DataSource`.
- Endpoint REST para comprobar la conexión.
- Prueba de integración del endpoint.

## Configuración

La aplicación utiliza las siguientes variables de entorno:

| Variable | Descripción |
|---|---|
| `DB_USERNAME` | Usuario de MySQL |
| `DB_PASSWORD` | Contraseña de MySQL |
| `DB_JDBC_URL` | URL JDBC para producción |

En desarrollo local se utiliza la configuración definida en `application-dev.properties`.

Asegúrate de tener MySQL iniciado y de crear la base de datos `inklusport` antes de ejecutar la aplicación.

## Endpoint de verificación

**Método:** `GET`

**Ruta:** `/api/persistencia/conexion`

Respuesta esperada:

```json
{
  "estado": "conectado",
  "baseDatos": "inklusport",
  "resultado": 1
}
```

Si la verificación de la conexión falla, el recurso está preparado para responder con HTTP 503 Service Unavailable.

## Ejecutar las pruebas

En Windows PowerShell:

```powershell
.\mvnw.cmd test
```

Resultado de la última ejecución registrada:

- 55 pruebas ejecutadas.
- 0 fallos.
- 0 errores.
- Compilación y pruebas exitosas.

## Objetivo de aprendizaje

Comprender la configuración del datasource en Quarkus, el uso de JDBC, la gestión del pool de conexiones, la separación de entornos y las pruebas de integración con MySQL.

## Archivos trabajados en esta guia
### Código principal
- src/main/java/com/inklusport/persistence/ConexionService.java
- src/main/java/com/inklusport/api/PersistenciaResource.java

### Pruebas 
- src/test/java/com/inklusport/api/PersistenciaResourceTest.java

### Configuración
- src/main/resources/application.properties
- src/main/resources/application-dev.properties
- src/main/resources/application-prod.properties

## Advertencias
1. `Advertencia de rotación de logs:` Maven informó que Windows no pudo mover logs\inklusport.log a logs\inklusport.log.1 porque el archivo estaba siendo utilizado por otro proceso. Las pruebas pasaron, pero conviene revisar después qué proceso mantiene el archivo abierto.
2. `Advertencias de Maven y Java:` aparecieron advertencias relacionadas con parámetros de la JVM y la carga dinámica de agentes. No provocaron el fallo de las pruebas; puedes investigarlas en una revisión de configuración posterior.