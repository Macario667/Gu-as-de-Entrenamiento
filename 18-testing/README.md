# Guía 18 — Testing con Quarkus

## Información del proyecto

- **Proyecto:** Inklusport
- **Módulo:** `18-testing`
- **Group ID:** `com.inklusport`
- **Versión:** `1.0.0-SNAPSHOT`
- **Framework:** Quarkus `3.40.1`
- **Lenguaje:** Java
- **Versión de Java configurada en el proyecto:** consultar `pom.xml`
- **Herramientas:** Maven, JUnit 5, REST Assured y Mockito

## Objetivo

Aprender a probar una aplicación Quarkus mediante pruebas automatizadas, comprobando tanto los componentes individuales como los recursos HTTP.

En esta guía se trabajan las pruebas de servicios, recursos REST, recursos estáticos, perfiles de prueba, configuración específica para tests, dobles de prueba y conceptos de pruebas de integración.

## 1. Tecnologías y conceptos

### JUnit 5

JUnit permite definir y ejecutar pruebas automatizadas en Java.

Anotaciones utilizadas:

- `@Test`: identifica un método de prueba.
- `@BeforeEach`: permite preparar el estado necesario antes de cada prueba.
- `@AfterEach`: permite realizar tareas posteriores a cada prueba.
- `@Assertions`: las aserciones permiten verificar los resultados esperados mediante métodos como `assertEquals`, `assertNotNull` y `assertThrows`.

### Quarkus Test

La anotación `@QuarkusTest` inicia el entorno de prueba de Quarkus para ejecutar los tests con el contexto de la aplicación.

Esto permite probar componentes que utilizan inyección de dependencias y realizar peticiones HTTP contra la aplicación iniciada para las pruebas.

### REST Assured

REST Assured permite enviar solicitudes HTTP y comprobar sus respuestas.

Entre las verificaciones habituales están:

- El código de estado HTTP.
- El contenido de la respuesta.
- Las cabeceras.
- El formato de los datos devueltos.

### Inyección de dependencias

La inyección de dependencias permite que Quarkus proporcione los componentes necesarios para ejecutar una prueba.

En este proyecto, `SaludoResource` utiliza `SaludoService` para generar los saludos.

## 2. Estructura del proyecto

La estructura principal desarrollada es:

```text
18-testing/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/inklusport/
│   │   │       ├── api/
│   │   │       │   └── SaludoResource.java
│   │   │       └── service/
│   │   │           └── SaludoService.java
│   │   └── resources/
│   │       └── META-INF/
│   │           └── resources/
│   │               └── prueba.html
│   └── test/
│       ├── java/
│       │   └── com/inklusport/
│       │       ├── api/
│       │       │   ├── PerfilPrueba.java
│       │       │   ├── RecursoEstaticoTest.java
│       │       │   ├── SaludoPerfilTest.java
│       │       │   ├── SaludoResourceIT.java
│       │       │   ├── SaludoResourceMockTest.java
│       │       │   ├── SaludoResourceSpyTest.java
│       │       │   └── SaludoResourceTest.java
│       │       └── service/
│       │           └── SaludoServiceTest.java
│       └── resources/
│           └── application.properties
└── pom.xml
```

La presencia exacta de cada archivo debe coincidir con la versión final del proyecto. Los archivos de prueba de integración solo se ejecutarán como tales si la configuración de Maven correspondiente está preparada.

## 3. Servicio de saludos

`SaludoService` contiene la lógica para generar un saludo.

Su responsabilidad es recibir un nombre y devolver un mensaje con el formato:

```text
Hola, Ana
```

También valida que el nombre no sea nulo ni esté vacío. Si no cumple esta condición, lanza una `IllegalArgumentException` con el mensaje:

```text
El nombre es obligatorio
```

Esta lógica permite practicar pruebas unitarias del comportamiento esperado y de los casos inválidos.

## 4. Recurso REST

`SaludoResource` expone endpoints HTTP bajo la ruta `/saludos`.

| Endpoint | Resultado esperado |
|---|---|
| `GET /saludos` | `Hola, Inklusport` |
| `GET /saludos/Ana` | `Hola, Ana` |
| `GET /saludos/estado` | `Servicio disponible` |

El recurso delega la generación de saludos en `SaludoService`.

