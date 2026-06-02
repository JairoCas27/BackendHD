package com.urbanpark.parking.domain.auth;

import com.urbanpark.parking.domain.auth.dto.AuthResponse;
import com.urbanpark.parking.domain.auth.dto.ExternalLoginRequest;
import com.urbanpark.parking.domain.auth.dto.UsuarioExternoDTO;
import com.urbanpark.parking.domain.tenant.Condominio;
import com.urbanpark.parking.domain.tenant.CondominioRepository;
import com.urbanpark.parking.domain.users.usuario.UsuarioCondominio;
import com.urbanpark.parking.domain.users.usuario.UsuarioCondominioRepository;
import com.urbanpark.parking.security.JwtService;
import com.urbanpark.parking.shared.enums.EstadoCondominio;
import com.urbanpark.parking.shared.enums.RolParking;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ExternalTokenValidator externalTokenValidator;
    private final CondominioRepository condominioRepository;
    private final UsuarioCondominioRepository usuarioCondominioRepository;
    private final JwtService jwtService;

    public AuthResponse login(ExternalLoginRequest request) {

        // 1. Buscar el condominio por tenantId
        Condominio condominio = condominioRepository.findById(request.getTenantId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Condominio no encontrado"));

        // 2. Verificar que el condominio está activo
        if (condominio.getEstado() != EstadoCondominio.ACTIVO) {
            throw new IllegalStateException(
                    "El condominio está inactivo o suspendido");
        }

        // 3. Login en el condominio → /me → datos del usuario externo
        // Si las credenciales son incorrectas, ExternalTokenValidator lanza excepción
        UsuarioExternoDTO usuarioExterno =
                externalTokenValidator.validate(request, condominio);

        // 4. Buscar o crear usuario interno del SaaS
        UsuarioCondominio usuario = usuarioCondominioRepository
                .findByExternalIdAndTenantId(
                        String.valueOf(usuarioExterno.getId()),
                        condominio.getId()
                )
                .orElseGet(() -> crearUsuarioInterno(usuarioExterno, condominio));

        // 5. Actualizar datos con la info más reciente del condominio
        usuario.setNombre(usuarioExterno.getNombres() + " " + usuarioExterno.getApellidos());
        usuario.setEmail(usuarioExterno.getCorreo());
        usuario.setRolParking(mapearRol(usuarioExterno.getRol()));
        usuario.setSyncedAt(LocalDateTime.now());
        usuarioCondominioRepository.save(usuario);

        // 6. Generar JWT interno del SaaS
        String accessToken = jwtService.generateToken(
                usuario.getId(),
                condominio.getId(),
                usuario.getRolParking().name()
        );

        String refreshToken = jwtService.generateRefreshToken(usuario.getId());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .rol(usuario.getRolParking().name())
                .tenantId(condominio.getId())
                .userId(usuario.getId())
                .nombre(usuarioExterno.getNombres() + " " + usuarioExterno.getApellidos())
                .build();
    }

    private UsuarioCondominio crearUsuarioInterno(
            UsuarioExternoDTO dto, Condominio condominio) {

        UsuarioCondominio nuevo = UsuarioCondominio.builder()
                .externalId(String.valueOf(dto.getId()))
                .tenantId(condominio.getId())
                .nombre(dto.getNombres() + " " + dto.getApellidos())
                .email(dto.getCorreo())
                .rolParking(mapearRol(dto.getRol()))
                .activo(true)
                .syncedAt(LocalDateTime.now())
                .build();

        return usuarioCondominioRepository.save(nuevo);
    }

    private RolParking mapearRol(String rolExterno) {
        if (rolExterno == null) return RolParking.PROPIETARIO;

        return switch (rolExterno.toUpperCase()) {
            case "ADMINISTRADOR_CONDOMINIO" -> RolParking.ADMIN_CONDOMINIO;
            case "AGENTE_SEGURIDAD",
                 "SEGURIDAD",
                 "VIGILANTE"               -> RolParking.AGENTE_SEGURIDAD;
            default                        -> RolParking.PROPIETARIO;
        };
    }
}