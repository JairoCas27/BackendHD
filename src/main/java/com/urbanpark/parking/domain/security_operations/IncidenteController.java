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

    @PostMapping
    @PreAuthorize("hasAnyRole('AGENTE_SEGURIDAD', 'ADMIN_CONDOMINIO')")
    public ResponseEntity<ApiResponse<IncidenteResponse>> reportar(
            @RequestBody @Valid IncidenteRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.success("Incidente reportado",
                        incidenteService.reportar(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'AGENTE_SEGURIDAD')")
    public ResponseEntity<ApiResponse<List<IncidenteResponse>>> listar(
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

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'AGENTE_SEGURIDAD')")
    public ResponseEntity<ApiResponse<IncidenteResponse>> buscar(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(incidenteService.buscarPorId(id)));
    }

    @GetMapping("/agente/{agenteId}")
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'AGENTE_SEGURIDAD')")
    public ResponseEntity<ApiResponse<List<IncidenteResponse>>> listarPorAgente(
            @PathVariable UUID agenteId) {
        return ResponseEntity.ok(ApiResponse.success(
                incidenteService.listarPorAgente(agenteId)));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN_CONDOMINIO')")
    public ResponseEntity<ApiResponse<IncidenteResponse>> cambiarEstado(
            @PathVariable UUID id,
            @RequestParam EstadoIncidente estado) {
        return ResponseEntity.ok(ApiResponse.success(
                incidenteService.cambiarEstado(id, estado)));
    }

    @PatchMapping("/{id}/resolver")
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'AGENTE_SEGURIDAD')")
    public ResponseEntity<ApiResponse<IncidenteResponse>> resolver(
            @PathVariable UUID id,
            @RequestBody @Valid ResolucionRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Incidente resuelto",
                incidenteService.resolver(id, request)));
    }
}