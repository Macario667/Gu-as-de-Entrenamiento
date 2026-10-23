# Preparación para la guía 13: datasource

Referencia de estudio: [Configure data sources in Quarkus](https://quarkus.io/guides/datasource).

Este directorio reserva el lugar para el trabajo de esa guía. Por ahora no se
añaden extensiones, drivers, configuración de conexión, clientes de base de
datos ni almacenamiento persistente. `EventoService` y `ActividadService`
siguen usando datos en memoria.

## Estructura acumulada

La guía siguiente parte del proyecto integrador de las guías 1–12. Se
conservan sus responsabilidades y rutas; el almacenamiento actual queda
encapsulado en los servicios:

```text
com/inklusport/
├── api/          REST, parámetros, estados HTTP, errores y validación
├── client/       REST Client y tolerancia a fallos
├── config/       propiedades, perfiles y fuentes de configuración
├── json/         JSON-P y configuración/serialización Jackson
├── logging/      niveles, filtros, MDC y correlación de peticiones
├── model/        modelos de dominio y DTOs
├── resilience/   políticas de SmallRye Fault Tolerance
├── service/      lógica actual y almacenamiento en memoria
└── persistence/  reservado para estudiar datasource

src/test/java/com/inklusport/
├── api/          pruebas de recursos y contrato HTTP
├── config/       pruebas de configuración
├── json/         pruebas de JSON
├── logging/      pruebas de observabilidad
├── resilience/   pruebas de tolerancia a fallos
└── persistence/  espacio previsto para pruebas del tema cuando se estudie
```

## Continuidad de las guías anteriores

Al avanzar, el punto de entrada HTTP seguirá siendo `api/`; los recursos
seguirán aplicando validación, manejando errores y devolviendo los mismos
formatos JSON. `service/` conservará la lógica de negocio, mientras que este
paquete podrá alojar el acceso a datos cuando corresponda. Configuración,
perfiles, logs, REST Client, OpenAPI y las políticas de resiliencia siguen
disponibles en sus paquetes actuales.

Así, el ejercicio futuro podrá conectar las capas existentes sin convertir
esta preparación en una implementación anticipada del datasource.
