// domain/audit/dto/AuditLogFiltroRequest.java
package com.urbanpark.parking.domain.audit.dto;

import com.urbanpark.parking.shared.enums.TipoAccionAudit;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

public record AuditLogFiltroRequest(
        Long usuarioId,
        TipoAccionAudit accion,
        Boolean exitoso,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta
) {}