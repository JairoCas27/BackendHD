package com.urbanpark.parking.domain.reports.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class TopPlanesStatsDTO {

    @JsonProperty("topPlanes")
    private List<TopPlanDTO> topPlanes;

    @JsonProperty("totalPlanesActivos")
    private Long totalPlanesActivos;

    @JsonProperty("timestamp")
    private String timestamp;
}