package com.urbanpark.parking.domain.integration.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class ConexionStatusDTO {
    private UUID condominioId;
    private String apiUrl;
    private boolean disponible;
    private String healthStatus;  // "UP" / "DOWN"
}