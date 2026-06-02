package com.urbanpark.parking.domain.integration.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HealthExternoDTO {
    private LocalDateTime timestamp;
    private String status;
}