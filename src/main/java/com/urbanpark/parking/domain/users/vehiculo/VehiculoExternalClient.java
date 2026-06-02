package com.urbanpark.parking.domain.users.vehiculo;

import com.urbanpark.parking.domain.users.vehiculo.dto.VehiculoExternalPageResponse;
import com.urbanpark.parking.domain.users.vehiculo.dto.VehiculoExternalResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class VehiculoExternalClient {

    private final RestTemplate restTemplate;


    public List<VehiculoExternalResponse> listarTodos(String apiBaseUrl,
                                                      String accessTokenCookie) {
        String url = apiBaseUrl + "/api/vehiculos";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, accessTokenCookie);

        try {
            ResponseEntity<VehiculoExternalPageResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    VehiculoExternalPageResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK
                    && response.getBody() != null
                    && response.getBody().getContenido() != null) {
                return response.getBody().getContenido();
            }

            return List.of();

        } catch (HttpClientErrorException.Unauthorized e) {
            log.warn("Token externo expirado al obtener vehiculos: {}", apiBaseUrl);
            throw new IllegalStateException("Sesion expirada en el sistema del condominio");
        } catch (HttpClientErrorException e) {
            log.error("Error al obtener vehiculos externos [{}]: {}", apiBaseUrl, e.getMessage());
            throw new IllegalStateException("Error al consultar vehiculos del condominio");
        }
    }
}