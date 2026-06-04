package com.urbanpark.parking.domain.reports.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class GlobalStatsDTO {

    @JsonProperty("usuarios")
    private UsuarioStatsDTO usuarioStats;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("periodo")
    private String periodo;
}