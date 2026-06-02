package com.urbanpark.parking.domain.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteVehiculoDTO {
    private UUID vehiculoId;
    private String placa;
    private long totalAccesos;
    private long totalDenegados;
    private double duracionPromedioMinutos;
    private LocalDateTime ultimoAcceso;
}