# Guía 16 — MongoDB Panache con Quarkus

## 1. Objetivo de la guía

Esta guía profundiza en la integración de **MongoDB con Quarkus mediante MongoDB Panache**, utilizando como contexto el proyecto Inklusport.

El objetivo es aprender a persistir documentos, consultar información, aplicar filtros, actualizar y eliminar registros, validar los datos recibidos desde una API REST y organizar el acceso a MongoDB mediante una arquitectura por capas.

A lo largo de la guía se trabajan los siguientes conceptos:

- Fundamentos de bases de datos NoSQL y documentos BSON.
- Diferencias entre MongoDB y una base de datos relacional.
- Configuración de MongoDB en Quarkus.
- Modelos MongoDB y estrategias de identificación.
- Diferencias entre el acceso manual y MongoDB Panache.
- Patrón repositorio y separación de responsabilidades.
- Operaciones CRUD.
- Consultas por campos, filtros combinados y ordenación.
- Paginación de resultados.
- Actualización de documentos y gestión de identificadores.
- Validación de datos.
- Manejo de errores HTTP.
- Pruebas de integración.
- Configuración por perfiles y variables de entorno.
- Índices y criterios básicos de rendimiento.

> **Nota:** el proyecto Inklusport es acumulativo. Esta guía añade una implementación basada en Panache y debe conservar las clases, servicios, recursos y pruebas desarrollados en las guías anteriores.

---

## 2. Fundamentos de MongoDB

### 2.1. ¿Qué es MongoDB?

MongoDB es una base de datos NoSQL orientada a documentos. En lugar de organizar la información exclusivamente mediante tablas y filas, almacena documentos dentro de colecciones.

Los conceptos principales son:

| MongoDB | Concepto equivalente aproximado en SQL |
|---|---|
| Base de datos | Base de datos |
| Colección | Tabla |
| Documento | Fila o registro |
| Campo | Columna o atributo |
| `_id` | Clave primaria |

Estas equivalencias sirven como referencia, pero las estructuras no son idénticas. MongoDB permite documentos con estructuras flexibles y campos anidados.

### 2.2. Documentos BSON

Los documentos de MongoDB se almacenan en BSON, un formato binario similar a JSON que admite distintos tipos de datos.

Un documento de ejemplo para Inklusport podría ser:

```json
{
  "_id": "672ab1234567890123456789",
  "nombre": "Natación adaptada",
  "deporte": "Natación",
  "capacidad": 12
}
```

Este documento representa una actividad deportiva con un nombre, un deporte y una capacidad máxima.

Los campos no tienen que seguir obligatoriamente el esquema rígido de una tabla relacional. Sin embargo, en una aplicación bien diseñada conviene definir modelos y reglas de validación para mantener la coherencia de los datos.

### 2.3. Colecciones

Una colección agrupa documentos relacionados. En esta guía, la implementación de Panache utiliza la colección:

`actividades_panache`

La anotación `@MongoEntity` permite asociar explícitamente una entidad con una colección:

```java
@MongoEntity(collection = "actividades_panache")
public class ActividadPanache extends PanacheMongoEntity {
    public String nombre;
    public String deporte;
    public Integer capacidad;
}
```

De esta manera, la entidad queda vinculada a una colección concreta, en lugar de depender de una convención implícita para determinar su nombre.

### 2.4. El identificador `_id`

Cada documento de MongoDB tiene un identificador único, almacenado en el campo `_id`.

MongoDB utiliza habitualmente el tipo `ObjectId`, aunque también admite otros tipos de identificadores.

En esta guía debemos distinguir tres modelos del proyecto:

- `Actividad`: modelo relacional con identificador `Long`.
- `ActividadMongo`: modelo utilizado en la implementación MongoDB manual, con identificador de texto.
- `ActividadPanache`: modelo de MongoDB Panache que hereda un identificador `ObjectId`.

Esta separación es importante porque **un `Long`, un `String` y un `ObjectId` no son intercambiables automáticamente**. Cada modelo debe utilizar el tipo de identificador correspondiente a su mecanismo de persistencia.

