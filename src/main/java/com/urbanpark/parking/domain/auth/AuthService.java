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
import java.util.UUID;

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
                .orElseThrow(() -> new EntityNotFoundException("Condominio no encontrado"));

        // 2. Verificar que el condominio está activo
        if (condominio.getEstado() != EstadoCondominio.ACTIVO) {
            throw new IllegalArgumentException("El condominio está inactivo o suspendido");
        }

        // 3. Hacer login en el condominio → obtener cookie → llamar /me
        UsuarioExternoDTO usuarioExterno = externalTokenValidator.validate(request, condominio);

        // 4. Verificar que el usuario está activo en el condominio
        if (!usuarioExterno.isActivo()) {
            throw new IllegalArgumentException("Usuario inactivo en el sistema del condominio");
        }

        // 5. Buscar o crear el usuario interno del SaaS
        UsuarioCondominio usuario = usuarioCondominioRepository
                .findByExternalIdAndTenantId(
                        String.valueOf(usuarioExterno.getId()),
                        condominio.getId()
                )
                .orElseGet(() -> crearUsuarioInterno(usuarioExterno, condominio));

        // 6. Actualizar datos y última sincronización
        usuario.setNombre(usuarioExterno.getNombres() + " " + usuarioExterno.getApellidos());
        usuario.setEmail(usuarioExterno.getCorreo());
        usuario.setRolParking(mapearRol(usuarioExterno.getRol()));
        usuario.setSyncedAt(LocalDateTime.now());
        usuarioCondominioRepository.save(usuario);

        // 7. Generar JWT interno del SaaS
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

    private UsuarioCondominio crearUsuarioInterno(UsuarioExternoDTO dto, Condominio condominio) {
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
            case "SEGURIDAD", "VIGILANTE"   -> RolParking.AGENTE_SEGURIDAD;
            default                         -> RolParking.PROPIETARIO;
        };
    }
}