# Inklusport — Guía de MongoDB con Quarkus

## 1. Descripción del proyecto

Este módulo forma parte del proyecto acumulativo **Inklusport**, una aplicación desarrollada con Java, Quarkus y Maven para gestionar actividades deportivas inclusivas.

El objetivo de esta guía es aprender a integrar MongoDB en una aplicación Quarkus y comprender las principales herramientas disponibles para trabajar con bases de datos NoSQL.

Durante el desarrollo se estudian operaciones CRUD, consultas con filtros, servicios REST, integración con una interfaz web, programación reactiva, codecs BSON, MongoDB with Panache, pruebas automatizadas, health checks, métricas, trazabilidad, seguridad y configuración avanzada.

El módulo MongoDB se incorpora al proyecto existente sin sustituir las funcionalidades desarrolladas anteriormente con MySQL, H2, Hibernate ORM y Hibernate ORM with Panache.

---

## 2. Objetivos de aprendizaje

Al finalizar esta guía, se busca comprender cómo:

- Configurar una conexión entre Quarkus y MongoDB.
- Comprender las diferencias entre bases de datos relacionales y NoSQL.
- Representar actividades mediante documentos MongoDB.
- Implementar operaciones CRUD mediante una API REST.
- Consultar documentos utilizando filtros combinados.
- Consumir los endpoints desde una interfaz web.
- Utilizar operaciones reactivas con Mutiny.
- Comprender la serialización y deserialización mediante codecs BSON.
- Utilizar MongoDB with Panache para reducir código repetitivo.
- Escribir pruebas automatizadas para los endpoints.
- Comprobar la disponibilidad de MongoDB mediante health checks.
- Comprender las métricas y la trazabilidad de las operaciones.
- Configurar credenciales, TLS y clientes nombrados.
- Conocer las migraciones de documentos y las opciones de despliegue.

## 3. Tecnologías utilizadas

| Tecnología | Propósito |
|---|---|
| Java 17 | Lenguaje de programación |
| Quarkus 3.x | Framework para desarrollar servicios Java |
| Maven | Gestión de dependencias y construcción |
| MongoDB | Base de datos documental NoSQL |
| MongoDB Java Driver | Comunicación entre Java y MongoDB |
| REST | Exposición de operaciones HTTP |
| Jakarta REST | Definición de endpoints REST |
| Mutiny | Programación reactiva mediante `Uni` y `Multi` |
| MongoDB with Panache | Abstracción para simplificar la persistencia |
| JUnit y REST Assured | Pruebas automatizadas |
| SmallRye Health | Comprobaciones de salud |
| Micrometer | Recopilación y exposición de métricas |
| OpenTelemetry | Instrumentación y trazabilidad |

Las extensiones concretas disponibles dependen de la versión instalada de Quarkus y de las dependencias declaradas en el proyecto.

---

## 4. MongoDB y las bases de datos NoSQL

### 4.1. ¿Qué es MongoDB?

MongoDB es una base de datos NoSQL orientada a documentos. En lugar de organizar toda la información en tablas y filas relacionales, almacena documentos BSON agrupados en colecciones.

BSON es un formato binario de serialización de documentos que admite tipos como cadenas de texto, números, fechas, arreglos y documentos anidados.

Por ejemplo, una actividad deportiva puede representarse así:

```json
{
  "_id": "identificador",
  "nombre": "Baloncesto adaptado",
  "deporte": "Baloncesto",
  "capacidad": 12
}
```

Este documento contiene los datos de una actividad en una única estructura.

### 4.2. Base de datos, colección y documento

Los tres conceptos principales son:

- **Base de datos:** agrupa colecciones relacionadas.
- **Colección:** agrupa documentos de una misma categoría.
- **Documento:** contiene los campos y valores de un registro.

En Inklusport utilizamos la base de datos `inklusport` y la colección `actividades` para los ejemplos del CRUD síncrono.

### 4.3. Diferencias con una base de datos relacional

