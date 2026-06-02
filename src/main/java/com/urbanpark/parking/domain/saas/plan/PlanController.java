package com.urbanpark.parking.domain.saas.plan;

import com.urbanpark.parking.domain.saas.plan.dto.PlanRequestDTO;
import com.urbanpark.parking.domain.saas.plan.dto.PlanResponseDTO;
import com.urbanpark.parking.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/planes")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    // ─── PÚBLICOS (sin JWT) ───────────────────────────────────────────

    @GetMapping
    public ResponseEntity<ApiResponse<List<PlanResponseDTO>>> listar() {
        return ResponseEntity.ok(ApiResponse.success(planService.listarActivos()));
    }

    // ─── REQUIEREN SUPERADMIN o ADMIN ────────────────────────────────

    @GetMapping("/todos")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<PlanResponseDTO>>> listarTodos() {
        return ResponseEntity.ok(ApiResponse.success(planService.listarTodos()));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<PlanResponseDTO>> crear(
            @RequestBody @Valid PlanRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Plan creado", planService.crear(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<PlanResponseDTO>> actualizar(
            @PathVariable UUID id,
            @RequestBody @Valid PlanRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.success("Plan actualizado",
                planService.actualizar(id, request)));
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable UUID id) {
        planService.desactivar(id);
        return ResponseEntity.ok(ApiResponse.success("Plan desactivado", null));
    }

    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable UUID id) {
        planService.activar(id);
        return ResponseEntity.ok(ApiResponse.success("Plan activado", null));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable UUID id) {
        planService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.success("Plan eliminado", null));
    }
}