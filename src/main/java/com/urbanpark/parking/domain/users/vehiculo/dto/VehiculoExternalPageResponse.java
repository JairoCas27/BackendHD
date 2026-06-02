package com.urbanpark.parking.domain.users.vehiculo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehiculoExternalPageResponse {
    private List<VehiculoExternalResponse> contenido;
    private int pagina;
    private int tamanio;
    private long totalElementos;
    private int totalPaginas;
}