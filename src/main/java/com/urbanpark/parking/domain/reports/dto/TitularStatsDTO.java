package com.urbanpark.parking.domain.reports.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class TitularStatsDTO {

    private Long totalCondominios;
    private String razonSocial;
    private String ruc;
    private String planActual;
    private String estadoPlan;

    @JsonProperty("timestamp")
    private String timestamp;
}