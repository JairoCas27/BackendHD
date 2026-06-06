# UrbanPark Parking API

API REST SaaS para la gestión de acceso vehicular en condominios (planes, titulares, condominios, reglas de acceso, solicitudes de plan, usuarios internos y módulo de contacto público). Incluye autenticación JWT, seguridad con Spring Security, documentación OpenAPI/Swagger y envío de correos.

## Tecnologías

- Java 17
- Spring Boot 4.0.6
- Spring Web MVC
- Spring Security
- Spring Data JPA (PostgreSQL)
- JWT (jjwt 0.12.3)
- springdoc-openapi-starter-webmvc-ui 2.8.8 (Swagger UI)
- Spring Mail
- Lombok
- Hibernate Validator

## Estructura de proyecto

```text
src
├── main
│   ├── java
│   │   └── com.urbanpark.parking
│   │       ├── ParkingApplication.java
│   │       ├── config
│   │       │   ├── AppConfig.java
│   │       │   ├── DataInitializer.java
│   │       │   ├── GlobalExceptionHandler.java
│   │       │   ├── JacksonConfig.java
│   │       │   └── OpenApiConfig.java
│   │       ├── domain
│   │       │   ├── auth
│   │       │   │   ├── AuthController.java
│   │       │   │   ├── AuthService.java
│   │       │   │   ├── dto
│   │       │   │   │   ├── ChangePasswordRequest.java
│   │       │   │   │   ├── ForgotPasswordRequest.java
│   │       │   │   │   ├── LoginRequest.java
│   │       │   │   │   ├── LoginResponse.java
│   │       │   │   │   ├── ProfileResponse.java
│   │       │   │   │   ├── RegisterRequest.java
│   │       │   │   │   └── ResetPasswordRequest.java
│   │       │   │   └── validators
│   │       │   │       └── RegisterValidator.java
│   │       │   ├── condominios
│   │       │   │   ├── Condominio.java
│   │       │   │   ├── CondominioController.java
│   │       │   │   ├── CondominioRepository.java
│   │       │   │   ├── CondominioService.java
│   │       │   │   └── dto
│   │       │   │       ├── CondominioRequest.java
│   │       │   │       ├── CondominioResponse.java
│   │       │   │       └── VerificacionRequest.java
│   │       │   ├── notifications
│   │       │   │   └── contactanos
│   │       │   │       ├── ContactoController.java
│   │       │   │       ├── ContactoMensaje.java
│   │       │   │       ├── ContactoMensajeRepository.java
│   │       │   │       ├── ContactoMensajeService.java
│   │       │   │       └── dto
│   │       │   │           ├── ContactoPublicResponse.java
│   │       │   │           ├── ContactoRequest.java
│   │       │   │           ├── ContactoResponse.java
│   │       │   │           └── RespuestaRequest.java
│   │       │   ├── planes
│   │       │   │   ├── Plan.java
│   │       │   │   ├── PlanController.java
│   │       │   │   ├── PlanRepository.java
│   │       │   │   ├── PlanService.java
│   │       │   │   └── dto
│   │       │   │       ├── PlanRequest.java
│   │       │   │       └── PlanResponse.java
│   │       │   ├── reports
│   │       │   │   ├── ReportController.java
│   │       │   │   ├── ReportService.java
│   │       │   │   └── dto
│   │       │   │       ├── AdminClientesStatsDTO.java
│   │       │   │       ├── GlobalStatsDTO.java
│   │       │   │       ├── ReporteDetalladoDTO.java
│   │       │   │       ├── TitularStatsDTO.java
│   │       │   │       ├── TopPlanDTO.java
│   │       │   │       ├── TopPlanesStatsDTO.java
│   │       │   │       └── UsuarioStatsDTO.java
│   │       │   ├── rules
│   │       │   │   ├── ReglaAcceso.java
│   │       │   │   ├── ReglaAccesoController.java
│   │       │   │   ├── ReglaAccesoRepository.java
│   │       │   │   ├── ReglaAccesoService.java
│   │       │   │   ├── RuleEngine.java
│   │       │   │   └── dto
│   │       │   │       ├── ReglaRequest.java
│   │       │   │       ├── ReglaResponse.java
│   │       │   │       ├── ValidacionRequest.java
│   │       │   │       └── ValidacionResult.java
│   │       │   ├── solicitudes
│   │       │   │   ├── SolicitudPlan.java
│   │       │   │   ├── SolicitudPlanController.java
│   │       │   │   ├── SolicitudPlanRepository.java
│   │       │   │   ├── SolicitudPlanService.java
│   │       │   │   └── dto
│   │       │   │       ├── RevisionSolicitudRequest.java
│   │       │   │       ├── SolicitudPlanRequest.java
│   │       │   │       └── SolicitudPlanResponse.java
│   │       │   ├── titulares
│   │       │   │   ├── Titular.java
│   │       │   │   ├── TitularController.java
│   │       │   │   ├── TitularRepository.java
│   │       │   │   ├── TitularService.java
│   │       │   │   └── dto
│   │       │   │       ├── TitularRequest.java
│   │       │   │       └── TitularResponse.java
│   │       │   └── usuarios
│   │       │       ├── UsuarioSaas.java
│   │       │       ├── UsuarioSaasController.java
│   │       │       ├── UsuarioSaasRepository.java
│   │       │       ├── UsuarioSaasService.java
│   │       │       └── dto
│   │       │           ├── ActualizarEstadoRequest.java
│   │       │           ├── CrearUsuarioAdminRequest.java
│   │       │           └── UsuarioSaasResponse.java
│   │       ├── security
│   │       │   ├── CustomSecurityExceptionHandler.java
│   │       │   ├── SecurityConfig.java
│   │       │   ├── jwt
│   │       │   │   ├── JwtAuthController.java
│   │       │   │   ├── JwtAuthFilter.java
│   │       │   │   ├── JwtProperties.java
│   │       │   │   └── JwtService.java
│   │       │   ├── otp
│   │       │   │   ├── OtpService.java
│   │       │   │   ├── OtpToken.java
│   │       │   │   └── OtpTokenRepository.java
│   │       │   └── userdetails
│   │       │       └── UserDetailsServiceImpl.java
│   │       └── shared
│   │           ├── dto
│   │           │   └── ApiResponse.java
│   │           ├── enums
│   │           ├── exceptions
│   │           └── utils
│   └── resources
│       └── application.properties
└── test
    └── java
        └── com.urbanpark.parking
            └── ParkingApplicationTests.java
```

