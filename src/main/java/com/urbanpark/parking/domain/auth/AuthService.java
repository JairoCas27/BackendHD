package com.urbanpark.parking.domain.auth;

import com.urbanpark.parking.domain.auth.dto.AuthResponse;
import com.urbanpark.parking.domain.auth.dto.ExternalLoginRequest;
import com.urbanpark.parking.domain.auth.dto.ExternalLoginResult;
import com.urbanpark.parking.domain.auth.dto.UsuarioExternoDTO;
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

    public AuthResponse login(ExternalLoginRequest request) {

        // 1. Validar que el condominio existe y esta ACTIVO
        Condominio condominio = condominioRepository.findById(request.getCondominioId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Condominio no encontrado"));

        if (condominio.getEstado() != EstadoCondominio.ACTIVO) {
            throw new IllegalArgumentException(
                    "El condominio no esta activo en el sistema");
        }

        // 2. Autenticar en la API externa y obtener tokens + datos del usuario
        ExternalLoginResult result = tokenValidator.validate(request, condominio);
        UsuarioExternoDTO externo = result.getUsuario();

        log.info("[{}] Login exitoso para: {} (rol: {})",
                condominio.getNombre(), externo.getCorreo(), externo.getRol());

        // 3. Mapear y devolver respuesta propia
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

    /**
     * Extrae solo el valor del token desde el par "access_token=eyJ..."
     * para no exponer la forma de cookie al cliente.
     */
    private String extraerValorToken(String cookiePar) {
        if (cookiePar == null) return null;
        int idx = cookiePar.indexOf('=');
        return idx >= 0 ? cookiePar.substring(idx + 1) : cookiePar;
    }
}