package com.urbanpark.parking.domain.audit;

import com.urbanpark.parking.domain.audit.dto.AuditLogResponse;
import com.urbanpark.parking.shared.dto.ApiResponse;
import com.urbanpark.parking.shared.enums.TipoAccionAudit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditQueryService auditQueryService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'SUPERADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> listar(
            @RequestParam(required = false) TipoAccionAudit accion,
            @RequestParam(required = false) UUID usuarioId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin,
            @PageableDefault(size = 50, sort = "createdAt") Pageable pageable) {

        Page<AuditLogResponse> resultado;

        if (accion != null) {
            resultado = auditQueryService.listarPorAccion(accion, pageable);
        } else if (usuarioId != null) {
            resultado = auditQueryService.listarPorUsuario(usuarioId, pageable);
        } else if (inicio != null && fin != null) {
            resultado = auditQueryService.listarPorRango(inicio, fin, pageable);
        } else {
            resultado = auditQueryService.listarPorTenant(pageable);
        }

        return ResponseEntity.ok(ApiResponse.success(resultado));
    }

    @GetMapping("/entidad/{entidad}/{entidadId}")
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'SUPERADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> historialEntidad(
            @PathVariable String entidad,
            @PathVariable String entidadId) {
        return ResponseEntity.ok(ApiResponse.success(
                auditQueryService.historialEntidad(entidad, entidadId)));
    }

    @GetMapping("/global")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> global(
            @PageableDefault(size = 50, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                auditQueryService.listarGlobal(pageable)));
    }
}