En una base relacional se trabaja habitualmente con tablas, columnas, claves y relaciones. MongoDB permite documentos con estructuras flexibles.

Esto no significa que MongoDB sea siempre mejor que una base relacional. Cada tecnología responde a necesidades distintas.

En Inklusport ambas alternativas pueden coexistir: las entidades JPA continúan utilizando sus mecanismos relacionales, mientras que los documentos MongoDB utilizan el controlador correspondiente.

---

## 5. Configuración de MongoDB en Quarkus

### 5.1. Instalación de la extensión

La integración con MongoDB se incorpora mediante la extensión del controlador:

```powershell
.\mvnw quarkus:add-extension -Dextensions="mongodb-client"
```

Si la extensión ya está instalada, no es necesario volver a agregarla.

La extensión permite a Quarkus configurar y administrar el cliente MongoDB utilizado por los servicios.

### 5.2. Configuración local

En `src/main/resources/application.properties` se utiliza una configuración como esta:

```properties
quarkus.mongodb.connection-string=mongodb://localhost:27017
quarkus.mongodb.database=inklusport
```

La primera propiedad define la URI de conexión y la segunda establece la base de datos predeterminada.

Esta configuración presupone que MongoDB está disponible en el puerto `27017` del equipo local.

### 5.3. Ejecutar MongoDB mediante Docker

Si Docker está instalado, se puede iniciar un contenedor de desarrollo:

```powershell
docker run --name mongodb-inklusport -p 27017:27017 -d mongo:7.0
```

Para comprobar que el contenedor está activo:

```powershell
docker ps
```

Para detenerlo:

```powershell
docker stop mongodb-inklusport
```

Para iniciarlo nuevamente:

```powershell
docker start mongodb-inklusport
```

Este ejemplo es apropiado para desarrollo local. No debe interpretarse como una configuración de producción segura.

### 5.4. Variables de entorno

Para evitar almacenar las credenciales en el repositorio, se puede configurar la conexión mediante una variable de entorno.

En `application.properties`:

```properties
quarkus.mongodb.connection-string=${MONGODB_CONNECTION_STRING}
quarkus.mongodb.database=inklusport
```

En PowerShell:

```powershell
$env:MONGODB_CONNECTION_STRING="mongodb://localhost:27017"
```

En producción, la variable debe contener la URI real con los parámetros de autenticación, TLS y demás opciones que requiera el servidor.

La URI de conexión y las contraseñas no deben subirse al repositorio.

---

## 6. Modelos de datos

### 6.1. Modelo relacional existente

La clase `Actividad` representa el modelo relacional existente en el proyecto:

```java
public class Actividad {
    public Long id;
    public String nombre;
    public String deporte;
    public Integer capacidad;
}
```

Su identificador es de tipo `Long`, adecuado para el modelo que ya utiliza la aplicación.

Esta clase no debe modificarse para convertirla en una entidad MongoDB si ello afecta a las funcionalidades relacionales anteriores.

### 6.2. Modelo MongoDB

Para trabajar con documentos se creó `ActividadMongo`:

```java
package com.inklusport.model;

public class ActividadMongo {

    public String id;
    public String nombre;
    public String deporte;
    public Integer capacidad;

    public ActividadMongo() {
    }

    public ActividadMongo(
            String id,
            String nombre,
            String deporte,
            Integer capacidad) {
        this.id = id;
        this.nombre = nombre;
        this.deporte = deporte;
        this.capacidad = capacidad;
    }
}
```

El modelo contiene los campos principales de una actividad deportiva.

El identificador se representa como `String` para facilitar su transmisión mediante JSON y las rutas HTTP. En la colección síncrona, el servicio convierte ese identificador en un `ObjectId` de MongoDB cuando corresponde.

### 6.3. ¿Por qué separar los modelos?

Separar el modelo relacional y el modelo documental permite:

- Mantener los tipos de identificador apropiados para cada tecnología.
- Evitar que una modificación de MongoDB afecte a las entidades JPA.
- Evolucionar las estructuras de datos independientemente.
- Facilitar la integración progresiva de diferentes mecanismos de persistencia.

