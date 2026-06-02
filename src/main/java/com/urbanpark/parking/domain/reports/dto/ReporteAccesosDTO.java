package com.urbanpark.parking.domain.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteAccesosDTO {
    private long totalEntradas;
    private long totalSalidas;
    private long totalDenegados;
    private long vehiculosActivos;
    private double duracionPromedioMinutos;
    private LocalDateTime desde;
    private LocalDateTime hasta;
}