Separar el recurso REST de la lógica de negocio facilita el mantenimiento y permite probar cada componente de forma independiente.

## 5. Pruebas del recurso REST

La clase `SaludoResourceTest` utiliza `@QuarkusTest` y REST Assured para comprobar las respuestas HTTP.

Las pruebas verifican que:

- El endpoint principal responde correctamente.
- El endpoint que recibe un nombre devuelve el saludo esperado.
- El endpoint de estado devuelve el mensaje correspondiente.
- Se puede obtener la URL de prueba mediante `@TestHTTPResource`.

El objetivo no es comprobar únicamente que la aplicación arranque, sino verificar el comportamiento observable de sus endpoints.

## 6. Pruebas del servicio

`SaludoServiceTest` verifica la lógica de negocio sin necesidad de enviar solicitudes HTTP.

Los casos incluyen:

- Generar un saludo con un nombre válido.
- Rechazar un nombre vacío o compuesto por espacios.
- Comprobar el tipo de excepción y el mensaje esperado cuando la entrada no es válida.

`assertEquals` comprueba los resultados y `assertThrows` permite verificar que se lanza la excepción prevista.

## 7. Uso de mocks

Un **mock** sustituye una dependencia real por un objeto controlado por la prueba.

`SaludoResourceMockTest` utiliza `@InjectMock` y Mockito para simular el comportamiento de `SaludoService`.

Con esta técnica se pueden definir respuestas específicas para las llamadas al servicio y comprobar cómo responde el recurso REST sin depender de la implementación real de la lógica de negocio.

Esto resulta especialmente útil para aislar componentes y probar distintos escenarios.

## 8. Uso de spies

Un **spy** permite observar o modificar determinadas llamadas de un objeto manteniendo, por defecto, el comportamiento real de sus métodos.

`SaludoResourceSpyTest` practica esta técnica sobre el servicio inyectado.

A diferencia de un mock, que normalmente simula el comportamiento de la dependencia, un spy permite conservar el comportamiento original de los métodos que no se han sustituido.

## 9. Perfiles de prueba

Quarkus permite definir perfiles de prueba con `QuarkusTestProfile`.

En este proyecto, `PerfilPrueba` sobrescribe la propiedad:

```properties
quarkus.http.test-port=8181
```

La clase `SaludoPerfilTest` utiliza `@TestProfile(PerfilPrueba.class)` para ejecutar una prueba con ese perfil.

Los perfiles son útiles cuando una prueba requiere una configuración diferente de la habitual, por ejemplo, cambiar propiedades o activar opciones específicas.

## 10. Configuración específica para tests

Las propiedades de prueba pueden colocarse en:

```text
src/test/resources/application.properties
```

El proyecto utiliza una configuración de nivel de registro para el paquete `com.inklusport`:

```properties
%test.quarkus.log.category."com.inklusport".level=DEBUG
```

El prefijo `%test` indica que la propiedad corresponde al perfil `test`.

La configuración de pruebas permite adaptar el comportamiento del entorno sin tener que modificar necesariamente la configuración normal de la aplicación.

## 11. Prueba de recursos estáticos

El archivo HTML se encuentra en:

```text
src/main/resources/META-INF/resources/prueba.html
```

Quarkus puede servir archivos colocados en esa ubicación como recursos estáticos.

`RecursoEstaticoTest` utiliza `@TestHTTPResource` para obtener la URL del recurso y verificar que se puede acceder a él.

La prueba comprueba el contenido de la página, incluido el título HTML.

Este caso demuestra que las pruebas automatizadas también pueden comprobar recursos web estáticos, no solamente endpoints REST.

## 12. Obtención de recursos para las pruebas

`@TestHTTPResource` permite obtener una URL de un recurso servido por la aplicación de prueba.

Es útil cuando una prueba necesita acceder directamente a un recurso HTTP, como una página HTML, sin construir manualmente la dirección del servidor.

`@TestHTTPEndpoint`, cuando se utiliza, permite asociar una clase de prueba con el endpoint de un recurso REST, reduciendo la necesidad de repetir la ruta base en cada prueba.

## 13. Recursos externos de prueba

Quarkus dispone de `@QuarkusTestResource` y de la interfaz `QuarkusTestResourceLifecycleManager` para administrar recursos que deben estar disponibles durante las pruebas.

