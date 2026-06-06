// domain/audit/AuditLogController.java
package com.urbanpark.parking.domain.audit;

import com.urbanpark.parking.domain.audit.dto.AuditLogFiltroRequest;
import com.urbanpark.parking.domain.audit.dto.AuditLogResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
@Tag(name = "Auditoría", description = "Logs del sistema — solo SUPERADMIN")
public class AuditLogController {

    private final AuditLogService service;
    private final AuditLogCleanupService cleanupService; 

    @GetMapping
    @Secured("ROLE_SUPERADMIN")
    @Operation(summary = "Listar todos los logs paginados")
    public ResponseEntity<Page<AuditLogResponse>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
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

    // ═══════════════════════════════════════════════════════════════
    // NUEVO ENDPOINT: Limpieza manual de logs
    // ═══════════════════════════════════════════════════════════════
    @PostMapping("/admin/cleanup")
    @Secured("ROLE_SUPERADMIN")
    @Operation(summary = "Limpieza manual de logs antiguos (desarrollo/testing)")
    public ResponseEntity<Map<String, Object>> limpiarLogsManual(
            @RequestParam(defaultValue = "6") int meses) {
        
        try {
            int eliminados = cleanupService.limpiarLogsManual(meses);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Limpieza completada exitosamente");
            response.put("data", Map.of(
                "registrosEliminados", eliminados,
                "mesesRetenidos", meses,
                "fechaCorte", LocalDateTime.now().minusMonths(meses).toString()
            ));
            response.put("timestamp", LocalDateTime.now().toString());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al ejecutar la limpieza: " + e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now().toString());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}