---

## 3. ¿Qué aporta MongoDB Panache?

MongoDB Panache simplifica el acceso a MongoDB desde Quarkus. Proporciona operaciones habituales de persistencia y consulta sin necesidad de escribir todo el código de acceso a la base de datos manualmente.

Entre sus ventajas están:

- Reducir código repetitivo.
- Simplificar las operaciones CRUD.
- Facilitar las consultas por campos y condiciones.
- Integrarse con la inyección de dependencias de Quarkus.
- Permitir trabajar con un patrón repositorio o con el patrón Active Record.

### 3.1. Patrón Active Record

En el patrón Active Record, el propio modelo hereda funcionalidades de persistencia.

Por ejemplo:

```java
public class ActividadPanache extends PanacheMongoEntity {
    public String nombre;
    public String deporte;
    public Integer capacidad;
}
```

Al extender `PanacheMongoEntity`, la clase obtiene un identificador `ObjectId` y métodos de persistencia asociados al modelo.

Este enfoque puede resultar cómodo en aplicaciones pequeñas o cuando se desea trabajar directamente con las entidades.

### 3.2. Patrón Repository

En el patrón repositorio, las operaciones de persistencia se centralizan en una clase independiente.

En esta guía utilizamos:

```java
@ApplicationScoped
public class ActividadPanacheRepository
        implements PanacheMongoRepository<ActividadPanache> {
}
```

El repositorio recibe como parámetro el tipo de entidad que administra.

Al implementar `PanacheMongoRepository<ActividadPanache>`, se obtienen operaciones comunes como listar, buscar, persistir, actualizar y eliminar, sin tener que implementarlas desde cero.

**¿Por qué utilizar el repositorio?**

Porque permite mantener separadas las responsabilidades:

- El modelo representa los datos.
- El repositorio se encarga del acceso a MongoDB.
- El servicio contiene la lógica de negocio.
- El recurso REST recibe solicitudes HTTP y devuelve respuestas.

Esta estructura facilita el mantenimiento y las pruebas.

---

## 4. Configuración de MongoDB en Quarkus

### 4.1. Dependencias

Para utilizar MongoDB Panache es necesario incorporar la extensión correspondiente al proyecto.

La dependencia habitual de Maven es:

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-mongodb-panache</artifactId>
</dependency>
```

Si se utiliza validación mediante anotaciones como `@NotBlank`, `@NotNull` y `@Min`, también se necesita la extensión de Hibernate Validator:

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-hibernate-validator</artifactId>
</dependency>
```

Las versiones deben mantenerse alineadas con el BOM de Quarkus utilizado por el proyecto.

### 4.2. Configuración de la base de datos

Una configuración básica en `application.properties` puede incluir:

```properties
quarkus.mongodb.database=inklusport
```

Esta propiedad identifica la base de datos predeterminada que utilizará la aplicación.

La conexión puede configurarse mediante la propiedad correspondiente al URI de MongoDB. En desarrollo local debe utilizarse la dirección real del servidor disponible en el entorno.

Ejemplo de configuración por variable de entorno:

```properties
quarkus.mongodb.connection-string=${MONGODB_CONNECTION_STRING}
```

La variable `MONGODB_CONNECTION_STRING` debe estar definida antes de iniciar la aplicación.

No se deben subir contraseñas ni credenciales reales al repositorio.

### 4.3. Perfiles de configuración

Quarkus permite mantener configuraciones distintas según el entorno.

Por ejemplo:

- `application.properties`: configuración común.
- `application-dev.properties`: ajustes de desarrollo.
- `application-prod.properties`: ajustes de producción.

Conviene mantener la base de datos, la conexión y las credenciales correctamente separadas para evitar que una ejecución de desarrollo utilice accidentalmente recursos de producción.

Antes de añadir propiedades nuevas, hay que revisar las existentes para evitar duplicados o conflictos.

---

## 5. Creación del modelo `ActividadPanache`

El modelo que se utiliza en esta parte de la guía es:

