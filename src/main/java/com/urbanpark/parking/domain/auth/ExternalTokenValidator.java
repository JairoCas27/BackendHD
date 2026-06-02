package com.urbanpark.parking.domain.auth;

import com.urbanpark.parking.domain.auth.dto.ExternalLoginRequest;
import com.urbanpark.parking.domain.auth.dto.UsuarioExternoDTO;
import com.urbanpark.parking.domain.tenant.Condominio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalTokenValidator {

    private final RestTemplate restTemplate;

    public UsuarioExternoDTO validate(ExternalLoginRequest request, Condominio condominio) {
        String cookiePar = doExternalLogin(request.getEmail(), request.getPassword(), condominio);
        return doGetMe(cookiePar, condominio);
    }

    // Retorna "access_token=eyJ...abc" — usado por IntegrationClient
    public String obtenerCookieParaSync(String email, String password, Condominio condominio) {
        return doExternalLogin(email, password, condominio);
    }

    // Retorna "access_token=eyJ...abc" (par completo listo para Cookie header)
    private String doExternalLogin(String email, String password, Condominio condominio) {
        String loginUrl = condominio.getApiBaseUrl() + "/api/auth/login";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "email",      email,
                "password",   password,
                "rememberMe", true
        );

        try {
            ResponseEntity<Void> response = restTemplate.exchange(
                    loginUrl,
                    HttpMethod.POST,
                    new HttpEntity<>(body, headers),
                    Void.class
            );

            List<String> setCookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);

            if (setCookies == null || setCookies.isEmpty()) {
                throw new IllegalArgumentException(
                        "El sistema del condominio no devolvió cookies");
            }

            return setCookies.stream()
                    .filter(c -> c.startsWith("access_token="))
                    .findFirst()
                    .map(c -> c.split(";")[0].trim())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Cookie access_token no encontrada en respuesta del condominio"));

        } catch (HttpClientErrorException.Unauthorized e) {
            throw new IllegalArgumentException(
                    "Credenciales inválidas en el sistema del condominio");
        } catch (HttpClientErrorException e) {
            log.error("[{}] Error en login externo: {}",
                    condominio.getNombre(), e.getMessage());
            throw new IllegalArgumentException(
                    "Error al autenticar en el sistema del condominio");
        }
    }

    private UsuarioExternoDTO doGetMe(String cookiePar, Condominio condominio) {
        String meUrl = condominio.getApiBaseUrl() + "/api/auth/me";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, cookiePar);

        try {
            ResponseEntity<UsuarioExternoDTO> response = restTemplate.exchange(
                    meUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    UsuarioExternoDTO.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            }

            throw new IllegalArgumentException(
                    "No se pudo obtener información del usuario del condominio");

        } catch (HttpClientErrorException.Unauthorized e) {
            throw new IllegalArgumentException(
                    "Sesión expirada en el sistema del condominio");
        } catch (HttpClientErrorException e) {
            log.error("[{}] Error en /me externo: {}",
                    condominio.getNombre(), e.getMessage());
            throw new IllegalArgumentException(
                    "Error al obtener datos del usuario externo");
        }
    }
}