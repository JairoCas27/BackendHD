package com.urbanpark.parking.domain.reports.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ReporteDetalladoDTO {

    @JsonProperty("estadisticasGlobales")
    private GlobalStatsDTO estadisticasGlobales;

    @JsonProperty("estadisticasClientes")
    private AdminClientesStatsDTO estadisticasClientes;

    @JsonProperty("topPlanesStats")
    private TopPlanesStatsDTO topPlanesStats;

    @JsonProperty("timestamp")
    private String timestamp;
}