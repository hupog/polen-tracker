# Polen Tracker

Esqueleto ejecutable de una arquitectura asíncrona para recolectar, normalizar y consultar datos de polen. El monorepositorio contiene dos aplicaciones Spring Boot independientes y dos librerías Java compartidas.

## Requisitos

- JDK 25 LTS
- Maven 3.6.3 o posterior
- Docker y Docker Compose para la infraestructura local o el arranque completo

## Módulos

- `applications/pollen-core`: API REST, cliente web integrado, persistencia y publicación/consumo de eventos.
- `applications/pollen-collector`: worker y adaptadores de fuentes.
- `libraries/pollen-domain`: modelo canónico sin dependencias de infraestructura.
- `libraries/pollen-messaging-contracts`: contratos versionados de RabbitMQ.

## Desarrollo desde IntelliJ IDEA

1. Abre el `pom.xml` raíz como proyecto Maven y selecciona un SDK Java 25.
2. Levanta únicamente la infraestructura:

   ```bash
   docker compose up -d postgres rabbitmq minio minio-init
   ```

3. Ejecuta `PollenCoreApplication` y `PollenCollectorApplication` desde IntelliJ. Sus configuraciones predeterminadas apuntan a los puertos locales de Docker.
4. Abre http://localhost:8080. El collector expone su endpoint de salud en http://localhost:8081/actuator/health.

También puedes compilar todo el reactor con:

```bash
mvn clean verify
```

## Arranque completo con Docker Compose

No es obligatorio crear un `.env`: Compose incluye credenciales locales predeterminadas. Si quieres personalizarlas, copia `.env.example` como `.env`; este último está excluido de Git.

```bash
cp .env.example .env   # opcional
docker compose up --build
```

Servicios disponibles:

- Aplicación y cliente web: http://localhost:8080
- RabbitMQ Management: http://localhost:15672
- MinIO API: http://localhost:9000
- MinIO Console: http://localhost:9001
- PostgreSQL: `localhost:5432`

Para detener el entorno sin borrar datos: `docker compose down`. Para borrar también los volúmenes locales: `docker compose down -v`.

## Estado del esqueleto

El flujo mínimo crea una recolección, publica `collection.requested`, permite que el collector seleccione el adaptador mediante Strategy y publica `collection.completed`. Los adaptadores Open-Meteo y CSV son puntos de extensión deliberadamente vacíos; la consulta real, la ingesta S3, la persistencia de mediciones y el outbox transaccional corresponden a las siguientes iteraciones.

Los valores predeterminados son exclusivamente de desarrollo. En cualquier despliegue compartido se deben sustituir mediante variables de entorno o un gestor de secretos.
