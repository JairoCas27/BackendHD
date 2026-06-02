package com.urbanpark.parking.domain.integration.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ConexionStatusDTO {
    private UUID tenantId;
    private String apiUrl;
    private boolean disponible;
    private String healthStatus;    // "UP" / "DOWN"
    private LocalDateTime ultimaSync;
    private String estadoUltimaSync;
}