```java
package com.inklusport.model;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;

@MongoEntity(collection = "actividades_panache")
public class ActividadPanache extends PanacheMongoEntity {
    public String nombre;
    public String deporte;
    public Integer capacidad;
}
```

### 5.1. Explicación de la clase

- `@MongoEntity`: indica la colección de MongoDB asociada a la entidad.
- `PanacheMongoEntity`: proporciona el identificador `ObjectId` y las funcionalidades de Active Record.
- `nombre`: nombre de la actividad.
- `deporte`: disciplina deportiva.
- `capacidad`: número máximo de participantes.

El uso de campos públicos es compatible con el estilo de entidades de Panache. Si el proyecto necesita controlar estrictamente el acceso a los atributos, puede optarse por otra estrategia de encapsulación.

### 5.2. Validación del modelo

Si queremos impedir actividades sin nombre o sin deporte, podemos añadir restricciones:

```java
@NotBlank
public String nombre;

@NotBlank
public String deporte;

@NotNull
@Min(1)
public Integer capacidad;
```

Para utilizarlas, hay que importar las anotaciones de Jakarta Validation.

Estas reglas expresan que:

- El nombre no puede ser nulo, vacío o estar compuesto únicamente por espacios.
- El deporte debe tener un valor válido.
- La capacidad debe estar informada y ser igual o superior a uno.

La declaración de estas restricciones no garantiza por sí sola que se validen todas las operaciones. La validación debe activarse en el punto de entrada apropiado, por ejemplo, mediante `@Valid` en el recurso REST.

---

## 6. Creación del repositorio

La clase `ActividadPanacheRepository` centraliza el acceso a la colección.

```java
package com.inklusport.service;

import com.inklusport.model.ActividadPanache;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ActividadPanacheRepository
        implements PanacheMongoRepository<ActividadPanache> {
}
```

### Conceptos importantes

**`@ApplicationScoped`**

Permite que CDI gestione la instancia del repositorio con un ámbito de aplicación.

**`PanacheMongoRepository<ActividadPanache>`**

Asocia el repositorio al modelo `ActividadPanache` y proporciona operaciones habituales de persistencia.

**¿Por qué no implementamos todos los métodos manualmente?**

Porque Panache ya ofrece una API para las operaciones más frecuentes. Solo es necesario escribir código adicional cuando la lógica requerida no queda cubierta por los métodos disponibles.

---

## 7. Capa de servicio

La capa de servicio reúne las operaciones que utiliza el recurso REST.

En esta guía, `ActividadPanacheService` trabaja con `ActividadPanacheRepository` para evitar que el recurso tenga que acceder directamente a la base de datos.

Sus responsabilidades incluyen:

- Listar actividades.
- Buscar por identificador.
- Crear nuevas actividades.
- Actualizar actividades existentes.
- Eliminar actividades.
- Filtrar por deporte, capacidad y nombre.
- Combinar criterios de búsqueda.
- Devolver resultados paginados.

### 7.1. Listar todos los documentos

El repositorio permite utilizar:

```java
repository.listAll();
```

Esta operación devuelve todas las entidades de la colección administrada.

Es apropiada cuando se sabe que el volumen de resultados es pequeño. Para colecciones grandes, conviene utilizar filtros y paginación para evitar cargar todos los documentos en memoria.

### 7.2. Buscar por identificador

Los identificadores de `ActividadPanache` son `ObjectId`. En un endpoint REST, sin embargo, el identificador suele recibirse como texto.

Por eso, el servicio debe convertir y validar el valor recibido antes de utilizarlo.

Conceptualmente, el proceso es:

1. Recibir el identificador como `String`.
2. Comprobar que tenga un formato válido de `ObjectId`.
3. Convertirlo al tipo esperado.
4. Buscar el documento.
5. Devolver el resultado o indicar que no existe.

Es importante distinguir dos situaciones:

- Identificador con formato inválido: la solicitud no contiene un identificador utilizable.
- Identificador válido, pero inexistente: la consulta es correcta, aunque no se encontró ningún documento.

