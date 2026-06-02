# UrbanPark Parking Backend

## Descripción general

UrbanPark Parking Backend es una API construida con Spring Boot orientada a la administración multi-tenant de condominios, control de accesos vehiculares, reglas de negocio, auditoría, autenticación híbrida y operación SaaS. La estructura del proyecto muestra una separación clara por dominios funcionales, lo que facilita el mantenimiento, la escalabilidad y la incorporación de nuevos módulos.

El sistema combina dos niveles de operación. Por un lado, existe una capa SaaS para la gestión global de usuarios administradores, planes y condominios. Por otro lado, existe una capa operativa por tenant, donde cada condominio maneja usuarios, vehículos, accesos, incidentes, espacios de estacionamiento, reglas, reportes y notificaciones.

## Estructura del proyecto

La base del proyecto se encuentra en `src/main/java/com.urbanpark.parking`. A partir de las capturas, la organización principal está dividida en `config`, `domain`, `security` y `shared`, además de los recursos de configuración en `src/main/resources`.

```text
src/main/java/com.urbanpark.parking
├── config
├── domain
│   ├── audit
│   ├── auth
│   ├── integration
│   ├── notifications
│   ├── parking
│   ├── parking_management
│   ├── reports
│   ├── rules
│   ├── saas
│   │   ├── plan
│   │   └── user
│   ├── security_operations
│   ├── tenant
│   └── users
│       ├── usuario
│       └── vehiculo
├── security
├── shared
│   ├── dto
│   ├── enums
│   └── util
└── ParkingApplication
```

## Módulo `config`

El paquete `config` centraliza la configuración transversal del sistema. Según la estructura observada, incluye clases como `AppConfig`, `DataInitializer`, `GlobalExceptionHandler` y `OpenApiConfig`.

### Responsabilidades principales

- `AppConfig`: define beans generales de infraestructura, como `RestTemplate`, configuraciones auxiliares u otros componentes compartidos.
- `DataInitializer`: carga datos iniciales para pruebas o arranque controlado, como usuarios SaaS base, planes, condominios, sesiones ejemplo, reglas o incidencias de demostración.
- `GlobalExceptionHandler`: captura excepciones globales y devuelve respuestas consistentes para toda la API.
- `OpenApiConfig`: configura la documentación Swagger / OpenAPI.

## Módulo `security`

El paquete `security` contiene la capa de autenticación y autorización basada en JWT. En las imágenes se aprecian las clases `JwtAuthFilter`, `JwtService`, `SecurityConfig` y `SecurityContextHelper`.

### Componentes principales

- `JwtService`: genera, valida y extrae claims de los tokens JWT propios del sistema.
- `JwtAuthFilter`: intercepta cada request, valida el token y carga la autenticación en el contexto de Spring Security.
- `SecurityConfig`: define la política global de seguridad, rutas públicas, filtros y acceso autenticado.
- `SecurityContextHelper`: sirve como utilidad para obtener datos del usuario autenticado o simplificar lecturas del contexto de seguridad.

### Enfoque de seguridad

El proyecto maneja dos tipos de autenticación:

1. Autenticación SaaS, para usuarios internos del sistema como `SUPERADMIN` y `ADMIN`.
2. Autenticación externa por condominio, donde el backend valida credenciales contra una API externa, guarda una sesión local y luego genera un JWT propio para operar internamente.

Ese diseño permite trabajar con un esquema desacoplado, donde la identidad externa del usuario se conserva, pero la autorización operativa se resuelve dentro del backend UrbanPark.

## Módulo `shared`

El paquete `shared` agrupa piezas reutilizables por todos los dominios.

### `shared.dto`

Incluye objetos genéricos de intercambio, como `ApiResponse`, usado para estandarizar la salida de la API. Esto permite que todas las respuestas tengan una forma común con campos como éxito, mensaje, data y timestamp.

### `shared.enums`

Contiene los enums globales del sistema. En las imágenes se observan, entre otros:

- `EstadoCondominio`
- `EstadoEspacio`
- `EstadoIncidente`
- `EstadoPlan`
- `EstadoSync`
- `MetodoAcceso`
- `NivelIncidente`
- `RolParking`
- `RolSaas`
- `TipoAccionAudit`
- `TipoEspacio`
- `TipoEvento`
- `TipoIncidente`
- `TipoNotificacion`
- `TipoRegla`
- `TipoVehiculo`

Estos enums son fundamentales para mantener consistencia semántica, validación fuerte y reglas claras de negocio.

### `shared.util`

Incluye utilidades de soporte. En la estructura se aprecia `TenantValidator`, probablemente orientado a validar la coherencia del tenant en requests o procesos internos.

