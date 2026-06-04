package com.urbanpark.parking.domain.reports.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class TopPlanDTO {

    @JsonProperty("posicion")
    private Integer posicion;

    @JsonProperty("nombrePlan")
    private String nombrePlan;

    @JsonProperty("descripcion")
    private String descripcion;

    @JsonProperty("totalAdquisiciones")
    private Long totalAdquisiciones;

    @JsonProperty("precio")
    private BigDecimal precio;

    @JsonProperty("moneda")
    private String moneda;

    @JsonProperty("limiteCondominios")
    private String limiteCondominios;

    @JsonProperty("estado")
    private String estado;
}