La conversión de identificadores debe manejar las entradas inválidas de forma controlada, sin producir un error interno inesperado.

### 7.3. Crear una actividad

Para crear una actividad, el servicio recibe el modelo, comprueba las reglas de negocio necesarias y solicita su persistencia.

Una precaución importante es no confiar en que el cliente pueda decidir libremente el identificador de una nueva entidad.

Cuando se utiliza la estrategia de identificadores generados por MongoDB, el identificador debe dejarse sin asignar antes de persistir el documento.

El proceso general es:

1. Recibir los datos.
2. Validar los campos.
3. Comprobar las reglas de negocio.
4. Persistir la entidad.
5. Devolver la actividad creada y su identificador.

### 7.4. Actualizar una actividad

Una actualización debe localizar primero el documento existente.

El flujo recomendado es:

1. Validar el identificador recibido.
2. Buscar la actividad.
3. Devolver una respuesta adecuada si no existe.
4. Validar los datos nuevos.
5. Modificar los campos permitidos.
6. Persistir los cambios.
7. Devolver el resultado actualizado, si corresponde.

Actualizar el documento existente permite conservar su identidad y evitar que una operación de modificación se convierta accidentalmente en una creación nueva.

Es preferible modificar explícitamente los campos que se pueden cambiar, en lugar de sustituir datos sin comprobarlos.

### 7.5. Eliminar una actividad

La eliminación se realiza utilizando el identificador.

Antes de devolver una respuesta de éxito, el servicio debe comprobar que la operación haya encontrado y eliminado el documento esperado.

En una API REST, una respuesta `204 No Content` es adecuada cuando la eliminación se ha realizado correctamente y no se necesita devolver un cuerpo.

Si el documento no existe, puede devolverse `404 Not Found`, según el contrato establecido por la API.

---

## 8. Consultas y filtros con Panache

MongoDB Panache permite construir consultas mediante campos y condiciones.

Las consultas deben adaptarse a la sintaxis admitida por la versión y la API de Panache utilizada. No se debe asumir que una consulta escrita para Hibernate ORM Panache funcionará exactamente igual en MongoDB Panache.

### 8.1. Buscar por deporte

El objetivo es recuperar las actividades de una disciplina concreta y ordenarlas por nombre.

El concepto de la consulta es:

```java
repository.list(
    "deporte",
    Sort.by("nombre").ascending(),
    deporte
);
```

La condición filtra los documentos por el campo `deporte`, mientras que `Sort` establece el orden de los resultados.

La ordenación no modifica los documentos guardados; únicamente determina cómo se devuelven.

### 8.2. Filtrar por capacidad mínima

Para recuperar actividades cuya capacidad sea igual o superior a un valor:

```java
repository.list(
    "capacidad >= ?1",
    Sort.by("capacidad").descending(),
    capacidadMinima
);
```

El parámetro `?1` representa el primer argumento de la consulta.

El orden descendente permite mostrar primero las actividades con mayor capacidad.

Los parámetros de consulta deben utilizarse en lugar de construir expresiones concatenando directamente datos del usuario.

### 8.3. Buscar por nombre

Una búsqueda parcial permite localizar actividades cuyo nombre contenga una cadena.

En el ejemplo trabajado se planteó una consulta equivalente a:

```java
repository.list(
    "lower(nombre) like ?1",
    Sort.by("nombre").ascending(),
    "%" + nombre.toLowerCase() + "%"
);
```

**Importante:** las expresiones de funciones y operadores, como `lower` y `like`, deben comprobarse contra la sintaxis soportada por la versión concreta de MongoDB Panache. No se deben dar por válidas únicamente porque sean familiares en SQL o en Hibernate ORM Panache.

Si la expresión no está soportada, se debe utilizar la alternativa apropiada para MongoDB, por ejemplo, una consulta basada en expresiones regulares o en los operadores del controlador.

### 8.4. Combinar varios filtros

Un caso más útil consiste en buscar actividades que cumplan simultáneamente dos condiciones:

- Pertenecer a un deporte determinado.
- Tener una capacidad igual o superior al mínimo indicado.

