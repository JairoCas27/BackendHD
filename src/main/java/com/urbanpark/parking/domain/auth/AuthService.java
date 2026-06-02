package com.urbanpark.parking.domain.auth;

import com.urbanpark.parking.domain.audit.AuditService;
import com.urbanpark.parking.domain.auth.dto.AuthResponse;
import com.urbanpark.parking.domain.auth.dto.ExternalLoginRequest;
import com.urbanpark.parking.domain.auth.dto.ExternalLoginResult;
import com.urbanpark.parking.domain.auth.dto.UsuarioExternoDTO;
import com.urbanpark.parking.domain.integration.UsuarioSesion;
import com.urbanpark.parking.domain.integration.UsuarioSesionRepository;
import com.urbanpark.parking.domain.tenant.Condominio;
import com.urbanpark.parking.domain.tenant.CondominioRepository;
import com.urbanpark.parking.shared.enums.EstadoCondominio;
import com.urbanpark.parking.shared.enums.TipoAccionAudit;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final CondominioRepository condominioRepository;
    private final ExternalTokenValidator tokenValidator;
    private final UsuarioSesionRepository usuarioSesionRepository;
    private final AuditService auditService;

    public AuthResponse login(ExternalLoginRequest request) {

        Condominio condominio = condominioRepository.findById(request.getCondominioId())
                .orElseThrow(() -> new EntityNotFoundException("Condominio no encontrado"));

        if (condominio.getEstado() != EstadoCondominio.ACTIVO) {
            throw new IllegalArgumentException("El condominio no esta activo en el sistema");
        }

        ExternalLoginResult result;
        try {
            result = tokenValidator.validate(request, condominio);
        } catch (Exception e) {
            auditService.registrar(
                    condominio.getId(),
                    null,
                    TipoAccionAudit.LOGIN_FALLIDO,
                    "UsuarioSesion",
                    null,
                    Map.of(
                            "email", request.getEmail(),
                            "condominio", condominio.getNombre(),
                            "motivo", e.getMessage()
                    )
            );
            throw e;
        }

        UsuarioExternoDTO externo = result.getUsuario();

        UsuarioSesion sesion = usuarioSesionRepository.save(UsuarioSesion.builder()
                .externalUserId(externo.getId())
                .condominioId(condominio.getId())
                .email(externo.getCorreo())
                .nombre(externo.getNombres() + " " + externo.getApellidos())
                .rol(externo.getRol())
                .build());

        log.info("[{}] Login registrado: {} ({})",
                condominio.getNombre(), externo.getCorreo(), externo.getRol());

        auditService.registrar(
                condominio.getId(),
                sesion.getId(),
                TipoAccionAudit.LOGIN,
                "UsuarioSesion",
                sesion.getId().toString(),
                Map.of(
                        "email", externo.getCorreo(),
                        "rol", externo.getRol(),
                        "condominio", condominio.getNombre()
                )
        );

        return AuthResponse.builder()
                .condominioId(condominio.getId())
                .condominioNombre(condominio.getNombre())
                .externalUserId(externo.getId())
                .nombre(externo.getNombres() + " " + externo.getApellidos())
                .email(externo.getCorreo())
                .rol(externo.getRol())
                .accessToken(extraerValorToken(result.getAccessToken()))
                .refreshToken(extraerValorToken(result.getRefreshToken()))
                .build();
    }

    private String extraerValorToken(String cookiePar) {
        if (cookiePar == null) return null;
        int idx = cookiePar.indexOf('=');
        return idx >= 0 ? cookiePar.substring(idx + 1) : cookiePar;
    }
}