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
        String cookie = doExternalLogin(request, condominio);
        return doGetMe(cookie, condominio);
    }

    private String doExternalLogin(ExternalLoginRequest request, Condominio condominio) {
        String loginUrl = condominio.getApiBaseUrl() + "/api/auth/login";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "email", request.getEmail(),
                "password", request.getPassword(),
                "rememberMe", true
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Void> response = restTemplate.exchange(
                    loginUrl,
                    HttpMethod.POST,
                    entity,
                    Void.class
            );

            List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);

            if (cookies == null || cookies.isEmpty()) {
                throw new IllegalArgumentException("El sistema del condominio no devolvió cookies");
            }

            return cookies.stream()
                    .filter(c -> c.startsWith("access_token="))
                    .findFirst()
                    .map(c -> c.split(";")[0])
                    .orElseThrow(() -> new IllegalArgumentException("Cookie access_token no encontrada"));

        } catch (HttpClientErrorException.Unauthorized e) {
            throw new IllegalArgumentException("Credenciales inválidas en el sistema del condominio");
        } catch (HttpClientErrorException e) {
            log.error("Error en login externo: {}", e.getMessage());
            throw new IllegalArgumentException("Error al autenticar en el sistema del condominio");
        }
    }

    private UsuarioExternoDTO doGetMe(String cookie, Condominio condominio) {
        String meUrl = condominio.getApiBaseUrl() + "/api/auth/me";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, cookie);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<UsuarioExternoDTO> response = restTemplate.exchange(
                    meUrl,
                    HttpMethod.GET,
                    entity,
                    UsuarioExternoDTO.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            }

            throw new IllegalArgumentException("No se pudo obtener información del usuario");

        } catch (HttpClientErrorException.Unauthorized e) {
            throw new IllegalArgumentException("Sesión expirada en el sistema del condominio");
        } catch (HttpClientErrorException e) {
            log.error("Error en /me externo: {}", e.getMessage());
            throw new IllegalArgumentException("Error al obtener datos del usuario externo");
        }
    }
}