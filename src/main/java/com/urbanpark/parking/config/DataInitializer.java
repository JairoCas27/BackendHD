package com.urbanpark.parking.config;

import com.urbanpark.parking.domain.saas.plan.Plan;
import com.urbanpark.parking.domain.saas.plan.PlanRepository;
import com.urbanpark.parking.domain.saas.user.SaasUser;
import com.urbanpark.parking.domain.saas.user.SaasUserRepository;
import com.urbanpark.parking.domain.tenant.Condominio;
import com.urbanpark.parking.domain.tenant.CondominioRepository;
import com.urbanpark.parking.shared.enums.EstadoCondominio;
import com.urbanpark.parking.shared.enums.EstadoPlan;
import com.urbanpark.parking.shared.enums.RolSaas;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SaasUserRepository saasUserRepository;
    private final PlanRepository planRepository;
    private final CondominioRepository condominioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        crearSuperAdmin();
        crearAdminEjemplo();
        crearPlanes();
        crearCondominios();
    }

    // ─── USUARIOS ────────────────────────────────────────────────────

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
                .nombre(nombre)
                .descripcion(descripcion)
                .precio(precio)
                .maxEspacios(maxEspacios)
                .maxUsuarios(maxUsuarios)
                .estado(estado)
                .build());
        log.info("Plan creado: {}", nombre);
    }

    // ─── CONDOMINIOS ─────────────────────────────────────────────────

    private void crearCondominios() {
        crearCondominio(
                "Residencial Las Magnolias",
                "https://sistemagestioncondominios-backend.onrender.com",
                "Roberto Salas Fuentes",
                "rsalas@lasmagnolias.pe",
                "+51 998 112 233",
                "Estandar"
        );

        crearCondominio(
                "Torres del Sol Miraflores",
                "https://sistemagestioncondominios-backend.onrender.com",
                "Patricia Quispe Huanca",
                "pquispe@torressol.pe",
                "+51 955 443 221",
                "Profesional"
        );

        crearCondominio(
                "Condominio Vista Verde",
                "https://sistemagestioncondominios-backend.onrender.com",
                "Jorge Mamani Ccopa",
                "jmamani@vistaverde.pe",
                "+51 941 887 766",
                "Basico"
        );
    }

    private void crearCondominio(String nombre, String apiBaseUrl,
                                 String titularNombre, String titularEmail,
                                 String titularTelefono, String planNombre) {
        if (condominioRepository.existsByTitularEmail(titularEmail)) {
            log.info("Condominio '{}' ya existe, omitiendo.", nombre);
            return;
        }

        Plan plan = planRepository.findAll().stream()
                .filter(p -> p.getNombre().equals(planNombre))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Plan no encontrado: " + planNombre));

        condominioRepository.save(Condominio.builder()
                .nombre(nombre)
                .apiBaseUrl(apiBaseUrl)
                .titularNombre(titularNombre)
                .titularEmail(titularEmail)
                .titularTelefono(titularTelefono)
                .estado(EstadoCondominio.ACTIVO)
                .plan(plan)
                .build());

        if (!saasUserRepository.existsByEmail(titularEmail)) {
            String nombreSinEspacios = titularNombre.replaceAll("\\s+", "");
            String prefijo = nombreSinEspacios.substring(0, Math.min(4, nombreSinEspacios.length()));
            String password = "Urban" + prefijo + "@" + java.time.LocalDate.now().getYear();

            saasUserRepository.save(SaasUser.builder()
                    .email(titularEmail)
                    .password(passwordEncoder.encode(password))
                    .nombre(titularNombre)
                    .telefono(titularTelefono)
                    .cargo("Titular de Condominio")
                    .rol(RolSaas.CLIENTE)
                    .activo(true)
                    .esBase(false)
                    .build());

            log.info("Condominio + CLIENTE creado: {} | pwd: {}", titularEmail, password);
        }
    }
}