## Módulo `domain`

El paquete `domain` concentra toda la lógica del negocio. Cada submódulo representa un contexto funcional bien separado.

***

## `domain.auth`

Este paquete centraliza la autenticación. En las imágenes se ven DTOs como `AuthResponse`, `ExternalLoginRequest`, `ExternalLoginResult`, `SaasAuthResponse`, `SaasLoginRequest` y `UsuarioExternoDTO`, además de `AuthController`, `AuthService`, `ExternalTokenValidator` y `SaasAuthService`.

### Responsabilidad

Gestiona dos flujos de autenticación:

- Login de usuarios SaaS del sistema principal.
- Login de usuarios de condominios mediante integración con una API externa.

### Componentes clave

- `AuthController`: expone endpoints de autenticación.
- `AuthService`: coordina login externo, registro de sesión local, auditoría y emisión del JWT propio.
- `ExternalTokenValidator`: se encarga de autenticar contra la API del condominio, obtener cookies/tokens externos y consultar la identidad del usuario remoto.
- `SaasAuthService`: maneja autenticación de usuarios de la plataforma SaaS.

Este módulo es clave porque sirve como punto de entrada para la identidad y la sesión del usuario.

***

## `domain.audit`

El módulo `audit` permite registrar y consultar trazabilidad del sistema. En las capturas aparecen `AuditLog`, `AuditLogRepository`, `AuditController`, `AuditService`, `AuditQueryService` y el DTO `AuditLogResponse`.

### Propósito

Registrar eventos relevantes como:

- inicio de sesión,
- intentos fallidos,
- registro y actualización de entidades,
- activaciones o desactivaciones,
- accesos vehiculares,
- incidentes,
- reglas,
- acciones administrativas.

El objetivo es mantener trazabilidad operativa y facilitar monitoreo, control y soporte.

***

## `domain.integration`

Este módulo actúa como capa de integración con sistemas externos. En la estructura se aprecian `IntegrationClient`, `IntegrationController`, `UsuarioSesion`, `UsuarioSesionRepository` y DTOs como `ConexionStatusDTO` y `HealthExternoDTO`.

### Rol del módulo

- Consultar disponibilidad de servicios externos.
- Probar conectividad con la API del condominio.
- Mantener sesiones locales vinculadas a usuarios autenticados externamente.
- Almacenar datos como `externalUserId`, `condominioId`, `rol`, `email`, nombre y token/cookie de acceso externo.

`UsuarioSesion` es una entidad importante porque hace de puente entre el usuario autenticado en el sistema externo y la sesión operativa dentro del backend UrbanPark.

***

## `domain.tenant`

Este módulo representa el contexto multi-tenant del sistema. En las imágenes aparecen `Condominio`, `CondominioController`, `CondominioRepository`, `CondominioService`, `TenantContext`, `TenantFilter` y los DTOs `CondominioRequest` y `CondominioResponse`.

### Responsabilidad

- Gestionar condominios registrados en la plataforma.
- Definir la información principal de cada tenant, como nombre, URL base de API externa, titular, plan y estado.
- Mantener el contexto del tenant durante cada request.

### Componentes destacados

- `Condominio`: entidad principal del tenant.
- `TenantContext`: almacena el tenant actual en contexto por request.
- `TenantFilter`: resuelve y limpia el contexto multi-tenant durante el ciclo HTTP.

Este módulo es la base para aislar datos entre condominios y asegurar que cada operación se ejecute dentro del tenant correspondiente.

***

## `domain.saas`

La carpeta `saas` está separada en dos submódulos: `plan` y `user`.

### `saas.plan`

Incluye `Plan`, `PlanController`, `PlanRepository`, `PlanService` y los DTOs `PlanRequestDTO` y `PlanResponseDTO`.

#### Objetivo

Administrar los planes comerciales de la plataforma SaaS, por ejemplo:

- nombre,
- descripción,
- precio,
- límites funcionales,
- estado.

Esto permite ofrecer distintos niveles de servicio a los condominios registrados.

### `saas.user`

Incluye `SaasUser`, `SaasUserController`, `SaasUserRepository`, `SaasUserService` y sus DTOs de request/response.

#### Objetivo

Gestionar usuarios internos del ecosistema SaaS, como:

- `SUPERADMIN`
- `ADMIN`
- otros roles administrativos globales si se amplía el sistema

Este módulo no representa a los usuarios del condominio, sino a quienes administran la plataforma a nivel central.

***

## `domain.users.usuario`

Este submódulo contiene `UsuarioCondominio`, `UsuarioCondominioController`, `UsuarioCondominioRepository`, `UsuarioCondominioService` y DTOs `UsuarioRequest` y `UsuarioResponse`.