---

## 7. Operaciones CRUD con MongoDB

CRUD representa las cuatro operaciones fundamentales de persistencia:

| Operación | Propósito | HTTP habitual |
|---|---|---|
| Create | Crear un documento | POST |
| Read | Consultar documentos | GET |
| Update | Actualizar un documento | PUT |
| Delete | Eliminar un documento | DELETE |

### 7.1. Servicio MongoDB

`ActividadMongoService` concentra la lógica de acceso a la colección.

Entre sus responsabilidades están:

- Obtener la colección `actividades`.
- Consultar todos los documentos.
- Buscar una actividad por identificador.
- Insertar documentos.
- Actualizar documentos existentes.
- Eliminar documentos.
- Convertir documentos BSON a objetos Java.

Centralizar estas operaciones evita repetir la lógica de persistencia en cada endpoint.

### 7.2. Recurso REST

`ActividadMongoResource` expone las operaciones mediante la ruta:

`/api/mongodb/actividades`

Los endpoints implementados son:

| Método | Ruta | Propósito |
|---|---|---|
| GET | `/api/mongodb/actividades` | Listar actividades |
| GET | `/api/mongodb/actividades/{id}` | Buscar por identificador |
| POST | `/api/mongodb/actividades` | Crear actividad |
| PUT | `/api/mongodb/actividades/{id}` | Actualizar actividad |
| DELETE | `/api/mongodb/actividades/{id}` | Eliminar actividad |

### 7.3. Ejemplo de creación

Petición:

```http
POST /api/mongodb/actividades
Content-Type: application/json
```

Cuerpo:

```json
{
  "nombre": "Baloncesto adaptado",
  "deporte": "Baloncesto",
  "capacidad": 12
}
```

Una respuesta satisfactoria puede incluir el identificador generado por MongoDB.

### 7.4. Validación de datos

Antes de crear una actividad, el recurso comprueba que los datos obligatorios estén presentes y que la capacidad no sea negativa.

La validación evita guardar documentos con información incompleta o inválida.

En una aplicación más amplia también pueden utilizarse validaciones declarativas mediante Jakarta Bean Validation.

### 7.5. Códigos HTTP

Los códigos más habituales son:

- `200 OK`: operación satisfactoria con respuesta.
- `201 Created`: documento creado.
- `204 No Content`: operación satisfactoria sin contenido de respuesta.
- `400 Bad Request`: datos incorrectos.
- `404 Not Found`: documento inexistente.
- `500 Internal Server Error`: error no controlado en el servidor.

Los códigos concretos dependen de cómo se hayan implementado los recursos.

---

## 8. Consultas y filtros combinados

Los filtros permiten recuperar únicamente los documentos que cumplen determinados criterios.

En Inklusport se contempla un método `buscarConFiltros` con tres parámetros:

- `nombre`: buscar por nombre, ignorando mayúsculas y minúsculas.
- `deporte`: filtrar por deporte.
- `capacidadMinima`: obtener actividades con capacidad igual o superior a un valor.

### 8.1. Ejemplo de consulta

```http
GET /api/mongodb/actividades/buscar?deporte=Baloncesto&capacidadMinima=10
```

Esta petición busca actividades de baloncesto con capacidad mínima de diez personas.

### 8.2. Filtros MongoDB

El controlador permite construir condiciones mediante `Filters`.

Ejemplos:

```java
Filters.eq("deporte", "Baloncesto");
Filters.gte("capacidad", 10);
Filters.regex("nombre", "adaptado", "i");
```

- `eq` comprueba igualdad.
- `gte` significa greater than or equal, es decir, mayor o igual.
- `regex` busca coincidencias mediante expresiones regulares.
- `Filters.and` combina condiciones que deben cumplirse simultáneamente.

### 8.3. Ventajas

Las consultas combinadas evitan tener que recuperar toda la colección para filtrar los resultados en Java.

Cuando una colección crece, los índices adecuados pueden mejorar el rendimiento de las búsquedas.

