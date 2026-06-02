package com.urbanpark.parking.domain.security_operations;

import com.urbanpark.parking.domain.security_operations.dto.IncidenteRequest;
import com.urbanpark.parking.domain.security_operations.dto.IncidenteResponse;
import com.urbanpark.parking.domain.security_operations.dto.ResolucionRequest;
import com.urbanpark.parking.shared.dto.ApiResponse;
import com.urbanpark.parking.shared.enums.EstadoIncidente;
import com.urbanpark.parking.shared.enums.NivelIncidente;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/incidentes")
@RequiredArgsConstructor
public class IncidenteController {

    private final IncidenteService incidenteService;

    // AGENTE_SEGURIDAD y PROPIETARIO pueden reportar
    @PostMapping
    @PreAuthorize("hasAnyRole('AGENTE_SEGURIDAD', 'PROPIETARIO')")
    public ResponseEntity<ApiResponse<IncidenteResponse>> reportar(
            @RequestBody @Valid IncidenteRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.success("Incidente reportado",
                        incidenteService.reportar(request)));
    }

    // ADMIN_CONDOMINIO lista todos con filtros opcionales
    @GetMapping
    @PreAuthorize("hasRole('ADMIN_CONDOMINIO')")
    public ResponseEntity<ApiResponse<List<IncidenteResponse>>> listarTodos(
            @RequestParam(required = false) EstadoIncidente estado,
            @RequestParam(required = false) NivelIncidente nivel) {

        List<IncidenteResponse> incidentes;

        if (estado != null) {
            incidentes = incidenteService.listarPorEstado(estado);
        } else if (nivel != null) {
            incidentes = incidenteService.listarPorNivel(nivel);
        } else {
            incidentes = incidenteService.listarTodos();
        }

        return ResponseEntity.ok(ApiResponse.success(incidentes));
    }

    // AGENTE y PROPIETARIO solo ven los suyos por sesionId
    @GetMapping("/mis-incidentes")
    @PreAuthorize("hasAnyRole('AGENTE_SEGURIDAD', 'PROPIETARIO')")
    public ResponseEntity<ApiResponse<List<IncidenteResponse>>> listarMios(
            @RequestParam UUID sesionId) {
        return ResponseEntity.ok(ApiResponse.success(
                incidenteService.listarMios(sesionId)));
    }

    // Cualquier rol autenticado puede ver el detalle
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'AGENTE_SEGURIDAD', 'PROPIETARIO')")
    public ResponseEntity<ApiResponse<IncidenteResponse>> buscar(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(incidenteService.buscarPorId(id)));
    }

    // Solo ADMIN_CONDOMINIO cambia estado
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN_CONDOMINIO')")
    public ResponseEntity<ApiResponse<IncidenteResponse>> cambiarEstado(
            @PathVariable UUID id,
            @RequestParam EstadoIncidente estado) {
        return ResponseEntity.ok(ApiResponse.success(
                "Estado actualizado", incidenteService.cambiarEstado(id, estado)));
    }

    // Solo ADMIN_CONDOMINIO resuelve
    @PatchMapping("/{id}/resolver")
    @PreAuthorize("hasRole('ADMIN_CONDOMINIO')")
    public ResponseEntity<ApiResponse<IncidenteResponse>> resolver(
            @PathVariable UUID id,
            @RequestBody @Valid ResolucionRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Incidente resuelto",
                incidenteService.resolver(id, request)));
    }

    // Solo ADMIN_CONDOMINIO elimina
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_CONDOMINIO')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable UUID id) {
        incidenteService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.success("Incidente eliminado", null));
    }
}