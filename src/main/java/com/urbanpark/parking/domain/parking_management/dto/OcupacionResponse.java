package com.urbanpark.parking.domain.parking_management.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OcupacionResponse {
    private int total;
    private int ocupados;
    private int libres;
    private int reservados;
    private int fueraServicio;
    private double porcentajeOcupacion;
}