---

## 9. Interfaz web para MongoDB

Se propuso una página estática en:

`src/main/resources/META-INF/resources/mongodb.html`

La página se sirve desde Quarkus y puede abrirse en:

`http://localhost:8080/mongodb.html`

La interfaz permite interactuar con la API mediante JavaScript y `fetch`.

### 9.1. Funcionalidades

La interfaz contempla:

- Listar actividades.
- Crear nuevas actividades.
- Filtrar por criterios.
- Actualizar una actividad.
- Eliminar una actividad.
- Mostrar resultados y errores de las peticiones.

### 9.2. ¿Cómo funciona `fetch`?

`fetch` permite enviar solicitudes HTTP desde el navegador.

Por ejemplo:

```javascript
fetch("/api/mongodb/actividades")
    .then(response => {
        if (!response.ok) {
            throw new Error("Error al consultar actividades");
        }
        return response.json();
    })
    .then(actividades => {
        console.log(actividades);
    })
    .catch(error => {
        console.error(error);
    });
```

El navegador consulta el endpoint, convierte la respuesta JSON y permite mostrar los resultados.

La interfaz web consume la API existente; no accede directamente a la base de datos.

---

## 10. Cliente reactivo con Mutiny

### 10.1. ¿Qué significa programación reactiva?

La programación reactiva permite componer operaciones asíncronas sin bloquear innecesariamente el hilo que las inicia.

Quarkus utiliza Mutiny para representar resultados asíncronos mediante dos tipos importantes:

- `Uni<T>`: representa un resultado asíncrono de tipo `T`, que puede completarse correctamente o con un error.
- `Multi<T>`: representa una secuencia asíncrona de cero o más elementos.

### 10.2. Cliente reactivo de MongoDB

El controlador reactivo permite ejecutar operaciones de MongoDB mediante publishers compatibles con el modelo reactivo.

La integración con Mutiny facilita transformar esos publishers en `Uni` y `Multi`.

El ejemplo del proyecto utiliza un servicio independiente, `ActividadMongoReactiveService`, y la colección `actividades_reactivas`.

Esta colección separada permite practicar sin mezclar los datos del CRUD síncrono.

### 10.3. Ejemplo conceptual

```java
public Multi<ActividadMongo> listar() {
    return Multi.createFrom()
            .publisher(coleccion().find())
            .map(this::convertir);
}
```

El método obtiene una secuencia de documentos y transforma cada documento en un objeto Java.

### 10.4. ¿Cuándo utilizarlo?

El enfoque reactivo resulta útil cuando se necesita atender muchas operaciones concurrentes y se desea evitar bloquear hilos durante la espera de resultados.

No todas las aplicaciones necesitan programación reactiva. Para una API sencilla, el cliente síncrono puede ser suficiente.

**Nota de implementación:** el cliente reactivo y sus dependencias deben comprobarse contra la versión de Quarkus utilizada. El ejemplo reactivo de esta guía cubre consultas, inserción y eliminación; no debe interpretarse como una implementación completa de todas las operaciones CRUD reactivas.

---

## 11. BSON y codecs personalizados

### 11.1. ¿Qué es un codec?

Un codec define cómo convertir un objeto Java en una representación BSON y cómo reconstruir el objeto Java a partir de BSON.

Sin un codec personalizado, el código puede convertir manualmente entre `Document` y el modelo Java.

Un codec permite centralizar esa transformación.

### 11.2. Métodos principales

Un codec personalizado implementa la interfaz `Codec<T>` y define:

- `encode`: escribe un objeto Java en formato BSON.
- `decode`: lee BSON y reconstruye el objeto.
- `getEncoderClass`: indica la clase Java que procesa.

### 11.3. ¿Cuándo es útil?

Los codecs son útiles cuando se necesita controlar el mapeo entre el modelo de Java y los documentos almacenados.

También permiten definir cómo se representan campos específicos, como identificadores o valores numéricos.

### 11.4. Consideraciones

