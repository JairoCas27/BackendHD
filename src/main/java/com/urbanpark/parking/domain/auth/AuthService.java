package com.urbanpark.parking.domain.auth;

import com.urbanpark.parking.domain.auth.dto.AuthResponse;
import com.urbanpark.parking.domain.auth.dto.ExternalLoginRequest;
import com.urbanpark.parking.domain.auth.dto.ExternalLoginResult;
import com.urbanpark.parking.domain.auth.dto.UsuarioExternoDTO;
import com.urbanpark.parking.domain.integration.UsuarioSesion;
import com.urbanpark.parking.domain.integration.UsuarioSesionRepository;
import com.urbanpark.parking.domain.tenant.Condominio;
import com.urbanpark.parking.domain.tenant.CondominioRepository;
import com.urbanpark.parking.shared.enums.EstadoCondominio;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final CondominioRepository condominioRepository;
    private final ExternalTokenValidator tokenValidator;
    private final UsuarioSesionRepository usuarioSesionRepository;

    public AuthResponse login(ExternalLoginRequest request) {

        // 1. Validar que el condominio existe y está ACTIVO
        Condominio condominio = condominioRepository.findById(request.getCondominioId())
                .orElseThrow(() -> new EntityNotFoundException("Condominio no encontrado"));

        if (condominio.getEstado() != EstadoCondominio.ACTIVO) {
            throw new IllegalArgumentException("El condominio no esta activo en el sistema");
        }

        // 2. Autenticar en la API externa → tokens + datos del usuario
        ExternalLoginResult result = tokenValidator.validate(request, condominio);
        UsuarioExternoDTO externo = result.getUsuario();

        // 3. Registrar sesión (historial de logins)
        usuarioSesionRepository.save(UsuarioSesion.builder()
                .externalUserId(externo.getId())
                .condominioId(condominio.getId())
                .email(externo.getCorreo())
                .nombre(externo.getNombres() + " " + externo.getApellidos())
                .rol(externo.getRol())
                .build());

        log.info("[{}] Login registrado: {} ({})",
                condominio.getNombre(), externo.getCorreo(), externo.getRol());

        // 4. Mapear y devolver respuesta
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