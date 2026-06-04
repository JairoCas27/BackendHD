package com.urbanpark.parking.domain.rules.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidacionResult {

    private boolean permitido;
    private String motivo;
    private Long reglaId;
    private String reglaNombre;
}
