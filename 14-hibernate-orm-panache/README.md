# Hibernate ORM with Panache — Inklusport

## Descripción

Proyecto práctico basado en la guía de Quarkus **Hibernate ORM with Panache**. Implementa persistencia con Hibernate ORM y Panache, utilizando los patrones Active Record y Repository para gestionar disciplinas deportivas adaptadas.

## Tecnologías

- Java 17
- Quarkus 3.40.1
- Maven
- Hibernate ORM with Panache
- MySQL
- H2 para pruebas
- Jakarta REST
- Jakarta Validation

## Modelo de datos

La entidad `Disciplina` se encuentra en `com.inklusport.persistence.entity` y representa una disciplina deportiva.

Sus atributos son:

| Campo | Descripción |
|---|---|
| `id` | Identificador generado mediante `PanacheEntity` |
| `nombre` | Nombre de la disciplina; obligatorio y con máximo de 100 caracteres |
| `descripcion` | Descripción opcional de hasta 500 caracteres |
| `adaptada` | Indica si la disciplina está adaptada |

La entidad también incluye una consulta nombrada para filtrar disciplinas según su condición de adaptación.

## Patrones de persistencia

### Active Record

La entidad utiliza `PanacheEntity` para acceder a las operaciones de persistencia directamente desde el modelo.

El recurso `DisciplinaResource` expone operaciones para listar, consultar, crear, actualizar y eliminar disciplinas, además de búsquedas y conteos.

### Repository

La clase `DisciplinaRepository` implementa `PanacheRepository<Disciplina>` y centraliza las operaciones de acceso a datos.

Incluye:

- Búsquedas por identificador y nombre.
- Filtros por condición de adaptación.
- Consultas combinadas mediante parámetros.
- Ordenación ascendente y descendente.
- Paginación y consultas por rango.
- Actualizaciones y eliminaciones masivas.
- Conteos y proyecciones a DTO.
- Consultas con parámetros nombrados.
- Bloqueo pesimista mediante `LockModeType.PESSIMISTIC_WRITE`.

El DTO `DisciplinaDto` permite devolver una representación resumida de las disciplinas.

## Endpoints principales

### Active Record

Prefijo: `/api/disciplinas`

| Método | Ruta | Operación |
|---|---|---|
| GET | `/api/disciplinas` | Listar disciplinas |
| GET | `/api/disciplinas/{id}` | Consultar por identificador |
| POST | `/api/disciplinas` | Crear disciplina |
| GET | `/api/disciplinas/contar` | Contar disciplinas |
| GET | `/api/disciplinas/adaptadas` | Listar disciplinas adaptadas |
| GET | `/api/disciplinas/buscar?nombre=...` | Buscar por nombre |
| PUT | `/api/disciplinas/{id}` | Actualizar disciplina |
| DELETE | `/api/disciplinas/{id}` | Eliminar disciplina |

### Repository

Prefijo: `/api/repositorio/disciplinas`

Incluye endpoints para listar, filtrar, buscar, ordenar, paginar, consultar por rango, actualizar descripciones, eliminar disciplinas adaptadas, contar registros y obtener resúmenes mediante DTO.

Entre las rutas disponibles se encuentran:

- `/adaptadas`
- `/buscar?nombre=...`
- `/ordenadas`
- `/ordenadas/descendentemente`
- `/pagina?numero=0&tamaño=5`
- `/buscar/adaptada?nombre=...&adaptada=...`
- `/contarAdaptadas`
- `/resumen`
- `/buscar-avanzado`
- `/rango?desde=0&hasta=4`

También se ofrecen operaciones de actualización y eliminación masiva. Las operaciones que modifican datos utilizan transacciones cuando corresponde.

## Configuración

La configuración se encuentra en `src/main/resources/application.properties`.

- **Desarrollo:** conexión a MySQL.
- **Pruebas:** base de datos H2.
- **Esquema:** configuración de generación del esquema según el perfil.

Ajusta las credenciales y la URL de MySQL a tu entorno local antes de ejecutar la aplicación.

## Ejecutar el proyecto

Desde la raíz del proyecto:

```bash
./mvnw quarkus:dev
```

En Windows PowerShell:

```powershell
.\mvnw.cmd quarkus:dev
```

La aplicación se ejecuta en el puerto predeterminado de Quarkus, salvo que se haya configurado otro.

## Ejecutar las pruebas

En Windows PowerShell:

```powershell
.\mvnw.cmd test
```

Las pruebas verifican las operaciones de persistencia, búsquedas, filtros, ordenación, paginación, actualizaciones, eliminaciones, conteos y proyecciones.

**Último resultado registrado:** 70 pruebas ejecutadas, 0 fallos, 0 errores y 0 omitidas. Compilación y pruebas finalizadas con `BUILD SUCCESS`.

## Estructura principal

```text
src/
├── main/
│   ├── java/com/inklusport/
│   │   ├── model/
│   │   │   └── DisciplinaDto.java
│   │   ├── persistence/
│   │   │   ├── entity/
│   │   │   │   └── Disciplina.java
│   │   │   └── repository/
│   │   │       └── DisciplinaRepository.java
│   │   └── ...
│   └── resources/
│       └── application.properties
└── test/
    └── java/
        └── ...
```

## Objetivo de aprendizaje

Practicar el uso de Hibernate ORM with Panache en Quarkus, comparar los patrones Active Record y Repository, construir consultas y operaciones transaccionales, y probar la capa de persistencia con una base de datos H2.