## Configuración de entorno

### Variables de entorno (spring-dotenv)

El proyecto usa `spring-dotenv` para cargar variables desde un archivo `.env` en la raíz. Algunos valores típicos:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/urbanpark
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres

# O para MySQL
# SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/urbanpark?useSSL=false
# SPRING_DATASOURCE_USERNAME=root
# SPRING_DATASOURCE_PASSWORD=123456

SPRING_JPA_HIBERNATE_DDL_AUTO=update

# JWT
JWT_SECRET=tu_clave_secreta_base64
JWT_EXPIRATION=900000
JWT_REFRESH_EXPIRATION=604800000

# Mail
SPRING_MAIL_HOST=smtp.tu-proveedor.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=tu-correo@dominio.com
SPRING_MAIL_PASSWORD=tu-password
SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH=true
SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE=true

APP_CONTACT_DESTINATION_EMAIL=soporte@urbanpark.com
APP_MAIL_NOMBRE_REMITENTE=UrbanPark SaaS
```

Asegúrate de mapear estos nombres a tus propiedades reales en `application.properties` y en `JwtProperties`, `ContactoMensajeService`, etc. [web:163][web:166]

### `application.properties` mínimo

```properties
spring.application.name=urbanpark-parking

spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
spring.jpa.hibernate.ddl-auto=${SPRING_JPA_HIBERNATE_DDL_AUTO:update}
spring.jpa.show-sql=true

# JWT
jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION:900000}
jwt.refresh-expiration=${JWT_REFRESH_EXPIRATION:604800000}

# Mail
spring.mail.host=${SPRING_MAIL_HOST}
spring.mail.port=${SPRING_MAIL_PORT}
spring.mail.username=${SPRING_MAIL_USERNAME}
spring.mail.password=${SPRING_MAIL_PASSWORD}
spring.mail.properties.mail.smtp.auth=${SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH:true}
spring.mail.properties.mail.smtp.starttls.enable=${SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE:true}

# Contacto
app.contact.destination-email=${APP_CONTACT_DESTINATION_EMAIL}
app.mail.nombre-remitente=${APP_MAIL_NOMBRE_REMITENTE:UrbanPark SaaS}

