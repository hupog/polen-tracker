# Arquitectura de Polen Tracker

## Estilo arquitectónico

Polen Tracker utiliza arquitectura hexagonal (Ports and Adapters) en cada aplicación. El objetivo es que las reglas de negocio y los casos de uso no dependan de HTTP, RabbitMQ, PostgreSQL, Open-Meteo ni de otros detalles tecnológicos.

La dirección válida de las dependencias es:

```text
adaptadores de entrada -> puertos de entrada -> servicios de aplicación
                                              -> dominio
servicios de aplicación -> puertos de salida <- adaptadores de salida
```

Los adaptadores dependen del núcleo. El núcleo no depende de los adaptadores.

## Capas y responsabilidades

### `domain`

Contiene modelos, estados y comportamiento de negocio sin anotaciones de persistencia o transporte. En `pollen-core`, `PollenCollection` es el agregado que controla las transiciones de una recolección. Los conceptos compartidos de polen viven en `libraries/pollen-domain`.

### `application`

- `port/in`: contratos que exponen los casos de uso.
- `port/out`: capacidades externas que necesita la aplicación.
- `service`: orquestación de casos de uso.
- `model`: comandos y modelos internos que no pertenecen al transporte.

Los servicios trabajan contra interfaces. No conocen controladores, entidades JPA, `RabbitTemplate` ni clientes HTTP.

### `adapter/in`

Entradas al hexágono:

- `web`: controladores REST y DTO de validación HTTP.
- `messaging`: listeners de RabbitMQ y traducción desde contratos de integración.

### `adapter/out`

Implementaciones de los puertos de salida:

- `persistence`: entidades JPA, repositorios Spring Data y mapeo al dominio.
- `messaging`: publicación RabbitMQ y nombres de la topología.
- `source/openmeteo`: cliente y mapeo específico de Open-Meteo.
- `source/csv`: implementación de importación manual.

### `configuration`

Configuración técnica y creación de beans de infraestructura. No contiene reglas de negocio.

## Estructura de paquetes

```text
applications/pollen-core/.../core
├── adapter
│   ├── in
│   │   ├── messaging
│   │   └── web
│   └── out
│       ├── messaging
│       └── persistence
│           ├── collection
│           └── trace
├── application
│   ├── model
│   ├── port
│   │   ├── in
│   │   └── out
│   └── service
├── configuration
└── domain/model

applications/pollen-collector/.../collector
├── adapter
│   ├── in/messaging
│   └── out
│       ├── messaging
│       ├── persistence/trace
│       └── source
│           ├── csv
│           └── openmeteo
├── application
│   ├── model
│   ├── port
│   │   ├── in
│   │   └── out
│   └── service
└── configuration
```

## Flujo de recolección

```text
HTTP POST /api/collections
  -> CollectionController
  -> StartCollectionUseCase
  -> CollectionApplicationService
  -> CollectionStore (PostgreSQL)
  -> CollectionRequestPublisher (RabbitMQ)
  -> collection.requests
  -> CollectionRequestRabbitListener
  -> ProcessCollectionUseCase
  -> CollectionProcessingService
  -> PollenSourcePort (Open-Meteo o CSV)
  -> CollectionEventPublisher
  -> collection.measurement-collected
  -> collection.measurements.core
  -> StorePollenMeasurementUseCase
  -> PollenMeasurementStore (PostgreSQL)
  -> collection.completed / collection.failed
  -> CollectionResultRabbitListener
  -> HandleCollectionResultUseCase
  -> CollectionStore
```

La trazabilidad de mensajes se realiza mediante `MessageTraceStore`, implementado por PostgreSQL, desde los adaptadores Rabbit. Así, los casos de uso no conocen metadatos AMQP.

La creación de una recolección utiliza un outbox transaccional. `collections` y
`collection_request_outbox` se escriben en la misma transacción PostgreSQL. Un relay periódico
reclama lotes con `FOR UPDATE SKIP LOCKED` mediante una transacción corta y cambia las entradas de
`PENDING` a `PROCESSING`, asignándoles propietario y vencimiento de lease. Después las publica en
paralelo mediante un pool limitado, fuera de la transacción de reclamación. El resultado cambia la
entrada a `PUBLISHED` o de nuevo a `PENDING` con backoff. Si una instancia muere, otra puede
recuperar las entradas `PROCESSING` cuyo lease haya vencido.

Los listeners realizan un máximo de tres intentos con backoff exponencial. Cuando se agotan,
rechazan el mensaje sin reencolarlo y RabbitMQ lo enruta mediante `pollen.dlx` a
`pollen.dead-letter`. Un intento fallido no publica por sí mismo `collection.failed`, ya que
el error puede ser transitorio; el tratamiento y eventual reprocesamiento de la DLQ debe decidir
el estado final de la recolección.

## Persistencia de mediciones

Cada evento `collection.measurement-collected` se enruta a la cola durable `collection.measurements.core`. El adaptador de entrada Rabbit delega en `StorePollenMeasurementUseCase`, que persiste mediante `PollenMeasurementStore`.

La tabla `pollen_measurements` representa muestras fechadas y no impone una frecuencia. El campo `valid_at` indica cuándo es válido el dato, independientemente de que la fuente publique cada hora, una vez al día o de forma irregular. No existe una columna de granularidad temporal.

La ingestión es idempotente mediante dos restricciones:

- `event_id` único para detectar la redelivery del mismo evento.
- `(collection_id, logical_key)` único para impedir duplicados lógicos dentro de una recolección.

Las mediciones de una recolección se consultan mediante:

```text
GET /api/collections/{collectionId}/measurements
```

## Modelo común de polen

El dominio común expone tres categorías:

- `TREES`
- `GRASSES`
- `WEEDS`

El adaptador Open-Meteo traduce sus variables específicas:

```text
TREES   = alder_pollen + birch_pollen + olive_pollen
GRASSES = grass_pollen
WEEDS   = mugwort_pollen + ragweed_pollen
```

Esta traducción pertenece al adaptador porque es una decisión vinculada al proveedor. Ningún servicio de aplicación conoce los nombres de Open-Meteo.

## Librerías compartidas

- `pollen-domain`: lenguaje común y modelos independientes de infraestructura.
- `pollen-messaging-contracts`: eventos versionados intercambiados entre aplicaciones.

Los contratos Rabbit no se usan como entidades de persistencia. Los cambios incompatibles requieren incrementar `schemaVersion` y mantener compatibilidad durante la transición.

## Reglas de evolución

1. Un nuevo caso de uso comienza con un puerto de entrada y una implementación en `application/service`.
2. Toda dependencia externa requerida por un caso de uso se expresa como puerto de salida.
3. Un nuevo proveedor de polen implementa `PollenSourcePort` dentro de `adapter/out/source`.
4. Las entidades JPA nunca se devuelven desde controladores ni cruzan hacia el dominio.
5. Los DTO HTTP permanecen en el adaptador web.
6. Los listeners solo traducen mensajes y delegan en casos de uso.
7. Las constantes de exchanges, colas y routing keys permanecen en el adaptador Rabbit.

## Verificación

La compilación reproducible de ambos servicios se ejecuta con:

```bash
docker compose build pollen-core pollen-collector
```