Estos mecanismos permiten iniciar y detener servicios auxiliares, preparar configuraciones y proporcionar propiedades a la aplicación de prueba.

Son útiles cuando una aplicación depende de servicios externos, como bases de datos, servidores de mensajería o APIs auxiliares.

Este concepto debe distinguirse de los perfiles de prueba: un perfil cambia la configuración de Quarkus, mientras que un recurso de prueba puede administrar el ciclo de vida de un servicio externo.

## 14. Pruebas de integración

`@QuarkusTest` ejecuta las pruebas en el entorno de pruebas de Quarkus.

`@QuarkusIntegrationTest` está diseñado para probar la aplicación ya empaquetada o ejecutada como artefacto de integración, según la configuración de construcción utilizada.

El proyecto incluye el concepto de una clase `SaludoResourceIT` que extiende las pruebas del recurso para reutilizar los casos existentes.

Para considerar verificadas estas pruebas, es necesario confirmar que el plugin de integración de Maven esté configurado y que el comando de integración se ejecute correctamente. La ejecución satisfactoria de `mvn test` no demuestra por sí sola que las pruebas de integración se hayan ejecutado.

## 15. Surefire y Failsafe

Maven utiliza plugins distintos para las fases de prueba habituales y las pruebas de integración:

- **Surefire:** ejecuta las pruebas de la fase `test`, normalmente con `mvn test`.
- **Failsafe:** puede ejecutar pruebas de integración en las fases `integration-test` y `verify`, si está configurado.

Los nombres de las clases de prueba y la configuración del plugin determinan qué pruebas se seleccionan.

Comandos principales:

```powershell
mvn test
```

Ejecuta las pruebas configuradas para la fase `test`.

```powershell
mvn clean test
```

Limpia los archivos generados y vuelve a compilar y ejecutar las pruebas habituales.

```powershell
mvn verify
```

Ejecuta las fases anteriores del ciclo de construcción y las verificaciones que estén configuradas. Para confirmar que incluye pruebas de integración, hay que revisar la configuración de Maven y el resultado de la ejecución.

## 16. Resultado de las pruebas

En la última ejecución compartida de `mvn test`, el resultado fue:

```text
Tests run: 11, Failures: 0, Errors: 0, Skipped: 0

BUILD SUCCESS
```

Esto confirma que las 11 pruebas seleccionadas por Surefire pasaron en esa ejecución.

No implica automáticamente que las pruebas de integración con `@QuarkusIntegrationTest` se hayan ejecutado; para ello debe comprobarse la configuración y el resultado de `mvn verify`.

## 17. Buenas prácticas

- Mantener separada la lógica de negocio de los recursos REST.
- Probar tanto los casos válidos como las entradas inválidas.
- Utilizar aserciones que comprueben resultados concretos.
- Emplear mocks cuando se necesita aislar una dependencia.
- Utilizar spies cuando se necesita observar o sustituir parte del comportamiento real.
- Mantener la configuración de pruebas separada de la configuración normal.
- No ocultar errores eliminando pruebas que fallan; investigar y corregir su causa.
- Ejecutar las pruebas después de cambiar dependencias o configuración.
- Documentar qué comandos se han ejecutado y qué resultados se han confirmado.

## 18. Lista de comprobación

- [x] Crear el proyecto de pruebas con Quarkus.
- [x] Probar endpoints REST.
- [x] Probar la lógica del servicio.
- [x] Comprobar el manejo de entradas inválidas.
- [x] Utilizar `@QuarkusTest`.
- [x] Utilizar `@TestHTTPResource`.
- [x] Probar un recurso estático HTML.
- [x] Configurar propiedades específicas para el perfil `test`.
- [x] Crear y utilizar un perfil de prueba.
- [x] Practicar mocks con Mockito.
- [x] Practicar spies.
- [x] Revisar el uso de `@QuarkusTestResource` y su administrador del ciclo de vida.
- [x] Revisar `@QuarkusIntegrationTest`.
- [x] Revisar el propósito de Surefire y Failsafe.
- [x] Ejecutar `mvn test` y obtener 11 pruebas satisfactorias.

La comprobación de las pruebas de integración mediante `mvn verify` queda pendiente de confirmación independiente si todavía no se ha ejecutado.

## Referencia oficial

Guía de Quarkus: Getting Started Testing

https://quarkus.io/guides/getting-started-testing
