package com.urbanpark.parking.domain.audit.dto;

import com.urbanpark.parking.shared.enums.TipoAccionAudit;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class AuditLogResponse {
    private UUID id;
    private UUID tenantId;
    private UUID usuarioId;
    private TipoAccionAudit accion;
    private String entidad;
    private String entidadId;
    private Map<String, Object> detalle;
    private String ipOrigen;
    private LocalDateTime createdAt;
}