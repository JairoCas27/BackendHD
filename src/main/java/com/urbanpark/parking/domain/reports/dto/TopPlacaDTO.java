package com.urbanpark.parking.domain.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopPlacaDTO {
    private String placa;
    private long totalDenegados;
}