El codec debe estar registrado en el `CodecRegistry` utilizado por el cliente MongoDB.

Además, la representación del identificador debe coincidir con los documentos almacenados. Un `_id` de tipo `ObjectId` no es equivalente a un `_id` de tipo `String`.

En el proyecto se utiliza un ejemplo de codec personalizado para estudiar este mecanismo sin reemplazar el servicio CRUD principal.

---

## 12. MongoDB with Panache

### 12.1. ¿Qué es Panache?

Panache simplifica el código de persistencia en Quarkus.

Para MongoDB, proporciona una abstracción orientada a documentos. Es distinta de Hibernate ORM with Panache, que trabaja con bases de datos relacionales.

### 12.2. Entidad MongoDB

El ejemplo utiliza una clase independiente:

```java
package com.inklusport.model;

import io.quarkus.mongodb.panache.PanacheMongoEntity;

public class ActividadPanache extends PanacheMongoEntity {

    public String nombre;
    public String deporte;
    public Integer capacidad;
}
```

`PanacheMongoEntity` proporciona un identificador MongoDB basado en `ObjectId`.

Esta clase no sustituye a `Actividad` ni a `ActividadMongo`.

### 12.3. Repositorio

El patrón repositorio permite separar el acceso a los datos de los recursos REST.

```java
@ApplicationScoped
public class ActividadPanacheRepository
        implements PanacheMongoRepository<ActividadPanache> {
}
```

A través del repositorio se pueden realizar operaciones como listar, buscar, persistir y eliminar documentos.

### 12.4. Ejemplos de consultas

```java
repository.listAll();
repository.list("deporte", "Baloncesto");
repository.list("capacidad >= ?1", 10);
```

El primer método devuelve todos los documentos de la entidad. Los otros dos filtran por deporte y capacidad mínima.

Estas consultas deben ejecutarse mediante el repositorio correspondiente y con la extensión MongoDB with Panache correctamente instalada.

---

## 13. Dev Services y clientes nombrados

### 13.1. Dev Services

Dev Services permite que Quarkus proporcione servicios de desarrollo automáticamente cuando se cumplen las condiciones necesarias y el servicio está disponible para la extensión correspondiente.

En el caso de MongoDB, puede utilizar un contenedor de desarrollo si Docker está disponible.

Si se configura explícitamente una cadena de conexión en `application.properties`, Quarkus normalmente utiliza esa conexión en lugar de iniciar una instancia automática.

Dev Services es útil para reducir la configuración manual en desarrollo y pruebas.

### 13.2. Clientes nombrados

Una aplicación puede necesitar conexiones diferentes para bases de datos o servidores distintos.

Por ejemplo:

```properties
quarkus.mongodb.analytics.connection-string=${MONGODB_ANALYTICS_CONNECTION_STRING}
quarkus.mongodb.analytics.database=inklusport_analytics
```

El cliente nombrado `analytics` puede inyectarse en un servicio mediante `@MongoClientName("analytics")`.

Esta técnica permite separar responsabilidades, como las actividades principales y la información de analítica.

No es necesario crear clientes adicionales si la aplicación solo necesita una conexión.

### 13.3. Personalización del cliente

La personalización permite establecer opciones del controlador, como el nombre de aplicación que identifica las conexiones.

Estas opciones deben configurarse mediante las interfaces compatibles con la versión instalada de Quarkus.

---

## 14. Migraciones de MongoDB

### 14.1. ¿Qué es una migración?

Una migración es un cambio controlado y versionado sobre los datos o su estructura lógica.

Aunque MongoDB no exige un esquema rígido, una aplicación puede necesitar modificar documentos existentes para mantener la coherencia del modelo.

Ejemplos:

- Añadir un campo nuevo.
- Normalizar valores.
- Crear índices.
- Transformar la estructura de documentos.
- Adaptar datos a una nueva versión de la aplicación.

### 14.2. Índices

Un índice permite que MongoDB encuentre documentos con mayor eficiencia para determinadas consultas.

