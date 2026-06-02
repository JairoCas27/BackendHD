package com.urbanpark.parking.domain.rules.dto;

import com.urbanpark.parking.shared.enums.TipoRegla;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class ReglaResponse {
    private UUID id;
    private String nombre;
    private TipoRegla tipo;
    private Map<String, Object> configuracion;
    private boolean activo;
    private LocalDateTime createdAt;
}