package com.urbanpark.parking.domain.integration;

import com.urbanpark.parking.domain.auth.ExternalTokenValidator;
import com.urbanpark.parking.domain.integration.dto.*;
import com.urbanpark.parking.domain.tenant.Condominio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class IntegrationClient {

    private final RestTemplate restTemplate;
    private final ExternalTokenValidator tokenValidator;

    private String obtenerCookieAcceso(Condominio condominio) {
        return tokenValidator.obtenerCookieParaSync(
                condominio.getSyncEmail(),
                condominio.getSyncPassword(),
                condominio
        );
    }

    public boolean verificarConexion(Condominio condominio) {
        String url = condominio.getApiBaseUrl() + "/api/health";
        try {
            ResponseEntity<HealthExternoDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(new HttpHeaders()),
                    HealthExternoDTO.class
            );
            HealthExternoDTO health = response.getBody();
            boolean up = health != null && "UP".equalsIgnoreCase(health.getStatus());
            log.info("[{}] Health: {}", condominio.getNombre(), up ? "UP" : "DOWN");
            return up;
        } catch (ResourceAccessException e) {
            log.warn("[{}] API no disponible: {}", condominio.getNombre(), e.getMessage());
            return false;
        } catch (Exception e) {
            log.warn("[{}] Error health check: {}", condominio.getNombre(), e.getMessage());
            return false;
        }
    }

    public HealthExternoDTO obtenerHealth(Condominio condominio) {
        String url = condominio.getApiBaseUrl() + "/api/health";
        try {
            ResponseEntity<HealthExternoDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(new HttpHeaders()),
                    HealthExternoDTO.class
            );
            return response.getBody();
        } catch (Exception e) {
            log.warn("[{}] Error obteniendo health: {}", condominio.getNombre(), e.getMessage());
            HealthExternoDTO down = new HealthExternoDTO();
            down.setStatus("DOWN");
            return down;
        }
    }

    public List<UsuarioExternoSyncDTO> obtenerUsuarios(Condominio condominio) {
        String url = condominio.getApiBaseUrl() + "/api/usuarios";
        try {
            String cookie = obtenerCookieAcceso(condominio);

            ResponseEntity<PaginadoExternoDTO<UsuarioExternoSyncDTO>> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            buildEntityConCookie(cookie),
                            new ParameterizedTypeReference<>() {}
                    );

            PaginadoExternoDTO<UsuarioExternoSyncDTO> body = response.getBody();
            if (body != null && body.getContenido() != null) {
                log.info("[{}] Usuarios obtenidos: {}",
                        condominio.getNombre(), body.getContenido().size());
                return body.getContenido();
            }
            return Collections.emptyList();

        } catch (Exception e) {
            log.error("[{}] Error obteniendo usuarios: {}",
                    condominio.getNombre(), e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<VehiculoExternoSyncDTO> obtenerVehiculos(Condominio condominio) {
        String url = condominio.getApiBaseUrl() + "/api/vehiculos";
        try {
            String cookie = obtenerCookieAcceso(condominio);

            ResponseEntity<PaginadoExternoDTO<VehiculoExternoSyncDTO>> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            buildEntityConCookie(cookie),
                            new ParameterizedTypeReference<>() {}
                    );

            PaginadoExternoDTO<VehiculoExternoSyncDTO> body = response.getBody();
            if (body != null && body.getContenido() != null) {
                log.info("[{}] Vehículos obtenidos: {}",
                        condominio.getNombre(), body.getContenido().size());
                return body.getContenido();
            }
            return Collections.emptyList();

        } catch (Exception e) {
            log.error("[{}] Error obteniendo vehículos: {}",
                    condominio.getNombre(), e.getMessage());
            return Collections.emptyList();
        }
    }

    public ApartamentoExternoDTO obtenerApartamento(Condominio condominio, Long apartamentoId) {
        String url = condominio.getApiBaseUrl() + "/api/apartamentos/" + apartamentoId;
        try {
            String cookie = obtenerCookieAcceso(condominio);

            ResponseEntity<ApartamentoExternoDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    buildEntityConCookie(cookie),
                    ApartamentoExternoDTO.class
            );
            return response.getBody();

        } catch (HttpClientErrorException.NotFound e) {
            log.warn("[{}] Apartamento {} no encontrado",
                    condominio.getNombre(), apartamentoId);
            return null;
        } catch (Exception e) {
            log.error("[{}] Error obteniendo apartamento {}: {}",
                    condominio.getNombre(), apartamentoId, e.getMessage());
            return null;
        }
    }

    public InquilinoExternoDTO obtenerInquilino(Condominio condominio, Long inquilinoId) {
        String url = condominio.getApiBaseUrl() + "/api/inquilinos/" + inquilinoId;
        try {
            String cookie = obtenerCookieAcceso(condominio);

            ResponseEntity<InquilinoExternoDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    buildEntityConCookie(cookie),
                    InquilinoExternoDTO.class
            );
            return response.getBody();

        } catch (HttpClientErrorException.NotFound e) {
            log.warn("[{}] Inquilino {} no encontrado",
                    condominio.getNombre(), inquilinoId);
            return null;
        } catch (Exception e) {
            log.error("[{}] Error obteniendo inquilino {}: {}",
                    condominio.getNombre(), inquilinoId, e.getMessage());
            return null;
        }
    }

    private HttpEntity<Void> buildEntityConCookie(String cookie) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.COOKIE, cookie);
        return new HttpEntity<>(headers);
    }
}