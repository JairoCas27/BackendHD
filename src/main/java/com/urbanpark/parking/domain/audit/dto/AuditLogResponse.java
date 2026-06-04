// domain/audit/dto/AuditLogResponse.java
package com.urbanpark.parking.domain.audit.dto;

import com.urbanpark.parking.shared.enums.TipoAccionAudit;
import java.time.LocalDateTime;

public record AuditLogResponse(
        Long id,
        Long usuarioSaasId,
        String usuarioEmail,
        String rolUsuario,
        TipoAccionAudit accion,
        String descripcion,
        String entidadAfectada,
        String endpoint,
        String metodoHttp,
        String ipOrigen,
        boolean exitoso,
        String detalleError,
        LocalDateTime fechaHora
) {}