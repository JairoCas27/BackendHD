package com.urbanpark.parking.integration.condominio;

import com.urbanpark.parking.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class CondominioApiClient {

    private final RestClient restClient;

    public CondominioApiClient(
            RestClient.Builder builder,
            @Value("${core.api.url:https://sistemagestioncondominios-backend.onrender.com}") String baseUrl) {
        this.restClient = builder
                .baseUrl(baseUrl)
                .defaultHeader("Accept", "application/json")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public CondominioInfoResponse obtenerCondominio(String condominioId) {
        log.info("Consultando condominio: {}", condominioId);
        try {
            return restClient.get()
                    .uri("/api/condominios/{id}", condominioId)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        throw new ApiException("Condominio no encontrado: " + condominioId, res.getStatusCode().value());
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                        throw new ApiException("Error en servidor del condominio", res.getStatusCode().value());
                    })
                    .body(CondominioInfoResponse.class);
        } catch (RestClientResponseException e) {
            log.error("Error al obtener condominio {}: {}", condominioId, e.getMessage());
            throw new ApiException("Fallo conexion con API de condominio", e.getStatusCode().value());
        }
    }

    public boolean validarAcceso(String usuarioId, String condominioId) {
        log.info("Validando acceso usuario {} en condominio {}", usuarioId, condominioId);
        AccesoRequest request = new AccesoRequest(usuarioId, condominioId);
        try {
            return Boolean.TRUE.equals(restClient.post()
                    .uri("/api/condominios/validar-acceso")
                    .body(request)
                    .retrieve()
                    .body(Boolean.class));
        } catch (RestClientResponseException e) {
            log.error("Error validando acceso: {}", e.getMessage());
            return false;
        }
    }

    public List<UsuarioExterno> obtenerUsuariosActivos(String condominioId) {
        log.info("Obteniendo usuarios activos del condominio: {}", condominioId);
        try {
            return restClient.get()
                    .uri("/api/condominios/{id}/usuarios?estado=ACTIVO", condominioId)
                    .retrieve()
                    .body(new org.springframework.core.ParameterizedTypeReference<>() {});
        } catch (RestClientResponseException e) {
            log.error("Error obteniendo usuarios: {}", e.getMessage());
            throw new ApiException("No se pudieron obtener usuarios del condominio", e.getStatusCode().value());
        }
    }

    public List<EspacioParqueo> obtenerEspaciosDisponibles(String condominioId) {
        log.info("Consultando espacios disponibles en condominio: {}", condominioId);
        try {
            return restClient.get()
                    .uri("/api/condominios/{id}/espacios?disponible=true", condominioId)
                    .retrieve()
                    .body(new org.springframework.core.ParameterizedTypeReference<>() {});
        } catch (RestClientResponseException e) {
            log.error("Error obteniendo espacios: {}", e.getMessage());
            throw new ApiException("No se pudieron obtener espacios de parqueo", e.getStatusCode().value());
        }
    }

    public Map<String, Object> obtenerEstadoSistema(String condominioId) {
        log.info("Consultando estado del sistema del condominio: {}", condominioId);
        try {
            return restClient.get()
                    .uri("/api/condominios/{id}/estado", condominioId)
                    .retrieve()
                    .body(new org.springframework.core.ParameterizedTypeReference<>() {});
        } catch (RestClientResponseException e) {
            log.error("Error obteniendo estado: {}", e.getMessage());
            throw new ApiException("Servicio de estado no disponible", e.getStatusCode().value());
        }
    }
}