### Función

Administrar usuarios del condominio dentro del dominio local del sistema. Dependiendo de la implementación, puede representar sincronización parcial, caché local o una capa propia para usuarios internos del tenant.

Aquí se manejan perfiles como propietarios, inquilinos, administradores del condominio y otros tipos de usuarios operativos.

***

## `domain.users.vehiculo`

Este módulo es uno de los más importantes para la operación diaria. En las imágenes aparecen:

- `Vehiculo`
- `VehiculoController`
- `VehiculoExternalClient`
- `VehiculoRepository`
- `VehiculoService`
- DTOs `VehiculoRequest`, `VehiculoResponse`, `VehiculoExternalResponse`, `VehiculoExternalPageResponse`

### Responsabilidad

- Gestionar vehículos registrados localmente.
- Consultar vehículos desde la API externa del condominio.
- Filtrar vehículos según el usuario autenticado.
- Permitir que propietarios o inquilinos vean sus propios vehículos.
- Permitir que administración o seguridad vea todos los vehículos del tenant.

### Diseño importante

Este módulo combina dos fuentes de datos:

1. Persistencia local para reglas internas, auditoría y operaciones propias.
2. Consulta remota a `/api/vehiculos` del sistema externo del condominio.

El servicio resuelve ambos escenarios mediante DTOs diferenciados para la respuesta externa y la respuesta local.

***

## `domain.parking`

Incluye `AccesoVehicular`, `AccesoVehicularController`, `AccesoVehicularRepository`, `AccesoVehicularService` y DTOs como `AccesoResponse`, `RegistroEntradaRequest` y `RegistroSalidaRequest`.

### Objetivo

Controlar el flujo de entrada y salida de vehículos en el estacionamiento.

### Funciones probables

- registrar ingresos,
- registrar salidas,
- validar placa,
- aplicar reglas de acceso,
- dejar trazabilidad del movimiento,
- vincular accesos con método de validación o resultado.

Este módulo es el núcleo operativo del sistema de parking.

***

## `domain.parking_management`

Este paquete contiene `EspacioParking`, `EspacioParkingController`, `EspacioParkingRepository`, `EspacioParkingService` y DTOs como `EspacioRequest`, `EspacioResponse` y `OcupacionResponse`.

### Responsabilidad

Gestionar espacios físicos del estacionamiento:

- alta y edición de espacios,
- estado del espacio,
- tipo de espacio,
- ocupación,
- métricas de disponibilidad.

Este módulo complementa el control vehicular con la administración física del parking.

***

## `domain.rules`

El módulo `rules` contiene `ReglaAcceso`, `ReglaAccesoController`, `ReglaAccesoRepository`, `ReglaAccesoService`, `RuleEngine` y DTOs como `ReglaRequest`, `ReglaResponse`, `ValidacionRequest` y `ValidacionResult`.

### Función

Permite definir y ejecutar reglas de acceso parametrizables por tenant.

### Ejemplos de reglas

- horario permitido de acceso,
- visitantes autorizados,
- roles permitidos,
- límite de vehículos activos,
- otras validaciones configurables.

### Componente central

`RuleEngine` evalúa las reglas activas de un tenant sobre un request de validación y devuelve si el acceso está autorizado o denegado, junto con el motivo correspondiente.

Este diseño hace posible adaptar el comportamiento del sistema a políticas diferentes por condominio sin reescribir lógica central.

***

## `domain.security_operations`

En este módulo se encuentran `Incidente`, `IncidenteController`, `IncidenteRepository`, `IncidenteService`, `ValidacionPlacaController` y DTOs como `IncidenteRequest`, `IncidenteResponse` y `ResolucionRequest`.

### Responsabilidad

Gestionar incidentes operativos y de seguridad relacionados con el estacionamiento.

### Funciones principales

- registrar incidentes,
- consultar estado,
- resolver casos,
- asociar incidentes a sesiones o placas,
- validar placas en escenarios operativos.

Sirve como soporte a la seguridad del condominio y al seguimiento de eventos relevantes.

***

## `domain.notifications`

El paquete contiene `Notificacion`, `NotificacionController`, `NotificacionRepository`, `NotificacionService`, `SseEmitterRegistry` y el DTO `NotificacionResponse`.

### Objetivo

Administrar notificaciones del sistema y mecanismos de emisión en tiempo real.

### Posibles usos

- alertas de acceso,
- aviso de incidentes,
- cambios de estado,
- mensajes al personal de seguridad,
- publicación de eventos por SSE.

`SseEmitterRegistry` sugiere que el sistema soporta Server-Sent Events para actualización en tiempo real hacia clientes conectados.