El criterio lógico puede expresarse así:

```text
deporte = deporteSolicitado
Y
capacidad >= capacidadMinima
```

La combinación de condiciones reduce el número de documentos devueltos y permite construir búsquedas más específicas.

Al implementar este tipo de consultas, hay que comprobar que la sintaxis utilizada y el orden de los parámetros sean compatibles con MongoDB Panache.

### 8.5. Filtros opcionales

Una búsqueda avanzada puede permitir que el cliente envíe solo algunos filtros.

Por ejemplo:

- Solo deporte.
- Solo capacidad mínima.
- Solo capacidad máxima.
- Deporte y capacidad.
- Ningún filtro, si el endpoint permite devolver todos los resultados.

Una implementación dinámica debe agregar únicamente los criterios realmente informados y mantener separados la consulta y sus parámetros.

No es recomendable construir consultas concatenando directamente texto recibido del usuario. Además de los problemas de seguridad y formato, esto dificulta validar los valores y mantener la consulta.

La construcción dinámica debe probarse con cada combinación de filtros y con valores límite.

---

## 9. Ordenación

Panache permite expresar la ordenación mediante `Sort`.

Ejemplos conceptuales:

```java
Sort.by("nombre").ascending()
```

Ordena alfabéticamente por nombre.

```java
Sort.by("capacidad").descending()
```

Muestra primero las actividades de mayor capacidad.

La ordenación debe aplicarse en la consulta siempre que sea posible. Así, MongoDB puede devolver los resultados en el orden solicitado sin necesidad de ordenar toda la colección en la capa REST.

En colecciones grandes, conviene evaluar si los campos de ordenación necesitan índices.

---

## 10. Paginación

Devolver todos los documentos puede ser ineficiente cuando una colección crece.

La paginación divide los resultados en bloques más pequeños.

En Panache se puede utilizar `Page`:

```java
repository.findAll(
    Sort.by("nombre").ascending()
).page(
    Page.of(pagina, tamano)
).list();
```

El ejemplo ilustra la idea de ordenar y obtener una página concreta.

### 10.1. Página y tamaño

- `pagina`: índice de la página solicitada.
- `tamano`: número máximo de elementos por página.

Hay que comprobar si el contrato del endpoint utiliza páginas basadas en cero o en uno. En el ejemplo de Panache, `Page.of(pagina, tamano)` utiliza el índice de página correspondiente a la API de Panache, por lo que el cliente debe respetar esa convención.

### 10.2. Validaciones recomendadas

La paginación debería comprobar:

- Que el índice de página no sea negativo.
- Que el tamaño sea positivo.
- Que el tamaño máximo esté limitado para evitar consultas excesivamente grandes.

Una respuesta de paginación más completa puede incluir el contenido y metadatos como la página actual, el tamaño y el total de resultados. Si el endpoint devuelve solo una lista, esos metadatos deberán añadirse explícitamente si se necesitan.

---

## 11. Recursos REST

El recurso `ActividadPanacheResource` expone las operaciones del servicio mediante HTTP.

La ruta base trabajada para esta implementación es:

`/api/mongodb/panache/actividades`

El recurso debe encargarse de:

- Definir las rutas HTTP.
- Leer los parámetros de la solicitud.
- Validar los datos de entrada.
- Invocar al servicio.
- Traducir los resultados a respuestas HTTP adecuadas.

La lógica de persistencia y las consultas deben mantenerse en la capa de servicio o repositorio, en lugar de concentrarse dentro de cada endpoint.

### 11.1. Endpoints principales

La siguiente tabla resume las rutas trabajadas en la guía. Hay que contrastarla con los métodos que finalmente estén presentes en el código.

