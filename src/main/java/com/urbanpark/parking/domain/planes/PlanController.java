package com.urbanpark.parking.domain.planes;

import com.urbanpark.parking.domain.planes.dto.PlanRequest;
import com.urbanpark.parking.domain.planes.dto.PlanResponse;
import com.urbanpark.parking.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Planes", description = "Gestión de planes del SaaS")
public class PlanController {

    private final PlanService planService;

    // ─── Público ──────────────────────────────────────────────

    @GetMapping("/api/v1/planes")
    @Operation(summary = "Listar planes activos (público)")
    public ResponseEntity<ApiResponse<List<PlanResponse>>> listarActivos() {
        return ResponseEntity.ok(ApiResponse.success(planService.listarActivos()));
    }

    // ─── ADMIN / SUPERADMIN ───────────────────────────────────

    @GetMapping("/api/v1/admin/planes")
    @Secured("ROLE_ADMIN")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Listar todos los planes")
    public ResponseEntity<ApiResponse<List<PlanResponse>>> listarTodos() {
        return ResponseEntity.ok(ApiResponse.success(planService.listarTodos()));
    }

    @GetMapping("/api/v1/admin/planes/{id}")
    @Secured("ROLE_ADMIN")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener plan por ID")
    public ResponseEntity<ApiResponse<PlanResponse>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(planService.obtenerPorId(id)));
    }

    @PostMapping("/api/v1/admin/planes")
    @Secured("ROLE_ADMIN")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Crear plan")
    public ResponseEntity<ApiResponse<PlanResponse>> crear(
            @Valid @RequestBody PlanRequest request) {

        PlanResponse response = planService.crear(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Plan creado exitosamente", response));
    }

    @PutMapping("/api/v1/admin/planes/{id}")
    @Secured("ROLE_ADMIN")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Actualizar plan")
    public ResponseEntity<ApiResponse<PlanResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PlanRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success("Plan actualizado", planService.actualizar(id, request)));
    }

    @DeleteMapping("/api/v1/admin/planes/{id}")
    @Secured("ROLE_ADMIN")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Eliminar plan")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        planService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.success("Plan eliminado", null));
    }
}