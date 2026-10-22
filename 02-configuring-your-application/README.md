# 02 · Configuring Your Application

Guía oficial: [Configuring Your Application](https://quarkus.io/guides/config) — inyectar propiedades tipadas y organizar valores por perfil.

## Qué aporta esta guía

Sustituye las constantes por **configuración externa**: tipada, con perfiles de
entorno y con expresiones entre propiedades.

| Concepto | Dónde se ve |
|---|---|
| Configuración tipada con `@ConfigMapping` | `src/main/java/com/inklusport/AppConfig.java` |
| Lectura puntual con `@ConfigProperty` | `src/main/java/com/inklusport/SaludoResource.java` |
| Perfiles `dev` / `prod` | `src/main/resources/application-dev.properties`, `application-prod.properties` |
| Expresión `${app.nombre}` entre propiedades | `src/main/resources/application.properties` |
| Perfil de test con `@TestProfile` | `src/test/java/com/inklusport/ConfigTest.java` |

## Estado acumulado

Hereda de [01 · Creating Your First Application](./../01-getting-started):
el recurso `GreetingResource`, el bean `GreetingService`, el CRUD de
actividades (`/api/actividades`) y sus pruebas. Esta etapa agrega perfiles y
configuración tipada a la misma aplicación práctica.

## Endpoints

| Método | Ruta | Respuesta |
|---|---|---|
| `GET` | `/hello` | `Hello from Quarkus REST` |
| `GET` | `/hello/greeting/{name}` | `hello {name}` |
| `GET` | `/info` | JSON con la configuración tipada |
| `GET` | `/saludo` | `Hola desde InkluSport API v1.0.0 — DESARROLLO` |

## Ejecutar

```powershell
mvn quarkus:dev
mvn quarkus:dev -Dquarkus.profile=dev
```

## Pruebas

```powershell
mvn test
```

## Siguiente paso

[03 · RESTEasy Classic](./../03-resteasy-classic) → la misma API escrita con
el motor clásico (`quarkus-resteasy`) para ver las diferencias con el actual.