| Método | Ruta | Propósito |
|---|---|---|
| GET | `/api/mongodb/panache/actividades` | Listar actividades |
| GET | `/api/mongodb/panache/actividades/{id}` | Buscar por identificador |
| POST | `/api/mongodb/panache/actividades` | Crear una actividad |
| PUT | `/api/mongodb/panache/actividades/{id}` | Actualizar una actividad |
| DELETE | `/api/mongodb/panache/actividades/{id}` | Eliminar una actividad |
| GET | `/api/mongodb/panache/actividades/buscar/deporte` | Filtrar por deporte |
| GET | `/api/mongodb/panache/actividades/buscar/capacidad` | Filtrar por capacidad mínima |
| GET | `/api/mongodb/panache/actividades/buscar/nombre` | Buscar por nombre |
| GET | `/api/mongodb/panache/actividades/pagina` | Obtener una página |
| GET | `/api/mongodb/panache/actividades/buscar` | Combinar filtros, si está implementado |

Antes de cerrar la guía, verifica que las rutas de búsqueda estén declaradas de forma que no entren en conflicto con la ruta parametrizada `/{id}`. En JAX-RS, el orden y las restricciones de los patrones pueden afectar a la resolución de las rutas. Una alternativa es utilizar rutas con segmentos claramente diferenciados.

### 11.2. Códigos HTTP

Las respuestas deben reflejar el resultado de la operación:

| Código | Uso habitual |
|---|---|
| `200 OK` | Consulta o actualización correcta con contenido |
| `201 Created` | Creación correcta |
| `204 No Content` | Operación correcta sin cuerpo de respuesta |
| `400 Bad Request` | Datos o parámetros inválidos |
| `404 Not Found` | Recurso inexistente |
| `500 Internal Server Error` | Error inesperado del servidor |

Los códigos anteriores describen el contrato esperado. La implementación debe verificarse para confirmar que realmente los devuelve en cada caso.

---

## 12. Validación de datos

La validación evita que entren en la aplicación datos incompletos o incorrectos.

### 12.1. Validación declarativa

Anotaciones como estas permiten expresar restricciones en el modelo:

- `@NotBlank`: texto obligatorio y no vacío.
- `@NotNull`: valor obligatorio.
- `@Min(1)`: valor numérico mínimo de uno.

Estas anotaciones documentan las reglas y facilitan la validación automática.

### 12.2. Activación de la validación REST

El parámetro que recibe el cuerpo de una solicitud puede anotarse con `@Valid` para activar la validación declarada.

Cuando los datos no cumplen las restricciones, la aplicación debe devolver una respuesta de error coherente, normalmente `400 Bad Request`.

Es importante comprobar el comportamiento real mediante pruebas, porque la respuesta concreta también puede depender de cómo se gestione la excepción de validación.

### 12.3. Validación sintáctica y de negocio

No todas las reglas se resuelven con anotaciones.

Por ejemplo:

- Validación sintáctica: comprobar que la capacidad sea mayor que cero.
- Validación de negocio: comprobar que no se supere un límite específico de participantes.
- Validación de existencia: comprobar que la actividad que se quiere actualizar exista.
- Validación de identificador: comprobar que el texto recibido represente un `ObjectId` válido.

Las reglas de negocio deben ubicarse en la capa apropiada y no depender exclusivamente de los datos enviados por el cliente.

---

## 13. Índices y rendimiento

MongoDB permite crear índices para reducir el coste de determinadas consultas.

Un índice puede ser útil cuando una colección se consulta repetidamente por campos como:

- `deporte`
- `capacidad`
- `nombre`

Sin embargo, no conviene crear índices indiscriminadamente.

Cada índice ocupa espacio y puede incrementar el coste de las escrituras. Su utilidad depende de las consultas reales y del volumen de datos.

### Buenas prácticas

1. Identificar las consultas frecuentes.
2. Revisar los campos utilizados en filtros y ordenaciones.
3. Analizar si un índice mejora esas consultas.
4. Evitar índices duplicados o innecesarios.
5. Verificar los índices existentes antes de crearlos.
6. Probar el comportamiento con datos representativos.

Si se crea un índice mediante el controlador de MongoDB, la base de datos y la colección utilizadas deben coincidir con la configuración de la aplicación.

En esta guía, cualquier inicialización automática de índices debe revisarse para asegurar que no dependa de un nombre de base de datos incorrecto o de una configuración distinta entre desarrollo y producción.

