package com.urbanpark.parking.config;

import com.urbanpark.parking.domain.condominios.Condominio;
import com.urbanpark.parking.domain.condominios.CondominioRepository;
import com.urbanpark.parking.domain.planes.Plan;
import com.urbanpark.parking.domain.planes.PlanRepository;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioSaasRepository usuarioSaasRepository;
    private final TitularRepository titularRepository;
    private final PlanRepository planRepository;
    private final CondominioRepository condominioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (usuarioSaasRepository.existsByEmail("superadmin@urbanpark.com")) {
            log.info("Data ya inicializada, omitiendo...");
            return;
        }

        crearSuperAdmin();
        crearAdmin();
        Plan planBasico = crearPlanes();
        crearCliente(planBasico);

        log.info("Inicialización completa");
    }

    private void crearSuperAdmin() {
        UsuarioSaas superAdmin = UsuarioSaas.builder()
                .email("superadmin@urbanpark.com")
                .passwordHash(passwordEncoder.encode("superadmin"))
                .nombres("Carlos")
                .apellidos("Mendoza")
                .dni("00000001")
                .telefono("999000001")
                .rol(RolSaas.SUPERADMIN)
                .estado(EstadoUsuarioSaas.ACTIVO)
                .origenRegistro(OrigenRegistro.SUPERADMIN)
                .creadoPor(null)
                .esBaseProtegido(true)
                .build();

        usuarioSaasRepository.save(superAdmin);
        log.info("SUPERADMIN creado: superadmin@urbanpark.com / superadmin");
    }

    private void crearAdmin() {
        UsuarioSaas superAdmin = usuarioSaasRepository.findByEmail("superadmin@urbanpark.com").orElseThrow();

        UsuarioSaas admin = UsuarioSaas.builder()
                .email("admin@urbanpark.com")
                .passwordHash(passwordEncoder.encode("admin123"))
                .nombres("Lucía")
                .apellidos("Torres")
                .dni("00000002")
                .telefono("999000002")
                .rol(RolSaas.ADMIN)
                .estado(EstadoUsuarioSaas.ACTIVO)
                .origenRegistro(OrigenRegistro.SUPERADMIN)
                .creadoPor(superAdmin)
                .esBaseProtegido(false)
                .build();

        usuarioSaasRepository.save(admin);
        log.info("ADMIN creado: admin@urbanpark.com / admin123");
    }

    private Plan crearPlanes() {
        Plan basico = Plan.builder()
                .nombre("Básico")
                .descripcion("1 condominio incluido")
                .limiteCondominios(LimiteCondominios.UNO)
                .precio(new BigDecimal("99.00"))
                .moneda("PEN")
                .estado(EstadoPlan.ACTIVO)
                .build();

        Plan pro = Plan.builder()
                .nombre("Pro")
                .descripcion("Hasta 3 condominios")
                .limiteCondominios(LimiteCondominios.TRES)
                .precio(new BigDecimal("249.00"))
                .moneda("PEN")
                .estado(EstadoPlan.ACTIVO)
                .build();

        Plan enterprise = Plan.builder()
                .nombre("Enterprise")
                .descripcion("Condominios ilimitados")
                .limiteCondominios(LimiteCondominios.ILIMITADO)
                .precio(new BigDecimal("599.00"))
                .moneda("PEN")
                .estado(EstadoPlan.ACTIVO)
                .build();

        planRepository.save(basico);
        planRepository.save(pro);
        planRepository.save(enterprise);
        log.info("Planes creados: Básico / Pro / Enterprise");

        return basico;
    }

    private void crearCliente(Plan plan) {
        UsuarioSaas cliente = UsuarioSaas.builder()
                .email("cliente@urbanpark.com")
                .passwordHash(passwordEncoder.encode("cliente123"))
                .nombres("Juan Carlos")
                .apellidos("Pérez Quispe")
                .dni("00000003")
                .telefono("999000003")
                .rol(RolSaas.CLIENTE)
                .estado(EstadoUsuarioSaas.ACTIVO)
                .origenRegistro(OrigenRegistro.PUBLICO)
                .creadoPor(null)
                .esBaseProtegido(false)
                .build();

        usuarioSaasRepository.save(cliente);

        Titular titular = Titular.builder()
                .usuarioSaas(cliente)
                .razonSocial("Inmobiliaria Pérez SAC")
                .ruc("20501234567")
                .direccionFiscal("Av. Javier Prado 123, San Isidro, Lima")
                .representanteLegal("Juan Carlos Pérez Quispe")
                .plan(plan)
                .estadoPlan(EstadoPlan.ACTIVO)
                .build();

        titularRepository.save(titular);

        Condominio condominio = Condominio.builder()
                .titular(titular)
                .nombre("Edificio Aurora")
                .slug("edificio-aurora")
                .razonSocial("Edificio Aurora SA")
                .ruc("20601111111")
                .direccion("Calle Los Pinos 456, Miraflores, Lima")
                .emailCondominio("contacto@aurora.pe")
                .telefonoCondominio("014445566")
                .apiBaseUrl("https://api.aurora.pe")
                .estado(EstadoCondominio.ACTIVO)
                .build();

        condominioRepository.save(condominio);

        log.info("CLIENTE creado: cliente@urbanpark.com / cliente123");
        log.info("Titular: Inmobiliaria Pérez SAC | Plan: Básico");
        log.info("Condominio: Edificio Aurora → ACTIVO");
    }
}