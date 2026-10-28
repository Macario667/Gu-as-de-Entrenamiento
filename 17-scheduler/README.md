# Inklusport 17 - Quarkus Scheduler

## Descripción

Este proyecto forma parte de la serie de aprendizaje de Quarkus para Inklusport. Su objetivo es aprender a programar y gestionar tareas periódicas utilizando **Quarkus Scheduler**.

## Tecnologías

- Java 17
- Quarkus
- Maven
- Quarkus Scheduler
- Jakarta REST

## Funcionalidades implementadas

- Ejecución periódica de tareas mediante `@Scheduled`.
- Configuración del intervalo desde `application.properties`.
- Programación mediante expresiones cron.
- Prevención de ejecuciones simultáneas con `Scheduled.ConcurrentExecution.SKIP`.
- Registro de ejecuciones y manejo de excepciones mediante `JBoss Logging`.
- Consulta del estado de la tarea a través de un endpoint REST.

## Configuración

Archivo: `src/main/resources/application.properties`

```properties
quarkus.application.name=inklusport-17-scheduler
quarkus.http.port=8080

app.scheduler.intervalo=10s
app.scheduler.activo=true

quarkus.log.level=INFO
quarkus.log.category."com.inklusport".level=DEBUG
```

**Nota:** la propiedad `app.scheduler.activo` se conserva como configuración, pero no controla actualmente la activación de la tarea, porque el atributo `enabled` de `@Scheduled` presentó un error en este proyecto.

## Ejecución

Inicia la aplicación en modo desarrollo:

```bash
mvn quarkus:dev
```

## Endpoint REST

Consulta el estado de la tarea programada:

```http
GET http://localhost:8080/scheduler/estado
```

La respuesta JSON contiene:

- `ultimaEjecucion`: fecha y hora de la última ejecución correcta.
- `ultimoResultado`: resultado registrado de la tarea, como `OK`, `ERROR` o `PENDIENTE`.

El estado se mantiene en memoria y no constituye un historial persistente.

## Conceptos aprendidos

- Programación de tareas periódicas.
- Configuración externa de intervalos.
- Expresiones cron.
- Control de ejecuciones concurrentes.
- Registro y gestión de errores.
- Integración de tareas programadas con un endpoint REST.

## Ejecutar pruebas

```bash
mvn test
```

## Estado del proyecto

Guía 17 completada: práctica principal de Quarkus Scheduler implementada y verificada.