# Springdoc / Swagger
springdoc.api-docs.enabled=true
springdoc.swagger-ui.path=/swagger-ui.html
```

## Seguridad y JWT

- Autenticación y registro en `domain.auth`:
    - `POST /api/v1/auth/register`
    - `POST /api/v1/auth/login` → retorna `token` y `refreshToken`.
    - `POST /api/v1/auth/refresh` → genera nuevo access token.
- Filtro JWT: `security.jwt.JwtAuthFilter`
    - Lee el header `Authorization: Bearer <token>`
    - Valida el token con `JwtService`
    - Carga el usuario con `UserDetailsServiceImpl`
    - Pone el `Authentication` en el `SecurityContext`.
- Configuración: `security.SecurityConfig`
    - Stateless, CSRF deshabilitado.
    - Jerarquía de roles: `SUPERADMIN > ADMIN > CLIENTE`.
    - Rutas públicas vs protegidas definidas por `requestMatchers` y `hasRole/hasAnyRole`. [web:165][web:171]

Ejemplo de uso con Swagger:

1. Llamar `POST /api/v1/auth/login` con email y password.
2. Copiar el `token` JWT de la respuesta.
3. Ir a Swagger UI (`/swagger-ui.html`) → botón `Authorize`.
4. Ingresar el token en el esquema `bearerAuth`.
5. Invocar endpoints protegidos con el header `Authorization` agregado automáticamente. [web:172][web:170]

## Documentación OpenAPI / Swagger

- Dependencia: `springdoc-openapi-starter-webmvc-ui`. [web:167][web:170]
- Configuración en `config.OpenApiConfig`:
    - Define información básica de la API.
    - Define esquema de seguridad `bearerAuth` (HTTP bearer, formato JWT).
    - Aplica seguridad global para que Swagger envíe el token en las llamadas.

Acceso a la UI:

```text
http://localhost:8080/swagger-ui.html
```

(o el puerto que uses).

## Módulos principales

### Autenticación (`domain.auth`)

- Registro de clientes (`RegisterRequest`, `RegisterValidator`).
- Login y generación de JWT/refresh (`LoginRequest`, `LoginResponse`).
- Perfil (`ProfileResponse`), cambio de contraseña y recuperación con OTP (`OtpService`, `OtpToken`).

### Usuarios internos (`domain.usuarios`)

- CRUD de usuarios SaaS internos.
- Gestión de roles (`RolSaas`) y estados (`EstadoUsuarioSaas`).

### Planes (`domain.planes`)

- CRUD de planes (`Plan`, `PlanRequest`, `PlanResponse`).
- Planes públicos activos (`GET /api/v1/planes`) y administración bajo `/api/v1/admin/planes`.

### Titulares y condominios

- Titulares (`domain.titulares`): datos fiscales y plan asociado.
- Condominios (`domain.condominios`): alta, verificación, estado (`EstadoCondominio`).

### Reglas de acceso (`domain.rules`)

- Motor de reglas (`RuleEngine`) y reglas por condominio (`ReglaAcceso`).
- Endpoints para crear, listar, actualizar y validar reglas de acceso.

### Solicitudes de plan (`domain.solicitudes`)

- Flujo de solicitud, revisión y cambio de plan (`SolicitudPlan`, `SolicitudPlanService`).

### Contacto público (`domain.notifications.contactanos`)

- `POST /api/v1/contacto`: público, registra mensaje de contacto y envía correo al corporativo.
- `GET /api/v1/contacto/seguimiento/{codigo}`: público, consulta por código de seguimiento.
- `GET /api/v1/contacto`, `/respondidos`, `/pendientes`, `PATCH /{id}/responder`: privados (ADMIN/SUPERADMIN), gestionan mensajes recibidos.

### Reportes y estadísticas (`domain.reports`)

- Estadísticas globales de usuarios, clientes, planes y titulares.
- Endpoints:
    - `GET /api/v1/reportes/estadisticas-globales` (SUPERADMIN)
    - `GET /api/v1/reportes/estadisticas-titular` (CLIENTE)
    - `GET /api/v1/reportes/estadisticas-clientes` (ADMIN)
    - `GET /api/v1/reportes/top-planes` (ADMIN)
    - `GET /api/v1/reportes/reporte-detallado` (SUPERADMIN)

## Ejecución del proyecto

### Requisitos previos

- JDK 17
- Maven 3.x
- Base de datos PostgreSQL en ejecución

### Comandos

Compilar:

```bash
mvn clean install
```

Ejecutar la aplicación:

```bash
mvn spring-boot:run
```

Ejecutar tests:

```bash
mvn test
```

La API quedará disponible por defecto en:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```