***

## `domain.reports`

Incluye `ReportController`, `ReportRepository`, `ReportService` y DTOs como:

- `ReporteAccesosDTO`
- `ReporteAccesosPorDiaDTO`
- `ReporteOcupacionDTO`
- `ReporteVehiculoDTO`
- `TopPlacaDTO`

### Responsabilidad

Construir vistas de reporte y analítica operativa.

### Casos de uso

- volumen de accesos por día,
- ocupación del estacionamiento,
- vehículos más frecuentes,
- placas con mayor recurrencia,
- indicadores de operación del condominio.

Este módulo permite transformar los datos operativos en información útil para gestión y toma de decisiones.

## Clase principal

La clase `ParkingApplication` es el punto de arranque del proyecto Spring Boot. Desde ahí se inicializa el contexto de la aplicación, los beans, la configuración y todos los módulos descritos.

## Archivo de configuración

En `src/main/resources/application.properties` se centralizan propiedades como:

- credenciales y conexión a base de datos,
- configuración JWT,
- parámetros de integración,
- puertos,
- logging,
- JPA / Hibernate,
- Swagger u otras opciones del entorno.

## Convenciones arquitectónicas observadas

La estructura del proyecto refleja varias decisiones de diseño importantes:

### 1. Organización por dominio

Cada contexto funcional está encapsulado en su propio paquete con entidades, controladores, servicios, repositorios y DTOs asociados.

### 2. Separación de capas

Se distingue claramente entre:

- Controller: entrada HTTP
- Service: lógica de negocio
- Repository: acceso a datos
- DTO: contratos de entrada y salida
- Entity: persistencia

### 3. Multi-tenancy contextual

El tenant se resuelve por request y condiciona operaciones sobre vehículos, reglas, incidencias y demás recursos del condominio.

### 4. Integración híbrida

El sistema no depende exclusivamente de datos locales. Parte de la información proviene de APIs externas del condominio, especialmente en autenticación y consulta de vehículos.

### 5. Auditoría transversal

La presencia del módulo `audit` y del enum `TipoAccionAudit` muestra que el sistema fue pensado para registrar operaciones sensibles de manera homogénea.

## Flujo funcional resumido

Un flujo típico del sistema puede entenderse así:

1. Un usuario del condominio inicia sesión mediante el módulo `auth`.
2. El backend valida credenciales en la API externa del condominio.
3. Se almacena una sesión local en `integration.UsuarioSesion`.
4. Se genera un JWT propio del backend.
5. El request posterior entra por `security.JwtAuthFilter`.
6. Se resuelve el tenant y el usuario actual.
7. El módulo correspondiente, por ejemplo `vehiculo`, `parking`, `rules` o `security_operations`, ejecuta la lógica dentro del contexto del condominio.
8. La acción se registra en auditoría cuando corresponde.

## Requisitos recomendados para ejecutar el proyecto

Aunque las imágenes no muestran el archivo `pom.xml`, por la estructura puede inferirse un stack típico de Spring Boot con los siguientes componentes:

- Java 17 o superior
- Spring Boot
- Spring Web
- Spring Security
- Spring Data JPA
- Base de datos relacional, probablemente PostgreSQL
- Lombok
- Swagger / springdoc-openapi
- JJWT para manejo de tokens

## Recomendaciones de mantenimiento

### Documentar cada módulo

Sería conveniente complementar esta estructura con documentación interna por paquete, especialmente en módulos con integración externa y reglas dinámicas.

### Diferenciar mejor algunos conceptos de dominio

En componentes como vehículos y reglas, conviene mantener una distinción clara entre datos locales y datos externos para evitar ambigüedades en enums, DTOs y lógica de mapeo.

### Fortalecer pruebas

La estructura es adecuada para incorporar pruebas unitarias y de integración por módulo, sobre todo en:

- `RuleEngine`
- `AuthService`
- `VehiculoService`
- `AccesoVehicularService`
- `IncidenteService`

## Conclusión

UrbanPark Parking Backend presenta una arquitectura bien modularizada, orientada a dominios de negocio concretos y preparada para operar en un entorno multi-tenant con integración externa. La separación entre la capa SaaS y la capa operativa por condominio es uno de los puntos más sólidos del diseño, ya que permite escalar la plataforma sin mezclar responsabilidades.

La estructura observada también sugiere una base bastante flexible para crecer en funcionalidades como sincronización avanzada, reglas más complejas, automatización de accesos, reportes enriquecidos y operación en tiempo real. Bien documentado y acompañado de pruebas por dominio, este proyecto puede evolucionar de forma ordenada y mantenible.