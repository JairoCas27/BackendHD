package com.urbanpark.parking.integration.client;
 
import com.urbanpark.parking.integration.dto.ExternalCondominiumDtos.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
 
import java.util.Collections;
import java.util.List;
import java.util.Optional;
 
/**
 * Cliente HTTP para consumir la API del condominio externo.
 *
 * Responsabilidades:
 * - Proveer datos de usuarios, vehículos y apartamentos del condominio.
 * - Usarse durante la sincronización periódica (RF-05) y durante el login (RF-02).
 *
 * El JWT del condominio se pasa como Bearer token en cada llamada.
 */
@Slf4j
@Component
public class CondominiumApiClient {
 
    private final RestTemplate restTemplate;
 
    /**
     * Base URL del API del condominio. Se configura por tenant en producción.
     * Este valor es el default; en multi-tenant real se resuelve dinámicamente.
     */
    @Value("${condominium.api.base-url:http://localhost:8081}")
    private String baseUrl;
 
    public CondominiumApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
 
    // ──────────────────────────────────────────────────────────────────────────
    // USUARIOS
    // ──────────────────────────────────────────────────────────────────────────
 
    /**
     * Obtiene todos los usuarios del condominio usando el JWT externo.
     */
    public List<ExternalUserDto> getAllUsers(String condominiumJwt) {
        String url = baseUrl + "/api/usuarios";
        try {
            ResponseEntity<List<ExternalUserDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                buildRequest(condominiumJwt),
                new ParameterizedTypeReference<>() {}
            );
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (HttpClientErrorException e) {
            log.error("Error al obtener usuarios del condominio: {} {}", e.getStatusCode(), e.getMessage());
            return Collections.emptyList();
        }
    }
 
    /**
     * Obtiene un usuario por ID del sistema externo.
     */
    public Optional<ExternalUserDto> getUserById(String userId, String condominiumJwt) {
        String url = baseUrl + "/api/usuarios/" + userId;
        try {
            ResponseEntity<ExternalUserDto> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                buildRequest(condominiumJwt),
                ExternalUserDto.class
            );
            return Optional.ofNullable(response.getBody());
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (HttpClientErrorException e) {
            log.error("Error al obtener usuario {}: {} {}", userId, e.getStatusCode(), e.getMessage());
            return Optional.empty();
        }
    }
 
    // ──────────────────────────────────────────────────────────────────────────
    // VEHÍCULOS
    // ──────────────────────────────────────────────────────────────────────────
 
    /**
     * Obtiene todos los vehículos registrados en el condominio externo.
     */
    public List<ExternalVehicleDto> getAllVehicles(String condominiumJwt) {
        String url = baseUrl + "/api/vehiculos";
        try {
            ResponseEntity<List<ExternalVehicleDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                buildRequest(condominiumJwt),
                new ParameterizedTypeReference<>() {}
            );
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (HttpClientErrorException e) {
            log.error("Error al obtener vehículos del condominio: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
 
    // ──────────────────────────────────────────────────────────────────────────
    // APARTAMENTOS / INQUILINOS
    // ──────────────────────────────────────────────────────────────────────────
 
    public Optional<ExternalApartmentDto> getApartmentById(String apartmentId, String condominiumJwt) {
        String url = baseUrl + "/api/apartamentos/" + apartmentId;
        try {
            ResponseEntity<ExternalApartmentDto> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                buildRequest(condominiumJwt),
                ExternalApartmentDto.class
            );
            return Optional.ofNullable(response.getBody());
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (HttpClientErrorException e) {
            log.error("Error al obtener apartamento {}: {}", apartmentId, e.getMessage());
            return Optional.empty();
        }
    }
 
    // ──────────────────────────────────────────────────────────────────────────
    // HELPERS
    // ──────────────────────────────────────────────────────────────────────────
 
    private HttpEntity<Void> buildRequest(String jwt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwt);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(headers);
    }
}