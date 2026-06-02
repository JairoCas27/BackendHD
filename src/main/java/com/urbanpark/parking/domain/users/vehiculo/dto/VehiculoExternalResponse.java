package com.urbanpark.parking.domain.users.vehiculo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehiculoExternalResponse {
    private Long id;
    private String marca;
    private String color;
    private String modelo;
    private String placa;
    private String tipo;
    private Long propietarioId;
    private Long inquilinoId;
    private Long estacionamientoId;
}