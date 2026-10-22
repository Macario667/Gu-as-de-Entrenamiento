# 01 · Creating Your First Application

Guía oficial: [Creating Your First Application](https://quarkus.io/guides/getting-started) — crear, ejecutar y probar una aplicación Java con Quarkus.

## Qué aporta esta guía

El punto de partida de cualquier aplicación Quarkus: crear el proyecto, exponer un
endpoint HTTP, entender CDI y practicar un CRUD de actividades deportivas en
memoria con búsqueda, validación básica, códigos HTTP y pruebas. El CRUD amplía
el saludo inicial para que el ejemplo sea útil sin saltar a persistencia avanzada.

| Concepto | Dónde se ve |
|---|---|
| Recurso REST (`@Path`, `@GET`, `@Produces`, `@PathParam`) | `src/main/java/com/inklusport/GreetingResource.java` |
| Bean CDI (`@ApplicationScoped`) e inyección (`@Inject`) | `src/main/java/com/inklusport/GreetingService.java` |
| Configuración básica por propiedades | `src/main/resources/application.properties` |
| Test de integración con `@QuarkusTest` + RestAssured | `src/test/java/com/inklusport/GreetingResourceTest.java` |
| CRUD de actividades con servicio CDI y filtro por deporte | `src/main/java/com/inklusport/api/ActividadResource.java`, `service/ActividadService.java` |
| Pruebas de ciclo CRUD, filtro, errores y validación | `src/test/java/com/inklusport/api/ActividadResourceTest.java` |

## Estado acumulado

Esta es la **guía 1 de 11**. No hereda nada: es la base sobre la que se
construyen todas las demás carpetas.

## Endpoints

| Método | Ruta | Respuesta |
|---|---|---|
| `GET` | `/hello` | `Hello from Quarkus REST` (text/plain) |
| `GET` | `/hello/greeting/{name}` | `hello {name}` (text/plain) |
| `GET` | `/api/actividades?deporte=balon` | Lista o filtra actividades |
| `GET` | `/api/actividades/{id}` | Consulta una actividad; 404 si no existe |
| `POST` | `/api/actividades` | Crea una actividad; 201 y cabecera `Location` |
| `PUT` | `/api/actividades/{id}` | Actualiza una actividad |
| `DELETE` | `/api/actividades/{id}` | Elimina una actividad; 204 |

## Ejecutar

```powershell
mvn quarkus:dev
```

Con `mvn quarkus:dev` los cambios en el código se aplican al instante
(*live reload*): no hace falta reiniciar.

```powershell
curl.exe http://localhost:8080/hello
curl.exe http://localhost:8080/hello/greeting/Samu
curl.exe "http://localhost:8080/api/actividades?deporte=balon"
curl.exe -X POST http://localhost:8080/api/actividades -H "Content-Type: application/json" -d '{"nombre":"Torneo inclusivo","deporte":"baloncesto","capacidad":32}'
```

Para practicar el ciclo completo, copia el `id` recibido al crear y úsalo en
`GET`, `PUT` y `DELETE` en `/api/actividades/{id}`. Una capacidad fuera de
`1..500` o un nombre vacío devuelve `400`.

## Pruebas

```powershell
mvn test
```

## Siguiente paso

[02 · Configuring Your Application](./../02-configuring-your-application) →
sustituye las constantes por **configuración** con perfiles y tipado.
