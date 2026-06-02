package com.urbanpark.parking.config;

import com.urbanpark.parking.domain.audit.AuditLog;
import com.urbanpark.parking.domain.audit.AuditLogRepository;
import com.urbanpark.parking.domain.integration.UsuarioSesion;
import com.urbanpark.parking.domain.integration.UsuarioSesionRepository;
import com.urbanpark.parking.domain.rules.ReglaAcceso;
import com.urbanpark.parking.domain.rules.ReglaAccesoRepository;
import com.urbanpark.parking.domain.saas.plan.Plan;
import com.urbanpark.parking.domain.saas.plan.PlanRepository;
import com.urbanpark.parking.domain.saas.user.SaasUser;
import com.urbanpark.parking.domain.saas.user.SaasUserRepository;
import com.urbanpark.parking.domain.security_operations.Incidente;
import com.urbanpark.parking.domain.security_operations.IncidenteRepository;
import com.urbanpark.parking.domain.tenant.Condominio;
import com.urbanpark.parking.domain.tenant.CondominioRepository;
import com.urbanpark.parking.shared.enums.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SaasUserRepository saasUserRepository;
    private final PlanRepository planRepository;
    private final CondominioRepository condominioRepository;
    private final UsuarioSesionRepository usuarioSesionRepository;
    private final IncidenteRepository incidenteRepository;
    private final AuditLogRepository auditLogRepository;
    private final ReglaAccesoRepository reglaAccesoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        crearSuperAdmin();
        crearAdminEjemplo();
        crearPlanes();
        crearCondominios();
        crearSesionesEjemplo();
        crearReglasEjemplo();       // ← antes de incidentes y logs
        crearIncidentesEjemplo();
        crearAuditLogsEjemplo();
    }

    // ─── USUARIOS SAAS ───────────────────────────────────────────────

    private void crearSuperAdmin() {
        String email = "superadmin@urbanpark.pe";
        if (saasUserRepository.existsByEmail(email)) {
            log.info("SUPERADMIN base ya existe, omitiendo.");
            return;
        }
        saasUserRepository.save(SaasUser.builder()
                .email(email)
                .password(passwordEncoder.encode("UrbanPark"))
                .nombre("Carlos Mendoza Rios")
                .dni("45678901")
                .telefono("+51 987 654 321")
                .cargo("CEO & Fundador")
                .rol(RolSaas.SUPERADMIN)
                .activo(true)
                .esBase(true)
                .build());
        log.info("SUPERADMIN base creado: {}", email);
    }

    private void crearAdminEjemplo() {
        String email = "admin@urbanpark.pe";
        if (saasUserRepository.existsByEmail(email)) {
            log.info("ADMIN ejemplo ya existe, omitiendo.");
            return;
        }
        saasUserRepository.save(SaasUser.builder()
                .email(email)
                .password(passwordEncoder.encode("UrbanPark"))
                .nombre("Lucia Torres Vega")
                .dni("72345678")
                .telefono("+51 912 345 678")
                .cargo("Administradora de Operaciones")
                .rol(RolSaas.ADMIN)
                .activo(true)
                .esBase(false)
                .build());
        log.info("ADMIN ejemplo creado: {}", email);
    }

    // ─── PLANES ──────────────────────────────────────────────────────

    private void crearPlanes() {
        crearPlan("Basico",
                "Ideal para estacionamientos pequenos que estan comenzando. " +
                        "Incluye gestion de espacios, reportes basicos y soporte por email.",
                new BigDecimal("99.00"), 50, 2, EstadoPlan.ACTIVO);

        crearPlan("Estandar",
                "Para negocios en crecimiento. Gestion avanzada de espacios, " +
                        "reportes en tiempo real, integracion de pagos y soporte prioritario.",
                new BigDecimal("249.00"), 200, 5, EstadoPlan.ACTIVO);

        crearPlan("Profesional",
                "Solucion completa para operadores medianos. Multiples sedes, " +
                        "API de integracion, analytics avanzados y soporte 24/7.",
                new BigDecimal("549.00"), 500, 15, EstadoPlan.ACTIVO);

        crearPlan("Empresarial",
                "Para grandes operadores y cadenas. Espacios ilimitados, " +
                        "white-label, SLA garantizado, gestor de cuenta dedicado y auditoria completa.",
                new BigDecimal("1199.00"), 9999, 50, EstadoPlan.ACTIVO);

        crearPlan("Prueba Gratuita",
                "30 dias gratis para explorar la plataforma sin compromiso. " +
                        "Funcionalidades del plan Basico con limite de tiempo.",
                new BigDecimal("0.00"), 10, 1, EstadoPlan.ACTIVO);

        crearPlan("Legacy 2023",
                "Plan descontinuado para clientes anteriores a 2024. " +
                        "No disponible para nuevas contrataciones.",
                new BigDecimal("79.00"), 30, 2, EstadoPlan.INACTIVO);
    }

    private void crearPlan(String nombre, String descripcion, BigDecimal precio,
                           int maxEspacios, int maxUsuarios, EstadoPlan estado) {
        if (planRepository.existsByNombre(nombre)) {
            log.info("Plan '{}' ya existe, omitiendo.", nombre);
            return;
        }
        planRepository.save(Plan.builder()
                .nombre(nombre).descripcion(descripcion).precio(precio)
                .maxEspacios(maxEspacios).maxUsuarios(maxUsuarios).estado(estado)
                .build());
        log.info("Plan creado: {}", nombre);
    }

    // ─── CONDOMINIOS ─────────────────────────────────────────────────

    private void crearCondominios() {
        crearCondominio("Residencial Las Magnolias",
                "https://sistemagestioncondominios-backend.onrender.com",
                "Roberto Salas Fuentes", "45112233",
                "rsalas@lasmagnolias.pe", "+51 998 112 233", "Estandar");

        crearCondominio("Torres del Sol Miraflores",
                "https://sistemagestioncondominios-backend.onrender.com",
                "Patricia Quispe Huanca", "72556677",
                "pquispe@torressol.pe", "+51 955 443 221", "Profesional");

        crearCondominio("Condominio Vista Verde",
                "https://sistemagestioncondominios-backend.onrender.com",
                "Jorge Mamani Ccopa", "61998877",
                "jmamani@vistaverde.pe", "+51 941 887 766", "Basico");
    }

    private void crearCondominio(String nombre, String apiBaseUrl,
                                 String titularNombre, String titularDni,
                                 String titularEmail, String titularTelefono,
                                 String planNombre) {
        if (condominioRepository.existsByTitularEmail(titularEmail)) {
            log.info("Condominio '{}' ya existe, omitiendo.", nombre);
            return;
        }

        Plan plan = planRepository.findAll().stream()
                .filter(p -> p.getNombre().equals(planNombre)).findFirst()
                .orElseThrow(() -> new IllegalStateException("Plan no encontrado: " + planNombre));

        condominioRepository.save(Condominio.builder()
                .nombre(nombre).apiBaseUrl(apiBaseUrl)
                .titularNombre(titularNombre).titularDni(titularDni)
                .titularEmail(titularEmail).titularTelefono(titularTelefono)
                .estado(EstadoCondominio.ACTIVO).plan(plan)
                .build());

        if (!saasUserRepository.existsByEmail(titularEmail)) {
            String sinEspacios = titularNombre.replaceAll("\\s+", "");
            String prefijo = sinEspacios.substring(0, Math.min(4, sinEspacios.length()));
            String password = "Urban" + prefijo + "@" + java.time.LocalDate.now().getYear();

            saasUserRepository.save(SaasUser.builder()
                    .email(titularEmail).password(passwordEncoder.encode(password))
                    .nombre(titularNombre).dni(titularDni).telefono(titularTelefono)
                    .cargo("Titular de Condominio").rol(RolSaas.CLIENTE)
                    .activo(true).esBase(false)
                    .build());

            log.info("CLIENTE creado: {} | pwd: {}", titularEmail, password);
        }
    }

    // ─── SESIONES DE EJEMPLO ─────────────────────────────────────────

    private void crearSesionesEjemplo() {
        if (usuarioSesionRepository.count() > 0) {
            log.info("Sesiones ya existen, omitiendo.");
            return;
        }

        List<Condominio> condominios = condominioRepository.findAll();
        if (condominios.isEmpty()) {
            log.warn("No hay condominios, omitiendo sesiones.");
            return;
        }

        UUID tenantId = condominios.get(0).getId();

        usuarioSesionRepository.save(UsuarioSesion.builder()
                .externalUserId(101L).condominioId(tenantId)
                .email("agente.ramirez@lasmagnolias.pe")
                .nombre("Luis Ramirez Flores").rol("AGENTE_SEGURIDAD")
                .build());

        usuarioSesionRepository.save(UsuarioSesion.builder()
                .externalUserId(202L).condominioId(tenantId)
                .email("propietario.gutierrez@lasmagnolias.pe")
                .nombre("Ana Gutierrez Paz").rol("PROPIETARIO")
                .build());

        usuarioSesionRepository.save(UsuarioSesion.builder()
                .externalUserId(303L).condominioId(tenantId)
                .email("admin.condominio@lasmagnolias.pe")
                .nombre("Miguel Paredes Soto").rol("ADMIN_CONDOMINIO")
                .build());

        log.info("Sesiones de ejemplo creadas para tenant: {}", tenantId);
    }

    // ─── REGLAS DE EJEMPLO ───────────────────────────────────────────

    private void crearReglasEjemplo() {
        if (reglaAccesoRepository.count() > 0) {
            log.info("Reglas ya existen, omitiendo.");
            return;
        }

        List<Condominio> condominios = condominioRepository.findAll();
        if (condominios.isEmpty()) {
            log.warn("No hay condominios, omitiendo reglas.");
            return;
        }

        UUID tenantId = condominios.get(0).getId();

        // Regla 1 — Horario de acceso: solo entre 06:00 y 22:00
        reglaAccesoRepository.save(ReglaAcceso.builder()
                .tenantId(tenantId)
                .nombre("Horario permitido de acceso")
                .tipo(TipoRegla.HORARIO_ACCESO)
                .configuracion(Map.of(
                        "horaInicio", "06:00",
                        "horaFin", "22:00"
                ))
                .activo(true)
                .build());

        // Regla 2 — Horario nocturno desactivado (inactivo, para mostrar en lista)
        reglaAccesoRepository.save(ReglaAcceso.builder()
                .tenantId(tenantId)
                .nombre("Acceso 24 horas (desactivado)")
                .tipo(TipoRegla.HORARIO_ACCESO)
                .configuracion(Map.of(
                        "horaInicio", "00:00",
                        "horaFin", "23:59"
                ))
                .activo(false)
                .build());

        // Regla 3 — Roles permitidos: propietario, residente y seguridad
        reglaAccesoRepository.save(ReglaAcceso.builder()
                .tenantId(tenantId)
                .nombre("Roles con acceso vehicular")
                .tipo(TipoRegla.TIPO_USUARIO)
                .configuracion(Map.of(
                        "rolesPermitidos", List.of(
                                "PROPIETARIO",
                                "RESIDENTE",
                                "AGENTE_SEGURIDAD",
                                "ADMIN_CONDOMINIO"
                        )
                ))
                .activo(true)
                .build());

        // Regla 4 — Visitantes permitidos solo con autorización previa
        reglaAccesoRepository.save(ReglaAcceso.builder()
                .tenantId(tenantId)
                .nombre("Visitantes requieren autorizacion previa")
                .tipo(TipoRegla.VISITANTE_PERMITIDO)
                .configuracion(Map.of(
                        "descripcion", "Solo visitantes con registro activo pueden ingresar"
                ))
                .activo(true)
                .build());

        // Regla 5 — Límite de 80 vehículos activos en el parking
        reglaAccesoRepository.save(ReglaAcceso.builder()
                .tenantId(tenantId)
                .nombre("Limite de capacidad del estacionamiento")
                .tipo(TipoRegla.LIMITE_VEHICULOS)
                .configuracion(Map.of(
                        "limiteMaximo", 80
                ))
                .activo(true)
                .build());

        // Regla 6 — Límite reducido para eventos (inactivo por defecto)
        reglaAccesoRepository.save(ReglaAcceso.builder()
                .tenantId(tenantId)
                .nombre("Limite reducido para eventos")
                .tipo(TipoRegla.LIMITE_VEHICULOS)
                .configuracion(Map.of(
                        "limiteMaximo", 40
                ))
                .activo(false)
                .build());

        log.info("6 reglas de ejemplo creadas para tenant: {}", tenantId);
    }

    // ─── INCIDENTES DE EJEMPLO ───────────────────────────────────────

    private void crearIncidentesEjemplo() {
        if (incidenteRepository.count() > 0) {
            log.info("Incidentes ya existen, omitiendo.");
            return;
        }

        List<UsuarioSesion> sesiones = usuarioSesionRepository.findAll();
        if (sesiones.isEmpty()) {
            log.warn("No hay sesiones, omitiendo incidentes.");
            return;
        }

        UsuarioSesion sesionAgente = sesiones.stream()
                .filter(s -> "AGENTE_SEGURIDAD".equals(s.getRol()))
                .findFirst().orElse(sesiones.get(0));

        UsuarioSesion sesionPropietario = sesiones.stream()
                .filter(s -> "PROPIETARIO".equals(s.getRol()))
                .findFirst().orElse(sesiones.get(0));

        UUID tenantId = sesionAgente.getCondominioId();

        incidenteRepository.save(Incidente.builder()
                .tenantId(tenantId).sesionId(sesionAgente.getId())
                .descripcion("Vehiculo sin placa intento ingresar por la rampa norte de manera forzada")
                .nivel(NivelIncidente.CRITICO).estado(EstadoIncidente.ABIERTO)
                .build());

        incidenteRepository.save(Incidente.builder()
                .tenantId(tenantId).sesionId(sesionAgente.getId())
                .descripcion("Se detecto persona sospechosa rondando el nivel B2 durante la madrugada")
                .nivel(NivelIncidente.ALTO).estado(EstadoIncidente.EN_REVISION)
                .build());

        incidenteRepository.save(Incidente.builder()
                .tenantId(tenantId).sesionId(sesionPropietario.getId())
                .descripcion("Mi vehiculo amaneció con rayones en la puerta del copiloto. Espacio C-08")
                .nivel(NivelIncidente.MEDIO).estado(EstadoIncidente.EN_REVISION)
                .placaInvolucrada("ABC-123")
                .build());

        incidenteRepository.save(Incidente.builder()
                .tenantId(tenantId).sesionId(sesionPropietario.getId())
                .descripcion("Fuga menor de aceite detectada en espacio B-14, mancha en el piso")
                .nivel(NivelIncidente.BAJO).estado(EstadoIncidente.RESUELTO)
                .placaInvolucrada("XYZ-789")
                .resolucion("Se limpio el area y se notifico al propietario via correo")
                .resueltoAt(LocalDateTime.now().minusHours(3))
                .build());

        incidenteRepository.save(Incidente.builder()
                .tenantId(tenantId).sesionId(sesionAgente.getId())
                .descripcion("Alarma del vehiculo activada por mas de 20 minutos sin respuesta del propietario")
                .nivel(NivelIncidente.MEDIO).estado(EstadoIncidente.RESUELTO)
                .placaInvolucrada("DEF-456")
                .resolucion("Se contacto al propietario, desactivo la alarma de forma remota.")
                .resueltoAt(LocalDateTime.now().minusDays(1))
                .build());

        log.info("5 incidentes de ejemplo creados para tenant: {}", tenantId);
    }

    // ─── AUDIT LOGS DE EJEMPLO ───────────────────────────────────────

    private void crearAuditLogsEjemplo() {
        if (auditLogRepository.count() > 0) {
            log.info("Audit logs ya existen, omitiendo.");
            return;
        }

        List<UsuarioSesion> sesiones = usuarioSesionRepository.findAll();
        if (sesiones.isEmpty()) {
            log.warn("No hay sesiones, omitiendo audit logs.");
            return;
        }

        UsuarioSesion sesionAgente = sesiones.stream()
                .filter(s -> "AGENTE_SEGURIDAD".equals(s.getRol()))
                .findFirst().orElse(sesiones.get(0));

        UsuarioSesion sesionPropietario = sesiones.stream()
                .filter(s -> "PROPIETARIO".equals(s.getRol()))
                .findFirst().orElse(sesiones.get(0));

        UsuarioSesion sesionAdmin = sesiones.stream()
                .filter(s -> "ADMIN_CONDOMINIO".equals(s.getRol()))
                .findFirst().orElse(sesiones.get(0));

        UUID tenantId = sesionAgente.getCondominioId();
        List<Incidente> incidentes = incidenteRepository.findAll();
        List<ReglaAcceso> reglas = reglaAccesoRepository.findAll();

        // 1 — Login del agente
        auditLogRepository.save(AuditLog.builder()
                .tenantId(tenantId).usuarioId(sesionAgente.getId())
                .accion(TipoAccionAudit.LOGIN).entidad("UsuarioSesion")
                .entidadId(sesionAgente.getId().toString())
                .detalle(Map.of("email", sesionAgente.getEmail(),
                        "rol", sesionAgente.getRol(),
                        "condominio", "Residencial Las Magnolias"))
                .build());

        // 2 — Login del propietario
        auditLogRepository.save(AuditLog.builder()
                .tenantId(tenantId).usuarioId(sesionPropietario.getId())
                .accion(TipoAccionAudit.LOGIN).entidad("UsuarioSesion")
                .entidadId(sesionPropietario.getId().toString())
                .detalle(Map.of("email", sesionPropietario.getEmail(),
                        "rol", sesionPropietario.getRol(),
                        "condominio", "Residencial Las Magnolias"))
                .build());

        // 3 — Login fallido
        auditLogRepository.save(AuditLog.builder()
                .tenantId(tenantId).usuarioId(null)
                .accion(TipoAccionAudit.LOGIN_FALLIDO).entidad("UsuarioSesion")
                .entidadId(null)
                .detalle(Map.of("email", "intruso@hack.com",
                        "motivo", "Credenciales invalidas",
                        "condominio", "Residencial Las Magnolias"))
                .build());

        // 4 — Incidente reportado por agente
        if (!incidentes.isEmpty()) {
            Incidente inc1 = incidentes.get(0);
            auditLogRepository.save(AuditLog.builder()
                    .tenantId(tenantId).usuarioId(sesionAgente.getId())
                    .accion(TipoAccionAudit.INCIDENTE_REPORTADO).entidad("Incidente")
                    .entidadId(inc1.getId().toString())
                    .detalle(Map.of("nivel", inc1.getNivel().name(),
                            "descripcion", inc1.getDescripcion(),
                            "reportadoPor", sesionAgente.getNombre(),
                            "rol", sesionAgente.getRol(), "placa", "N/A"))
                    .build());
        }

        // 5 — Incidente reportado por propietario
        if (incidentes.size() >= 3) {
            Incidente inc3 = incidentes.get(2);
            auditLogRepository.save(AuditLog.builder()
                    .tenantId(tenantId).usuarioId(sesionPropietario.getId())
                    .accion(TipoAccionAudit.INCIDENTE_REPORTADO).entidad("Incidente")
                    .entidadId(inc3.getId().toString())
                    .detalle(Map.of("nivel", inc3.getNivel().name(),
                            "descripcion", inc3.getDescripcion(),
                            "reportadoPor", sesionPropietario.getNombre(),
                            "rol", sesionPropietario.getRol(),
                            "placa", inc3.getPlacaInvolucrada() != null
                                    ? inc3.getPlacaInvolucrada() : "N/A"))
                    .build());
        }

        // 6 — Incidente resuelto por admin
        if (incidentes.size() >= 4) {
            Incidente inc4 = incidentes.get(3);
            auditLogRepository.save(AuditLog.builder()
                    .tenantId(tenantId).usuarioId(sesionAdmin.getId())
                    .accion(TipoAccionAudit.INCIDENTE_RESUELTO).entidad("Incidente")
                    .entidadId(inc4.getId().toString())
                    .detalle(Map.of(
                            "resolucion", inc4.getResolucion() != null
                                    ? inc4.getResolucion() : "Sin detalle",
                            "resolvedBy", sesionAdmin.getNombre()))
                    .build());
        }

        // 7 — Entrada de vehiculo
        auditLogRepository.save(AuditLog.builder()
                .tenantId(tenantId).usuarioId(sesionAgente.getId())
                .accion(TipoAccionAudit.ENTRADA_VEHICULO).entidad("Acceso")
                .entidadId("seed-acceso-001")
                .detalle(Map.of("placa", "ABC-123", "tipo", "RESIDENTE",
                        "espacio", "A-05", "registradoPor", sesionAgente.getNombre()))
                .build());

        // 8 — Salida de vehiculo
        auditLogRepository.save(AuditLog.builder()
                .tenantId(tenantId).usuarioId(sesionAgente.getId())
                .accion(TipoAccionAudit.SALIDA_VEHICULO).entidad("Acceso")
                .entidadId("seed-acceso-001")
                .detalle(Map.of("placa", "ABC-123", "duracionMinutos", 45,
                        "registradoPor", sesionAgente.getNombre()))
                .build());

        // 9 — Acceso denegado
        auditLogRepository.save(AuditLog.builder()
                .tenantId(tenantId).usuarioId(sesionAgente.getId())
                .accion(TipoAccionAudit.ACCESO_DENEGADO).entidad("Acceso")
                .entidadId("seed-acceso-002")
                .detalle(Map.of("placa", "ZZZ-999",
                        "motivo", "Vehiculo no registrado en el condominio",
                        "registradoPor", sesionAgente.getNombre()))
                .build());

        // 10 — Usuario activado por admin
        auditLogRepository.save(AuditLog.builder()
                .tenantId(tenantId).usuarioId(sesionAdmin.getId())
                .accion(TipoAccionAudit.USUARIO_ACTIVADO).entidad("UsuarioSesion")
                .entidadId(sesionPropietario.getId().toString())
                .detalle(Map.of("email", sesionPropietario.getEmail(),
                        "activadoPor", sesionAdmin.getNombre()))
                .build());

        // 11 — Regla creada por admin (horario)
        if (!reglas.isEmpty()) {
            ReglaAcceso regla1 = reglas.get(0);
            auditLogRepository.save(AuditLog.builder()
                    .tenantId(tenantId).usuarioId(sesionAdmin.getId())
                    .accion(TipoAccionAudit.REGLA_CREADA).entidad("ReglaAcceso")
                    .entidadId(regla1.getId().toString())
                    .detalle(Map.of("nombre", regla1.getNombre(),
                            "tipo", regla1.getTipo().name(),
                            "creadoPor", sesionAdmin.getNombre()))
                    .build());
        }

        // 12 — Regla desactivada por admin
        if (reglas.size() >= 2) {
            ReglaAcceso regla2 = reglas.get(1);
            auditLogRepository.save(AuditLog.builder()
                    .tenantId(tenantId).usuarioId(sesionAdmin.getId())
                    .accion(TipoAccionAudit.REGLA_DESACTIVADA).entidad("ReglaAcceso")
                    .entidadId(regla2.getId().toString())
                    .detalle(Map.of("nombre", regla2.getNombre(),
                            "desactivadoPor", sesionAdmin.getNombre()))
                    .build());
        }

        log.info("12 audit logs de ejemplo creados para tenant: {}", tenantId);
    }
}