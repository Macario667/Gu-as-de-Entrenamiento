# Ruta progresiva de guías Quarkus

Este directorio contiene **14 proyectos Maven independientes y numerados**.
Cada carpeta es una etapa completa: parte del código de la carpeta anterior y
añade únicamente el tema nuevo, de modo que la aplicación crece guía por guía.
La carpeta 01 es la base; la 13 contiene los aprendizajes acumulados de las guías 01–12 y la 14 añade su estructura para estudiar Hibernate ORM with Panache.

## Recorrido y utilidad de cada guía

| N.º | Proyecto | Guía | ¿Para qué sirve? |
|---:|---|---|---|
| 01 | [01-getting-started](./01-getting-started/README.md) | [Creating Your First Application](https://quarkus.io/guides/getting-started) | Crear una aplicación Quarkus con CDI y un CRUD de actividades probado. |
| 02 | [02-configuring-your-application](./02-configuring-your-application/README.md) | [Configuring Your Application](https://quarkus.io/guides/config) | Leer propiedades tipadas y utilizar perfiles de configuración. |
| 03 | [03-resteasy-classic](./03-resteasy-classic/README.md) | [RESTEasy Classic](https://es.quarkus.io/guides/resteasy) | Conocer la implementación REST clásica de Quarkus. |
| 04 | [04-configuration-reference](./04-configuration-reference/README.md) | [Configuration Reference Guide](https://quarkus.io/guides/config-reference) | Entender las fuentes de configuración, sus prioridades y cómo se resuelven los valores. |
| 05 | [05-rest-resteasy-reactive](./05-rest-resteasy-reactive/README.md) | [Writing REST Services with RESTEasy Reactive](https://quarkus.io/guides/rest) | Usar Quarkus REST (antes RESTEasy Reactive) para crear servicios HTTP. |
| 06 | [06-logging](./06-logging/README.md) | [Configuring Logging](https://quarkus.io/guides/logging) | Configurar niveles, categorías, formato y correlación de logs. |
| 07 | [07-json](./07-json/README.md) | [Conocimientos JSON](https://www.nylas.com/blog/the-complete-guide-to-working-with-json/) | Comprender tipos, estructuras, lectura y construcción de JSON. |
| 08 | [08-rest-json](./08-rest-json/README.md) | [Writing JSON REST Services](https://quarkus.io/guides/rest-json) | Controlar la serialización JSON en endpoints REST con Jackson. |
| 09 | [09-validation](./09-validation/README.md) | [Validation with Hibernate Validator](https://quarkus.io/guides/validation) | Validar las entradas declarativamente y rechazar solicitudes inválidas. |
| 10 | [10-openapi-swaggerui](./10-openapi-swaggerui/README.md) | [Using OpenAPI and Swagger UI](https://quarkus.io/guides/openapi-swaggerui) | Generar la especificación de la API y explorarla desde Swagger UI. |
| 11 | [11-rest-client](./11-rest-client/README.md) | [Using the REST Client](https://quarkus.io/guides/rest-client) | Consumir servicios REST con métodos síncronos y asíncronos. |
| 12 | [12-smallrye-fault-tolerance](./12-smallrye-fault-tolerance/README.md) | [SmallRye Fault Tolerance](https://quarkus.io/guides/smallrye-fault-tolerance) | Añadir reintentos, timeouts, circuit breaker, bulkhead y fallback. |
| 13 | [13-datasource](./13-datasource/README.md) | [Datasource](https://quarkus.io/guides/datasource) | Proyecto acumulativo con los temas 01–12; estructura lista para estudiar datasource, sin implementar todavía el tema nuevo. |
| 14 | [14-hibernate-orm-panache](./14-hibernate-orm-panache/README.md) | [Hibernate ORM with Panache](https://quarkus.io/guides/hibernate-orm-panache) | Proyecto acumulativo con los temas 01–13; dependencia y paquetes preparados, sin implementar todavía Hibernate ORM with Panache. |

## Cómo avanzar

Entra a una carpeta y ejecuta sus pruebas desde allí:

```powershell
Set-Location .\01-getting-started
mvn test
```

Para probar una etapa posterior, cambia `01-getting-started` por la carpeta
correspondiente. Cada etapa se compila y ejecuta por separado; no requiere
ejecutar las anteriores.

## Notas sobre los proyectos anteriores

- Las carpetas sin número son ejemplos auxiliares existentes, no etapas
  adicionales ni la ruta principal. La progresión oficial de este espacio es
  exclusivamente `01` a `14`.
- La guía 03 conserva deliberadamente la implementación RESTEasy Classic. La
  guía 05 cambia a Quarkus REST (antes RESTEasy Reactive), que se mantiene en
  las etapas posteriores.
- `quarkus-quickstarts-temp` contiene ejemplos pendientes de importar.
- `.idea` contiene metadatos del IDE, no un proyecto Quarkus.