---

## 14. Acceso directo al controlador de MongoDB

MongoDB Panache cubre muchas operaciones habituales, pero existen situaciones en las que puede resultar necesario utilizar directamente el controlador de MongoDB.

El controlador proporciona acceso a funcionalidades y operadores específicos, como filtros, expresiones regulares y otras operaciones avanzadas.

La decisión entre Panache y el controlador directo depende de la complejidad de la consulta y de las necesidades de la aplicación.

### Recomendaciones

- Utilizar Panache para operaciones sencillas y consultas habituales.
- Recurrir al controlador directo cuando se necesite una funcionalidad específica no cubierta adecuadamente por Panache.
- Mantener el acceso directo a la base de datos encapsulado en una clase o servicio.
- Evitar mezclar múltiples estrategias de persistencia dentro de los recursos REST.
- Verificar la compatibilidad del mapeo de entidades y los codecs cuando se deserializan documentos directamente.

Una entidad que hereda de `PanacheMongoEntity` no debe asumirse automáticamente compatible con cualquier forma de acceso mediante una colección tipada del controlador. El mapeo debe comprobarse con la configuración real del proyecto.

---

## 15. Pruebas de integración

Las pruebas permiten verificar que la API se comporta correctamente y que la integración con MongoDB funciona.

En esta guía se planteó una prueba para el recurso de actividades Panache que comprueba el ciclo básico de creación, consulta y eliminación.

### 15.1. Flujo de prueba

Una prueba de integración puede seguir estos pasos:

1. Enviar una solicitud POST con datos válidos.
2. Comprobar que la respuesta sea `201 Created`.
3. Recuperar el identificador generado.
4. Consultar la actividad mediante GET.
5. Comprobar que el documento existe y que los campos coinciden.
6. Eliminar la actividad.
7. Comprobar que la eliminación devuelve el código esperado.
8. Volver a consultar el identificador.
9. Comprobar que el recurso ya no existe.

### 15.2. Pruebas adicionales recomendadas

Además del ciclo CRUD, conviene probar:

- Identificador mal formado.
- Identificador válido que no existe.
- Campos obligatorios ausentes.
- Capacidad igual a cero o negativa.
- Filtros sin resultados.
- Filtros combinados.
- Parámetros de paginación inválidos.
- Actualización de un documento inexistente.
- Eliminación de un documento inexistente.

### 15.3. Dependencia de MongoDB

Las pruebas que persisten documentos necesitan una instancia de MongoDB disponible o una infraestructura de pruebas configurada correctamente.

Una prueba que falla porque MongoDB no está disponible no demuestra necesariamente que la lógica del endpoint sea incorrecta. Hay que distinguir los errores de infraestructura de los errores de la aplicación.

También es recomendable aislar los datos de prueba para que una ejecución no interfiera con otra.

---

## 16. Comandos de compilación y pruebas

Desde la raíz del proyecto Maven:

### Compilar

```bash
mvn compile
```

Permite comprobar que las clases y dependencias compilan.

### Ejecutar las pruebas

```bash
mvn test
```

Ejecuta las pruebas configuradas para el proyecto. Las pruebas de integración que dependan de MongoDB requieren la infraestructura correspondiente.

### Ejecutar en modo desarrollo

```bash
mvn quarkus:dev
```

Permite ejecutar la aplicación en modo desarrollo.

Antes de cerrar la guía, deben ejecutarse los comandos y comprobarse los resultados reales. No debe documentarse una prueba como exitosa si todavía no se ha ejecutado.

---

## 17. Errores frecuentes y cómo diagnosticarlos

### 17.1. Error de conexión con MongoDB

Posibles causas:

- MongoDB no está iniciado.
- La cadena de conexión es incorrecta.
- La variable de entorno no está definida.
- El usuario o la contraseña no son válidos.
- El perfil activo utiliza otra configuración.

### 17.2. Identificador inválido

Puede ocurrir cuando el cliente envía un texto que no representa un `ObjectId` válido.

