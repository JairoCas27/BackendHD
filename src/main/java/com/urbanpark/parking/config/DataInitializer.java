package com.urbanpark.parking.config;

import com.urbanpark.parking.domain.condominios.Condominio;
import com.urbanpark.parking.domain.condominios.CondominioRepository;
import com.urbanpark.parking.domain.planes.Plan;
import com.urbanpark.parking.domain.planes.PlanRepository;
import com.urbanpark.parking.domain.rules.ReglaAcceso;
import com.urbanpark.parking.domain.rules.ReglaAccesoRepository;
import com.urbanpark.parking.domain.solicitudes.SolicitudPlan;
import com.urbanpark.parking.domain.solicitudes.SolicitudPlanRepository;
import com.urbanpark.parking.domain.titulares.Titular;
import com.urbanpark.parking.domain.titulares.TitularRepository;
import com.urbanpark.parking.domain.usuarios.UsuarioSaas;
import com.urbanpark.parking.domain.usuarios.UsuarioSaasRepository;
import com.urbanpark.parking.shared.enums.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

        private final UsuarioSaasRepository usuarioSaasRepository;
        private final TitularRepository titularRepository;
        private final PlanRepository planRepository;
        private final CondominioRepository condominioRepository;
        private final SolicitudPlanRepository solicitudPlanRepository;
        private final ReglaAccesoRepository reglaAccesoRepository; // Repositorio de reglas inyectado
        private final PasswordEncoder passwordEncoder;

        @Override
        @Transactional
        public void run(String... args) {
                if (usuarioSaasRepository.existsByEmail("superadmin@urbanpark.com")) {
                        log.info("Data ya inicializada, omitiendo...");
                        return;
                }

                UsuarioSaas superAdmin = crearSuperAdmin();
                UsuarioSaas admin1 = crearAdmin1(superAdmin);
                UsuarioSaas admin2 = crearAdmin2(superAdmin);

                Plan basico = crearPlan("Básico", "1 condominio incluido", LimiteCondominios.UNO,
                                new BigDecimal("99.00"));
                Plan pro = crearPlan("Pro", "Hasta 3 condominios", LimiteCondominios.TRES, new BigDecimal("249.00"));
                Plan enterprise = crearPlan("Enterprise", "Condominios ilimitados", LimiteCondominios.ILIMITADO,
                                new BigDecimal("599.00"));

                crearCliente1(admin1, basico);
                crearCliente2(admin1, pro);
                crearCliente3(admin2, enterprise);
                crearCliente4(admin2, basico);

                log.info("Inicialización completa");
        }

        private UsuarioSaas crearSuperAdmin() {
                UsuarioSaas u = UsuarioSaas.builder()
                                .email("superadmin@urbanpark.com")
                                .passwordHash(passwordEncoder.encode("superadmin"))
                                .nombres("Carlos").apellidos("Mendoza")
                                .dni("00000001").telefono("999000001")
                                .rol(RolSaas.SUPERADMIN)
                                .estado(EstadoUsuarioSaas.ACTIVO)
                                .origenRegistro(OrigenRegistro.SUPERADMIN)
                                .creadoPor(null).esBaseProtegido(true)
                                .build();
                return usuarioSaasRepository.save(u);
        }

        private UsuarioSaas crearAdmin1(UsuarioSaas superAdmin) {
                UsuarioSaas u = UsuarioSaas.builder()
                                .email("alana.garcia@urbanpark.com")
                                .passwordHash(passwordEncoder.encode("admin123"))
                                .nombres("Alana").apellidos("Garcia Ramirez")
                                .dni("70998232").telefono("904898321")
                                .rol(RolSaas.ADMIN)
                                .estado(EstadoUsuarioSaas.ACTIVO)
                                .origenRegistro(OrigenRegistro.SUPERADMIN)
                                .creadoPor(superAdmin).esBaseProtegido(false)
                                .build();
                return usuarioSaasRepository.save(u);
        }

        private UsuarioSaas crearAdmin2(UsuarioSaas superAdmin) {
                UsuarioSaas u = UsuarioSaas.builder()
                                .email("roberto.salas@urbanpark.com")
                                .passwordHash(passwordEncoder.encode("admin123"))
                                .nombres("Roberto").apellidos("Salas Vega")
                                .dni("70998233").telefono("904898322")
                                .rol(RolSaas.ADMIN)
                                .estado(EstadoUsuarioSaas.ACTIVO)
                                .origenRegistro(OrigenRegistro.SUPERADMIN)
                                .creadoPor(superAdmin).esBaseProtegido(false)
                                .build();
                return usuarioSaasRepository.save(u);
        }

        private Plan crearPlan(String nombre, String desc, LimiteCondominios limite, BigDecimal precio) {
                return planRepository.save(Plan.builder()
                                .nombre(nombre).descripcion(desc)
                                .limiteCondominios(limite)
                                .precio(precio).moneda("PEN")
                                .estado(EstadoPlan.ACTIVO)
                                .build());
        }

        private void crearSolicitudAprobada(Titular titular, Plan plan, UsuarioSaas admin) {
                solicitudPlanRepository.save(SolicitudPlan.builder()
                                .titular(titular)
                                .plan(plan)
                                .estado(EstadoSolicitud.APROBADA)
                                .revisadoPor(admin)
                                .fechaRevision(LocalDateTime.now())
                                .build());
        }

        private void crearCliente1(UsuarioSaas admin, Plan plan) {
                UsuarioSaas cliente = usuarioSaasRepository.save(UsuarioSaas.builder()
                                .email("juan404999+1@gmail.com")
                                .passwordHash(passwordEncoder.encode("Cuenta123"))
                                .nombres("Juan").apellidos("Lopez Perez")
                                .dni("70898332").telefono("902983421")
                                .rol(RolSaas.CLIENTE)
                                .estado(EstadoUsuarioSaas.ACTIVO)
                                .origenRegistro(OrigenRegistro.PUBLICO)
                                .creadoPor(null).esBaseProtegido(false)
                                .build());

                Titular titular = titularRepository.save(Titular.builder()
                                .usuarioSaas(cliente)
                                .razonSocial("Inmobiliaria Pérez SAC")
                                .ruc("20501234567")
                                .direccionFiscal("Av. Javier Prado 123, San Isidro, Lima")
                                .representanteLegal("Juan Carlos Pérez Quispe")
                                .plan(plan)
                                .estadoPlan(EstadoPlan.ACTIVO)
                                .fechaAsignacionPlan(LocalDateTime.now())
                                .build());

                crearSolicitudAprobada(titular, plan, admin);

                // Capturamos el condominio guardado para asociarlo a las reglas
                Condominio condominioAurora = condominioRepository.save(Condominio.builder()
                                .titular(titular)
                                .nombre("Edificio Aurora")
                                .slug("edificio-aurora")
                                .razonSocial("Edificio Aurora SA")
                                .ruc("20601111111")
                                .direccion("Calle Los Pinos 456, Miraflores, Lima")
                                .emailCondominio("contacto@aurora.pe")
                                .telefonoCondominio("014445566")
                                .apiBaseUrl("https://sistemagestioncondominios-backend.onrender.com")
                                .estado(EstadoCondominio.ACTIVO)
                                .verificadoPor(admin)
                                .fechaVerificacion(LocalDateTime.now())
                                .build());

                // =========================================================================
                // INSERCIÓN AUTOMÁTICA DE LAS REGLAS DE ACCESO (RULE ENGINE)
                // =========================================================================
                log.info(">> Creando reglas de acceso por defecto para Edificio Aurora...");

                // Regla 1: Control Horario Nocturno
                reglaAccesoRepository.save(ReglaAcceso.builder()
                                .condominio(condominioAurora)
                                .tipo(TipoRegla.HORARIO_ACCESO)
                                .nombre("Control de Ingreso Nocturno")
                                .descripcion("Restringe el acceso en horas de la madrugada (Permitido de 06:00 a 23:00)")
                                .configuracion("{\"horaInicio\":\"06:00:00\",\"horaFin\":\"23:00:00\"}")
                                .activa(true)
                                .fechaCreacion(LocalDateTime.now())
                                .build());

                // Regla 2: Límite de Vehículos Activos
                reglaAccesoRepository.save(ReglaAcceso.builder()
                                .condominio(condominioAurora)
                                .tipo(TipoRegla.LIMITE_VEHICULOS)
                                .nombre("Límite de Estacionamiento")
                                .descripcion("Máximo de 5 vehículos permitidos en simultáneo en las zonas comunes")
                                .configuracion("{\"maxVehiculos\":5}")
                                .activa(true)
                                .fechaCreacion(LocalDateTime.now())
                                .build());

                // Regla 3: Filtro por Roles del Usuario
                reglaAccesoRepository.save(ReglaAcceso.builder()
                                .condominio(condominioAurora)
                                .tipo(TipoRegla.TIPO_USUARIO)
                                .nombre("Filtro de Roles Permitidos")
                                .descripcion("Solo se permite el ingreso a usuarios con rol Residente o Administrador")
                                .configuracion("{\"rolesPermitidos\":[\"RESIDENTE\",\"ADMINISTRADOR\"]}")
                                .activa(true)
                                .fechaCreacion(LocalDateTime.now())
                                .build());
        }

        private void crearCliente2(UsuarioSaas admin, Plan plan) {
                UsuarioSaas cliente = usuarioSaasRepository.save(UsuarioSaas.builder()
                                .email("juan404999+2@gmail.com")
                                .passwordHash(passwordEncoder.encode("Cuenta123"))
                                .nombres("María").apellidos("Torres Huanca")
                                .dni("70898333").telefono("902983422")
                                .rol(RolSaas.CLIENTE)
                                .estado(EstadoUsuarioSaas.ACTIVO)
                                .origenRegistro(OrigenRegistro.PUBLICO)
                                .creadoPor(null).esBaseProtegido(false)
                                .build());

                Titular titular = titularRepository.save(Titular.builder()
                                .usuarioSaas(cliente)
                                .razonSocial("Constructora Torres SAC")
                                .ruc("20501234568")
                                .direccionFiscal("Av. Arequipa 789, Miraflores, Lima")
                                .representanteLegal("María Torres Huanca")
                                .plan(plan)
                                .estadoPlan(EstadoPlan.ACTIVO)
                                .fechaAsignacionPlan(LocalDateTime.now())
                                .build());

                crearSolicitudAprobada(titular, plan, admin);

                condominioRepository.save(Condominio.builder()
                                .titular(titular)
                                .nombre("Torre Azul")
                                .slug("torre-azul")
                                .razonSocial("Torre Azul SAC")
                                .ruc("20602222222")
                                .direccion("Av. Arequipa 789, Miraflores, Lima")
                                .emailCondominio("admin@torreazul.pe")
                                .telefonoCondominio("014556677")
                                .apiBaseUrl("https://api.torreazul.pe")
                                .estado(EstadoCondominio.ACTIVO)
                                .verificadoPor(admin)
                                .fechaVerificacion(LocalDateTime.now())
                                .build());

                condominioRepository.save(Condominio.builder()
                                .titular(titular)
                                .nombre("Residencial Las Palmas")
                                .slug("residencial-las-palmas")
                                .razonSocial("Las Palmas SRL")
                                .ruc("20602222223")
                                .direccion("Calle Las Palmas 321, San Borja, Lima")
                                .emailCondominio("info@laspalmas.pe")
                                .telefonoCondominio("014667788")
                                .apiBaseUrl("https://api.laspalmas.pe")
                                .estado(EstadoCondominio.PENDIENTE_VERIFICACION)
                                .build());
        }

        private void crearCliente3(UsuarioSaas admin, Plan plan) {
                UsuarioSaas cliente = usuarioSaasRepository.save(UsuarioSaas.builder()
                                .email("juan404999+3@gmail.com")
                                .passwordHash(passwordEncoder.encode("Cuenta123"))
                                .nombres("Ricardo").apellidos("Vargas Mendoza")
                                .dni("70898334").telefono("902983423")
                                .rol(RolSaas.CLIENTE)
                                .estado(EstadoUsuarioSaas.ACTIVO)
                                .origenRegistro(OrigenRegistro.PUBLICO)
                                .creadoPor(null).esBaseProtegido(false)
                                .build());

                Titular titular = titularRepository.save(Titular.builder()
                                .usuarioSaas(cliente)
                                .razonSocial("Grupo Vargas Inmobiliaria SA")
                                .ruc("20501234569")
                                .direccionFiscal("Calle Schell 230, Miraflores, Lima")
                                .representanteLegal("Ricardo Vargas Mendoza")
                                .plan(plan)
                                .estadoPlan(EstadoPlan.ACTIVO)
                                .fechaAsignacionPlan(LocalDateTime.now())
                                .build());

                crearSolicitudAprobada(titular, plan, admin);

                condominioRepository.save(Condominio.builder()
                                .titular(titular)
                                .nombre("Condominio Los Cedros")
                                .slug("condominio-los-cedros")
                                .razonSocial("Los Cedros SA")
                                .ruc("20603333331")
                                .direccion("Av. Los Cedros 100, Surco, Lima")
                                .emailCondominio("contacto@loscedros.pe")
                                .telefonoCondominio("017778899")
                                .apiBaseUrl("https://api.loscedros.pe")
                                .estado(EstadoCondominio.ACTIVO)
                                .verificadoPor(admin)
                                .fechaVerificacion(LocalDateTime.now())
                                .build());

                condominioRepository.save(Condominio.builder()
                                .titular(titular)
                                .nombre("Parque Residencial Norte")
                                .slug("parque-residencial-norte")
                                .razonSocial("Parque Norte SAC")
                                .ruc("20603333332")
                                .direccion("Av. Universitaria 2500, Los Olivos, Lima")
                                .emailCondominio("info@parquenorte.pe")
                                .telefonoCondominio("015556677")
                                .apiBaseUrl("https://api.parquenorte.pe")
                                .estado(EstadoCondominio.ACTIVO)
                                .verificadoPor(admin)
                                .fechaVerificacion(LocalDateTime.now())
                                .build());

                condominioRepository.save(Condominio.builder()
                                .titular(titular)
                                .nombre("Villa del Sol")
                                .slug("villa-del-sol")
                                .razonSocial("Villa del Sol SRL")
                                .ruc("20603333333")
                                .direccion("Calle del Sol 890, La Molina, Lima")
                                .emailCondominio("contacto@villadelsol.pe")
                                .telefonoCondominio("013334455")
                                .apiBaseUrl("https://api.villadelsol.pe")
                                .estado(EstadoCondominio.PENDIENTE_VERIFICACION)
                                .build());
        }

        private void crearCliente4(UsuarioSaas admin, Plan plan) {
                UsuarioSaas cliente = usuarioSaasRepository.save(UsuarioSaas.builder()
                                .email("juan404999+4@gmail.com")
                                .passwordHash(passwordEncoder.encode("Cuenta123"))
                                .nombres("Sofía").apellidos("Quispe Mamani")
                                .dni("70898335").telefono("902983424")
                                .rol(RolSaas.CLIENTE)
                                .estado(EstadoUsuarioSaas.PENDIENTE_APROBACION)
                                .origenRegistro(OrigenRegistro.PUBLICO)
                                .creadoPor(null).esBaseProtegido(false)
                                .build());

                Titular titular = titularRepository.save(Titular.builder()
                                .usuarioSaas(cliente)
                                .razonSocial("Quispe & Asociados SAC")
                                .ruc("20501234570")
                                .direccionFiscal("Jr. Huancayo 456, Breña, Lima")
                                .representanteLegal("Sofía Quispe Mamani")
                                .plan(null)
                                .estadoPlan(null)
                                .fechaAsignacionPlan(null)
                                .build());

                solicitudPlanRepository.save(SolicitudPlan.builder()
                                .titular(titular)
                                .plan(plan)
                                .estado(EstadoSolicitud.PENDIENTE)
                                .revisadoPor(null)
                                .fechaRevision(null)
                                .build());
        }
}