Por ejemplo, un índice ascendente sobre el campo `deporte` puede crearse con:

```javascript
db.actividades.createIndex({ deporte: 1 })
```

El índice puede beneficiar las consultas por deporte, aunque su utilidad depende de las consultas reales y del tamaño de la colección.

Los índices consumen espacio y tienen un coste adicional durante las operaciones de escritura.

### 14.3. Liquibase

La extensión de Liquibase para MongoDB permite gestionar migraciones mediante changelogs compatibles con la herramienta.

Es importante utilizar la sintaxis admitida por la versión concreta instalada. Un comando de MongoDB no constituye por sí solo un changelog válido.

Las migraciones deben probarse primero en desarrollo antes de habilitar su ejecución automática en producción.

---

## 15. Pruebas automatizadas

### 15.1. ¿Por qué probar una API?

Las pruebas automatizadas comprueban que los endpoints se comportan como se espera y permiten detectar regresiones después de modificar el código.

En el proyecto se propuso una prueba de CRUD con Quarkus Test y REST Assured.

### 15.2. Qué comprueba la prueba

La prueba crea una actividad, consulta el registro mediante su identificador, lo elimina y comprueba que ya no existe.

Esto permite verificar tanto las respuestas HTTP como la persistencia real de los datos.

### 15.3. Ejecutar pruebas

Para ejecutar la suite de pruebas:

```powershell
.\mvnw test
```

Para ejecutar únicamente una clase de prueba:

```powershell
.\mvnw test -Dtest=ActividadMongoResourceTest
```

MongoDB debe estar disponible durante las pruebas, salvo que se configure otro entorno de prueba.

Las pruebas que modifican datos deben utilizar una base de desarrollo o de test, nunca una base de producción.

---

## 16. Health checks

### 16.1. ¿Qué es un health check?

Un health check permite comprobar el estado de una aplicación o de una dependencia importante.

En Quarkus, SmallRye Health proporciona endpoints para consultar el estado del servicio.

Los endpoints habituales son:

- `/q/health`
- `/q/health/live`
- `/q/health/ready`

### 16.2. Liveness y readiness

**Liveness** ayuda a determinar si la aplicación sigue viva.

**Readiness** indica si la aplicación está preparada para recibir solicitudes.

Un check personalizado puede realizar un comando `ping` contra MongoDB para comprobar si la base de datos responde.

Esto permite detectar problemas de conexión y facilita el diagnóstico del entorno.

---

## 17. Métricas con Micrometer

### 17.1. ¿Qué son las métricas?

Las métricas son valores numéricos que permiten observar el funcionamiento de la aplicación a lo largo del tiempo.

Según las extensiones y la instrumentación habilitadas, pueden incluir información sobre conexiones, solicitudes y otros recursos.

### 17.2. Prometheus

La extensión de Micrometer para Prometheus permite exponer métricas en un formato que Prometheus puede recopilar.

El endpoint habitual de métricas es:

`/q/metrics`

Las métricas disponibles dependen de los componentes instrumentados y de la configuración utilizada.

Las métricas permiten investigar problemas de rendimiento y observar tendencias, pero no sustituyen los registros ni las trazas.

---

## 18. Trazabilidad con OpenTelemetry

### 18.1. ¿Qué es la trazabilidad?

La trazabilidad permite seguir una operación a través de los componentes de una aplicación.

Una traza puede contener spans que representan distintas operaciones y ayudan a identificar dónde se consume el tiempo.

### 18.2. MongoDB y OpenTelemetry

La instrumentación compatible puede generar información sobre las operaciones realizadas por el controlador MongoDB.

El nivel de detalle debe configurarse cuidadosamente para evitar exponer datos personales, credenciales o valores sensibles.

### 18.3. Destino de las trazas

Para consultar las trazas se necesita un destino compatible, como un colector de OpenTelemetry y una herramienta de visualización.

Activar la instrumentación no implica que automáticamente exista una plataforma de visualización.

---

## 19. Seguridad y TLS

### 19.1. Credenciales