La aplicación debe validar el formato y responder con un error controlado.

### 17.3. Consultas incompatibles

Una consulta familiar de SQL o Hibernate ORM Panache puede no estar soportada por MongoDB Panache.

Para resolverlo:

1. Identificar la API utilizada.
2. Revisar la sintaxis compatible con esa API.
3. Probar la consulta con datos conocidos.
4. Añadir pruebas para resultados positivos y negativos.

### 17.4. Documento no encontrado

Una búsqueda puede no devolver resultados porque:

- El identificador no existe.
- El filtro no coincide con los datos almacenados.
- Se está consultando otra base de datos.
- Se está utilizando una colección diferente.
- El tipo del identificador no coincide con el utilizado en MongoDB.

### 17.5. Validaciones que no se ejecutan

Comprobar que:

- La extensión de validación está instalada.
- Las restricciones se han importado correctamente.
- El recurso utiliza `@Valid` donde corresponde.
- La prueba envía datos que incumplen realmente las restricciones.

---

## 18. Buenas prácticas aplicadas en Inklusport

- Mantener separados los modelos relacionales y los modelos MongoDB.
- Utilizar el tipo de identificador apropiado para cada modelo.
- Asociar explícitamente la entidad Panache a su colección.
- Centralizar el acceso a MongoDB mediante el repositorio.
- Mantener la lógica de negocio en el servicio.
- Dejar al recurso REST la responsabilidad de gestionar HTTP.
- Validar los datos de entrada.
- Gestionar identificadores inválidos y documentos inexistentes.
- Utilizar consultas parametrizadas.
- Aplicar paginación cuando sea necesario.
- Evaluar los índices a partir de las consultas reales.
- No exponer credenciales en el repositorio.
- Verificar las consultas y las pruebas contra la versión concreta de Quarkus.
- Conservar la funcionalidad acumulada de las guías anteriores.

---

## 19. Checklist de cierre

Antes de dar por terminada la guía, verifica cada punto:

- [ ] La dependencia de MongoDB Panache está configurada.
- [ ] La base de datos y la conexión corresponden al entorno utilizado.
- [ ] `ActividadPanache` está asociada a `actividades_panache`.
- [ ] El identificador `ObjectId` se gestiona correctamente.
- [ ] El repositorio compila.
- [ ] El servicio implementa las operaciones requeridas.
- [ ] Los endpoints REST coinciden con la implementación.
- [ ] Las rutas estáticas y las rutas con identificador no entran en conflicto.
- [ ] Las consultas por deporte, capacidad y nombre han sido probadas.
- [ ] La consulta combinada funciona con datos representativos.
- [ ] La paginación valida los parámetros.
- [ ] Las restricciones de validación se ejecutan.
- [ ] Los errores producen respuestas HTTP adecuadas.
- [ ] Las pruebas de integración se ejecutan con MongoDB disponible.
- [ ] Los índices, si existen, están justificados y utilizan la base de datos correcta.
- [ ] Las configuraciones no contienen secretos.
- [ ] `mvn compile` finaliza correctamente.
- [ ] `mvn test` finaliza correctamente o sus fallos pendientes están documentados.

---

## 20. Conclusión

MongoDB Panache permite integrar MongoDB con Quarkus de una manera más sencilla y organizada, reduciendo el código necesario para las operaciones habituales de persistencia.

La guía recorre el proceso desde los fundamentos de las bases de datos orientadas a documentos hasta la creación de una entidad, un repositorio, un servicio y una API REST con operaciones CRUD, consultas, filtros, paginación y validación.

La principal lección arquitectónica es que **simplificar la persistencia no significa eliminar la separación de responsabilidades**. Mantener los modelos, repositorios, servicios y recursos bien organizados hace que Inklusport sea más fácil de ampliar, probar y mantener.

El cierre de esta guía debe basarse en la verificación de la implementación real: compilar, ejecutar las pruebas, revisar los endpoints y confirmar que las consultas funcionan con la versión y configuración de MongoDB Panache utilizadas por el proyecto.
