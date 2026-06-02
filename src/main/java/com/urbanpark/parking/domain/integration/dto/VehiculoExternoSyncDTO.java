package com.urbanpark.parking.domain.integration.dto;

import lombok.Data;

@Data
public class VehiculoExternoSyncDTO {
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