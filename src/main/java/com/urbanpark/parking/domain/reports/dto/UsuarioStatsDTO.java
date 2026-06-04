package com.urbanpark.parking.domain.reports.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class UsuarioStatsDTO {

    private Long totalSuperAdmins;
    private Long totalAdmins;
    private Long totalClientes;
    private Long totalUsuarios;

    @JsonProperty("timestamp")
    private String timestamp;
}
