# 04 · Configuration Reference Guide

Guía oficial: [Configuration Reference Guide](https://quarkus.io/guides/config-reference) — consultar fuentes, prioridades, expresiones y conversión de propiedades.

## Qué aporta esta guía

La **referencia completa** de configuración: de dónde sale cada valor, cómo se
priorizan las fuentes y cómo se convierten los tipos.

| Concepto | Dónde se ve |
|---|---|
| Fuente de configuración propia (`ConfigSource` + ordinal) | `src/main/java/com/inklusport/config/EntornoConfigSource.java` |
| Registro de la fuente (`ConfigSourceProvider`) | `src/main/java/com/inklusport/config/EntornoConfigSourceProvider.java`, `src/main/resources/META-INF/services/` |
| Lectura en caliente: valor, fuente y ordinal | `src/main/java/com/inklusport/config/ConfigReferenceResource.java` |
| Tipos compuestos: `Duration`, `List<String>`, `enum` | `src/main/java/com/inklusport/AppConfig.java` |
| Expresión con variable de entorno y valor por defecto | `src/main/resources/application.properties` (`app.otel-colector-url`) |

### Ordinales de prioridad

| Ordinal | Fuente |
|---|---|
| 100 | `EntornoConfigSource` (la de este proyecto) |
| 250 | `application.properties` |
| 300 | Variables de entorno (`APP_NOMBRE`, `QUARKUS_HTTP_PORT`...) |
| 400 | Propiedades del sistema (`-Dapp.nombre=...`) |

> Ojo: el mapeo tipado (`@ConfigMapping`) es **estricto**. Si existe una
> propiedad `app.*` que no está en la interfaz, Quarkus falla al arrancar con
> `SRCFG00050: ... does not map to any root`. Por eso `AppConfig` declara
> `entorno`, `despliegue` y `otelColectorUrl`.

## Estado acumulado

Hereda de [03 · RESTEasy Classic](./../03-resteasy-classic): todo el CRUD de
eventos, los parámetros, el `ExceptionMapper`, el streaming, `GreetingResource`
y `AppConfig`, además del CRUD de actividades de la guía 01. En esta carpeta el
motor sigue siendo `quarkus-resteasy`.

## Endpoints nuevos

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/config` | Valor de `app.nombre` + fuente + ordinal |
| `GET` | `/config/fuentes` | Todas las fuentes ordenadas por ordinal |
| `GET` | `/config/tipos` | Conversión de tipos (`Duration`, `List`, `enum`) |

## Ejecutar

```powershell
mvn quarkus:dev
```

Probar las diferentes prioridades:

```powershell
# valor por defecto (application.properties)
curl.exe http://localhost:8080/config

# con variable de entorno (ordinal 300, gana a application.properties)
$env:APP_NOMBRE="InkluSport API (ENV)"
curl.exe http://localhost:8080/config

# con propiedad del sistema (ordinal 400, gana a todo lo demás)
mvn quarkus:dev "-Dapp.nombre=InkluSport API (SYS)"
```

## Pruebas

```powershell
mvn test
```

## Siguiente paso

[05 · Writing REST Services with RESTEasy Reactive](./../05-rest-resteasy-reactive) →
vuelta al motor `quarkus-rest` (antes RESTEasy Reactive) y a la API reactiva.
