# Transport Order Management API

API REST de ordenes de transporte con microservicios (Java 17, Spring Boot 3.4, SQL Server, Docker).

* Servicio `gateway` enruta y valida el JWT - Puerto: 8080 (unico expuesto) 
* Servicio `order-service` Login, ordenes, asignaciones, archivos (PDF/imagen como BLOB) - Puerto: 8081 (interno)
* Servicio `driver-service` Conductores - Puerto: 8082 (interno)
* Servicio `sqlserver` BD `orders_db` y `drivers_db`


## Ejecutar con Docker

Requisitos: Docker Desktop iniciado. No hace falta Java ni Maven: la compilacion ocurre dentro de la imagen.

```bash
# 1. Clonar y entrar al proyecto
git clone <url-del-repo>
cd transport-order-management-api

# 2. Construir y levantar todo (SQL Server, driver-service, order-service, gateway)
docker compose up --build -d

# 3. Ver logs (todos los servicios o uno solo)
docker compose logs -f
docker compose logs -f order-service

# 4. Detener
docker compose down        # conserva los datos
docker compose down -v     # tambien borra la base de datos
```
La primera vez tarda varios minutos (descarga de imagenes y dependencias). La API queda lista cuando `docker compose ps` muestra `gateway` en estado `running`; entra por `http://localhost:8080`.

Variables opcionales (con valores por defecto para desarrollo; cambialos fuera de local):

| `DB_PASSWORD` | `DevOnly_SqlServer_2026!` | Password de `sa` en SQL Server |
| `JWT_SECRET` | `DevOnly-JWT-Secret-32-Chars-2026!` | Secreto HMAC del JWT (minimo 32 caracteres) |
| `AUTH_USER` / `AUTH_PASSWORD` | `admin` / `admin123` | Credenciales de `/auth/login` |

Ejemplo: `$env:JWT_SECRET=DevOnly-JWT-Secret-32-Chars-2026!"; docker compose up --build -d`

## Swagger

La documentación OpenAPI está disponible a través del API Gateway.
Los servicios internos no necesitan exponerse directamente.

Después de obtener un JWT mediante `/auth/login`, se puede utilizar el botón **Authorize** de Swagger para realizar las peticiones autenticadas.

- Order Service: `http://localhost:8080/docs/orders/swagger-ui.html`
- Driver Service: `http://localhost:8080/docs/drivers/swagger-ui.html`

Notas:

Hay que reconstruir (docker compose up --build -d, o docker compose build order-service para un solo servicio) cuando cambias:

-Código Java de un servicio.
-application.yml de un servicio, cuando está empaquetado dentro del JAR.
-Un pom.xml, es decir, dependencias o versiones.
-El Dockerfile.

No hace falta reconstruir (basta docker compose up -d, que recrea el contenedor si cambió la configuración) cuando cambias:

-Variables de entorno en docker-compose.yml (environment), por ejemplo JWT_SECRET, AUTH_USER o AUTH_PASSWORD.
-Puertos, depends_on o volúmenes.
-Variables definidas desde la terminal ($env:JWT_SECRET=...).
-Solo se vuelven a levantar los contenedores afectados.
-Tampoco hace falta cuando solo cambias el README.md o los tests. El docker-compose.yml y el README.md entran al build por el COPY . ., pero un cambio en ellos solo invalida la caché del build y no cambia el JAR resultante.


## Uso

```bash
# 1. Token (usuario por defecto admin / admin123)
curl -X POST localhost:8080/auth/login -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

TOKEN=<token>

# 2. Conductor y orden
curl -X POST localhost:8080/api/drivers -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"name":"Victoria","licenseNumber":"LIC-1"}'
curl -X POST localhost:8080/api/orders -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"origin":"Mexico","destination":"Chile"}'

# 3. Asignar y subir archivos
curl -X POST localhost:8080/api/assignments -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"orderId":"<orderId>","driverId":"<driverId>"}'
curl -X POST localhost:8080/api/assignments/<id>/files  -H "Authorization: Bearer $TOKEN" -F "file=@doc.pdf"
curl -X POST localhost:8080/api/assignments/<id>/images -H "Authorization: Bearer $TOKEN" -F "file=@foto.png"
```

## Manejo de errores

Cada servicio tiene un `@RestControllerAdvice` (`ApiExceptionHandler`) que responde en formato `ProblemDetail`, con `Content-Type: application/problem+json`:

 400: Validacion de DTO (`@Valid`) o parametro con formato invalido
 401: Sin token, token invalido/expirado (gateway) o credenciales incorrectas
 404: Orden, conductor o asignacion inexistente
 409: Regla de negocio: transicion de estado invalida, conductor inactivo, orden no `CREATED` o ya asignada, licencia duplicada
 415: Archivo con tipo no permitido (PDF en `/files`; png/jpeg en `/images`)
 502: `driver-service` no disponible al asignar
 500: Error inesperado (el detalle solo queda en el log, no se expone)

Ejemplo (400):

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Invalid request content.",
  "instance": "/api/orders",
  "errors": { "origin": "must not be blank" }
}
```

## Logging

Se usa **SLF4J** (API) con **Logback** (implementacion por defecto de Spring Boot), sin librerias adicionales. Se utiliza el soporte de logging estructurado disponible en Spring Boot 3.4 (`logging.structured.format.console: ecs`, Elastic Common Schema), una linea JSON por evento:

```json
{"@timestamp":"2026-10-01T10:15:30.123Z","log.level":"INFO","process.pid":1,"service.name":"order-service","log.logger":"com.vruiz.order.order.OrderService","message":"Order status changed id=... from=CREATED to=IN_TRANSIT","ecs.version":"8.11"}
```

Eventos registrados: creacion de ordenes y conductores, cambios de estado, asignaciones, archivos almacenados (`INFO`), violaciones de integridad (`WARN`) y errores inesperados o fallos de `driver-service` con stack trace (`ERROR`). Los logs van a la salida estandar del contenedor (`docker compose logs`).

## Pruebas unitarias

JUnit 5 y Mockito; no necesitan base de datos ni Docker Compose levantado.

Con Maven instalado:

```bash
mvn test
```

Sin Maven local, usando Docker (PowerShell, desde la raiz del proyecto):

```powershell
docker run --rm -v "${PWD}:/app" -v m2cache:/root/.m2 -w /app maven:3.9-eclipse-temurin-17 mvn test
```

Tambien se pueden ejecutar desde IntelliJ IDEA (Maven → Lifecycle → `test`). Para un solo modulo: `mvn test -pl order-service -am`.

