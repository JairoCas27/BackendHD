package com.urbanpark.parking.domain.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteOcupacionDTO {
    private int totalEspacios;
    private int ocupados;
    private int libres;
    private int reservados;
    private int fueraServicio;
    private double porcentajeOcupacion;
    private String zonaConMasOcupacion;
}