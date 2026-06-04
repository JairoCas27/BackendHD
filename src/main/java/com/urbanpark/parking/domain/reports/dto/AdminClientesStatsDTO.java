package com.urbanpark.parking.domain.reports.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class AdminClientesStatsDTO {

    @JsonProperty("totalClientes")
    private Long totalClientes;

    @JsonProperty("clientesActivos")
    private Long clientesActivos;

    @JsonProperty("clientesPendientesPlan")
    private Long clientesPendientesPlan;

    @JsonProperty("clientesSuspendidos")
    private Long clientesSuspendidos;

    @JsonProperty("totalCondominiosRegistrados")
    private Long totalCondominiosRegistrados;

    @JsonProperty("timestamp")
    private String timestamp;
}