La autenticación permite controlar quién puede conectarse a MongoDB.

En producción deben utilizarse usuarios con los permisos mínimos necesarios para las operaciones de la aplicación.

Las credenciales deben gestionarse mediante variables de entorno o un sistema de secretos, evitando almacenarlas directamente en el código.

### 19.2. TLS

TLS cifra la comunicación entre el cliente y el servidor y permite verificar la identidad del servidor mediante certificados.

La configuración puede incluir:

```properties
quarkus.mongodb.tls=true
```

Esta propiedad solo debe utilizarse de acuerdo con la configuración del servidor MongoDB.

No se recomienda desactivar la validación de certificados para resolver errores de conexión. La solución correcta es configurar certificados y confianza adecuadamente.

### 19.3. Seguridad de producción

Antes de desplegar la aplicación, se debe comprobar:

- Autenticación configurada.
- Permisos mínimos para el usuario de MongoDB.
- URI de conexión protegida.
- TLS cuando corresponda.
- Certificados correctamente validados.
- Ausencia de contraseñas en Git y en los registros.
- Separación entre los entornos de desarrollo y producción.

---

## 20. Personalización y cliente heredado

### 20.1. Personalización del cliente

Quarkus permite configurar opciones del controlador MongoDB mediante mecanismos de personalización compatibles con la versión utilizada.

Esto permite ajustar opciones sin modificar los servicios que ejecutan las consultas.

La personalización debe probarse para confirmar que el cliente recibe realmente la configuración esperada.

### 20.2. Cliente heredado

Las APIs heredadas pueden ser necesarias cuando un proyecto antiguo depende de una versión anterior del controlador.

Para código nuevo se recomienda utilizar las APIs modernas compatibles con la versión instalada de Quarkus.

No se deben agregar dependencias heredadas si no existe una necesidad concreta de compatibilidad.

---

## 21. Compilación nativa

Quarkus admite la generación de ejecutables nativos mediante las herramientas compatibles con el entorno de compilación.

Una compilación nativa puede reducir el tiempo de arranque y el consumo de memoria en ciertos escenarios.

Para una compilación Maven normal:

```powershell
.\mvnw clean package
```

Para intentar generar una aplicación nativa:

```powershell
.\mvnw package -Dnative
```

El segundo comando requiere el entorno de compilación nativa apropiado.

Las extensiones y los codecs deben ser compatibles con el modo de compilación elegido. Una compilación nativa exitosa no debe darse por garantizada sin realizar la prueba.

---

## 22. Ejecución del proyecto

Desde la raíz del módulo:

### Desarrollo

```powershell
.\mvnw quarkus:dev
```

### Compilación

```powershell
.\mvnw clean compile
```

### Pruebas

```powershell
.\mvnw test
```

### Empaquetado

```powershell
.\mvnw clean package
```

MongoDB debe estar disponible con la configuración correspondiente antes de ejecutar las operaciones que acceden a la base de datos.

---

## 23. Endpoints principales

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/mongodb/actividades` | Lista actividades |
| GET | `/api/mongodb/actividades/{id}` | Consulta una actividad |
| POST | `/api/mongodb/actividades` | Crea una actividad |
| PUT | `/api/mongodb/actividades/{id}` | Actualiza una actividad |
| DELETE | `/api/mongodb/actividades/{id}` | Elimina una actividad |
| GET | `/api/mongodb/actividades/buscar` | Consulta mediante filtros |
| GET | `/api/mongodb/reactivas/actividades` | Lista actividades con el recurso reactivo |
| POST | `/api/mongodb/reactivas/actividades` | Crea una actividad reactiva |
| GET | `/q/health` | Estado general de salud |
| GET | `/q/health/live` | Liveness |
| GET | `/q/health/ready` | Readiness |
| GET | `/q/metrics` | Métricas, si están configuradas |

Los endpoints reactivos adicionales, como búsqueda por identificador y eliminación, dependen de la implementación concreta del recurso.

---

## 24. Estructura conceptual del módulo

Los principales componentes incorporados son:

```text
src/
├── main/
│   ├── java/
│   │   └── com/inklusport/
│   │       ├── api/
│   │       │   ├── ActividadMongoResource.java
│   │       │   └── ActividadMongoReactiveResource.java
│   │       ├── model/
│   │       │   ├── ActividadMongo.java
│   │       │   ├── ActividadMongoCodec.java
│   │       │   └── ActividadPanache.java
│   │       ├── service/
│   │       │   ├── ActividadMongoService.java
│   │       │   ├── ActividadMongoReactiveService.java
│   │       │   └── ActividadPanacheRepository.java
│   │       └── health/
│   │           └── MongoDBHealthCheck.java
│   └── resources/
│       ├── application.properties
│       ├── application-prod.properties
│       └── META-INF/
│           └── resources/
│               └── mongodb.html
└── test/
    └── java/
        └── com/inklusport/
            └── api/
                └── ActividadMongoResourceTest.java
