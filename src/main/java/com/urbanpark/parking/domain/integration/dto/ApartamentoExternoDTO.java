package com.urbanpark.parking.domain.integration.dto;

import lombok.Data;

@Data
public class ApartamentoExternoDTO {
    private Long id;
    private Integer numero;
    private boolean derechoEstacionamiento;
    private double metraje;
    private Long propietarioId;
    private Long pisoId;
}