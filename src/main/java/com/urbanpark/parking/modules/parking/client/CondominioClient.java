package com.urbanpark.parking.modules.parking.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.urbanpark.parking.modules.parking.dto.ApartamentoExternoDto;

@Component
public class CondominioClient {

    private final RestTemplate restTemplate;
    // URL base simulada del otro grupo (reemplazar por la real en producción)
    private final String BASE_URL = "http://localhost:8081/api/apartamentos/"; 

    public CondominioClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ApartamentoExternoDto obtenerApartamento(String apartamentoId) {
        try {
            String url = BASE_URL + apartamentoId;
            return restTemplate.getForObject(url, ApartamentoExternoDto.class);
        } catch (Exception e) {
            ApartamentoExternoDto mock = new ApartamentoExternoDto();
            mock.setId(apartamentoId);
            mock.setNumero("101-Mock");
            mock.setMaxVehiculosPermitidos(2); // Regla por defecto: máximo 2 cocheras por departamento
            return mock;
        }
    }
}