```

Esta estructura es orientativa. Debe ajustarse a las clases que realmente existen en el repositorio, ya que algunos ejemplos de configuración o servicios pueden no haberse incorporado todavía.

---

## 25. Buenas prácticas

1. Separar los recursos REST de la lógica de negocio y persistencia.
2. Validar los datos recibidos por la API.
3. Utilizar códigos HTTP coherentes con cada operación.
4. Evitar modificar entidades relacionales para adaptarlas a MongoDB.
5. Utilizar índices de acuerdo con las consultas reales.
6. Mantener las credenciales fuera del repositorio.
7. Probar las migraciones en un entorno de desarrollo.
8. No ejecutar pruebas destructivas contra producción.
9. Evitar registrar valores sensibles en trazas y logs.
10. Confirmar la compatibilidad de las extensiones con la versión de Quarkus.
11. Ejecutar compilación y pruebas después de incorporar nuevos componentes.
12. Mantener separadas las colecciones de demostración cuando los ejemplos tengan modelos de datos diferentes.

---

## 26. Checklist de validación

Antes de considerar el módulo completamente validado, comprobar:

- [ ] MongoDB está disponible.
- [ ] La conexión desde Quarkus funciona.
- [ ] El CRUD síncrono funciona.
- [ ] Los filtros devuelven resultados correctos.
- [ ] La interfaz web consume los endpoints.
- [ ] El cliente reactivo compila y funciona.
- [ ] El codec BSON está registrado y probado.
- [ ] MongoDB with Panache funciona de manera independiente.
- [ ] Las pruebas automatizadas finalizan correctamente.
- [ ] Los health checks muestran el estado esperado.
- [ ] Las métricas están disponibles.
- [ ] Las trazas llegan al destino configurado.
- [ ] Las credenciales y TLS están configurados para el entorno.
- [ ] Los clientes nombrados y la personalización funcionan.
- [ ] Las migraciones se ejecutan correctamente en desarrollo.
- [ ] La compilación normal finaliza correctamente.
- [ ] La compilación nativa se prueba si forma parte de los requisitos del proyecto.

---

## 27. Conclusión

La integración de MongoDB en Inklusport permite ampliar el proyecto con una base de datos documental sin reemplazar las funcionalidades relacionales ya existentes.

A lo largo de la guía se estudian los fundamentos de MongoDB, la configuración en Quarkus, las operaciones CRUD, los filtros, el consumo desde una interfaz web, el modelo reactivo, los codecs BSON y MongoDB with Panache.

También se presentan herramientas para mantener la aplicación, como las pruebas automatizadas, los health checks, las métricas y la trazabilidad, junto con conceptos de seguridad, TLS, clientes nombrados y compilación nativa.

El objetivo final es contar con una base sólida para desarrollar, probar, mantener y desplegar funcionalidades MongoDB dentro de una aplicación Java con Quarkus.

**Nota de estado:** este documento describe los conceptos y ejemplos trabajados durante la guía. La finalización temática no sustituye la comprobación real de cada extensión, configuración y endpoint en el proyecto.
