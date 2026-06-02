package com.urbanpark.parking.domain.users.visitante;

import com.urbanpark.parking.domain.users.visitante.dto.VisitanteRequest;
import com.urbanpark.parking.domain.users.visitante.dto.VisitanteResponse;
import com.urbanpark.parking.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/visitantes")
@RequiredArgsConstructor
public class VisitanteController {

    private final VisitanteService visitanteService;

    @PostMapping
    @PreAuthorize("hasAnyRole('PROPIETARIO', 'ADMIN_CONDOMINIO')")
    public ResponseEntity<ApiResponse<VisitanteResponse>> crear(
            @RequestBody @Valid VisitanteRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.success("Visitante registrado", visitanteService.crear(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'AGENTE_SEGURIDAD')")
    public ResponseEntity<ApiResponse<List<VisitanteResponse>>> listar() {
        return ResponseEntity.ok(ApiResponse.success(visitanteService.listarTodos()));
    }

    @GetMapping("/propietario/{propietarioId}")
    @PreAuthorize("hasAnyRole('PROPIETARIO', 'ADMIN_CONDOMINIO')")
    public ResponseEntity<ApiResponse<List<VisitanteResponse>>> listarPorPropietario(
            @PathVariable UUID propietarioId) {
        return ResponseEntity.ok(ApiResponse.success(
                visitanteService.listarPorPropietario(propietarioId)));
    }

    @PatchMapping("/{id}/revocar")
    @PreAuthorize("hasAnyRole('PROPIETARIO', 'ADMIN_CONDOMINIO')")
    public ResponseEntity<ApiResponse<Void>> revocar(@PathVariable UUID id) {
        visitanteService.revocar(id);
        return ResponseEntity.ok(ApiResponse.success("Acceso revocado", null));
    }
}