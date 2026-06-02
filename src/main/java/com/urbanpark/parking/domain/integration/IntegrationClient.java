package com.urbanpark.parking.domain.integration;

import com.urbanpark.parking.domain.integration.dto.HealthExternoDTO;
import com.urbanpark.parking.domain.tenant.Condominio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class IntegrationClient {

    private final RestTemplate restTemplate;

    public boolean verificarConexion(Condominio condominio) {
        String url = condominio.getApiBaseUrl() + "/api/health";
        try {
            ResponseEntity<HealthExternoDTO> response = restTemplate.exchange(
                    url, HttpMethod.GET,
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
                    url, HttpMethod.GET,
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
}