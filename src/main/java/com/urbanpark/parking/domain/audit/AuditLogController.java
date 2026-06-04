// domain/audit/AuditLogController.java
package com.urbanpark.parking.domain.audit;

import com.urbanpark.parking.domain.audit.dto.AuditLogFiltroRequest;
import com.urbanpark.parking.domain.audit.dto.AuditLogResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
@Tag(name = "Auditoría", description = "Logs del sistema — solo SUPERADMIN")
public class AuditLogController {

    private final AuditLogService service;

    @GetMapping
    @Secured("ROLE_SUPERADMIN")
    @Operation(summary = "Listar todos los logs paginados")
    public ResponseEntity<Page<AuditLogResponse>> listar(
            @PageableDefault(size = 20, sort = "fechaHora") Pageable pageable) {
        return ResponseEntity.ok(service.listarTodos(pageable));
    }

    @GetMapping("/filtrar")
    @Secured("ROLE_SUPERADMIN")
    @Operation(summary = "Filtrar logs por usuario, acción, resultado y rango de fechas")
    public ResponseEntity<Page<AuditLogResponse>> filtrar(
            AuditLogFiltroRequest filtro,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(service.filtrar(filtro, pageable));
    }

    @GetMapping("/{id}")
    @Secured("ROLE_SUPERADMIN")
    @Operation(summary = "Obtener log por ID")
    public ResponseEntity<AuditLogResponse> porId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }
}