package com.urbanpark.parking.config;

import com.urbanpark.parking.domain.usuarios.UsuarioSaas;
import com.urbanpark.parking.domain.usuarios.UsuarioSaasRepository;
import com.urbanpark.parking.shared.enums.EstadoUsuarioSaas;
import com.urbanpark.parking.shared.enums.OrigenRegistro;
import com.urbanpark.parking.shared.enums.RolSaas;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioSaasRepository usuarioSaasRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (usuarioSaasRepository.existsByEmail("super@urbanpark.com")) {
            log.info("SUPERADMIN base ya existe, omitiendo inicialización");
            return;
        }

        UsuarioSaas superAdmin = UsuarioSaas.builder()
                .email("superadmin@urbanpark.com")
                .passwordHash(passwordEncoder.encode("SuperAdmin"))
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
        log.info("SUPERADMIN base creado: superadmin@urbanpark.com");
    }
}