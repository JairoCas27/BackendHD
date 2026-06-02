package com.urbanpark.parking.domain.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteAccesosPorDiaDTO {
    private String fecha;
    private long entradas;
    private long denegados;
}