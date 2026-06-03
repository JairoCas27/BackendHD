package com.urbanpark.parking.domain.solicitudes;

import com.urbanpark.parking.domain.solicitudes.dto.RevisionSolicitudRequest;
import com.urbanpark.parking.domain.solicitudes.dto.SolicitudPlanRequest;
import com.urbanpark.parking.domain.solicitudes.dto.SolicitudPlanResponse;
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
@Tag(name = "Solicitudes de Plan", description = "Gestión de solicitudes de plan")
@SecurityRequirement(name = "bearerAuth")
public class SolicitudPlanController {

    private final SolicitudPlanService solicitudPlanService;

    // ─── CLIENTE ──────────────────────────────────────────────

    @PostMapping("/api/v1/me/solicitud-plan")
    @Secured("ROLE_CLIENTE")
    @Operation(summary = "Solicitar un plan")
    public ResponseEntity<ApiResponse<SolicitudPlanResponse>> solicitar(
            @Valid @RequestBody SolicitudPlanRequest request) {

        SolicitudPlanResponse response = solicitudPlanService.solicitar(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Solicitud enviada, espera la aprobación del administrador", response));
    }

    @GetMapping("/api/v1/me/solicitud-plan")
    @Secured("ROLE_CLIENTE")
    @Operation(summary = "Ver mi última solicitud de plan")
    public ResponseEntity<ApiResponse<SolicitudPlanResponse>> verMiSolicitud() {

        return ResponseEntity.ok(
                ApiResponse.success(solicitudPlanService.obtenerMiSolicitud()));
    }

    // ─── ADMIN ────────────────────────────────────────────────

    @GetMapping("/api/v1/admin/solicitudes-plan")
    @Secured("ROLE_ADMIN")
    @Operation(summary = "Listar todas las solicitudes")
    public ResponseEntity<ApiResponse<List<SolicitudPlanResponse>>> listarTodas() {

        return ResponseEntity.ok(
                ApiResponse.success(solicitudPlanService.listarTodas()));
    }

    @GetMapping("/api/v1/admin/solicitudes-plan/pendientes")
    @Secured("ROLE_ADMIN")
    @Operation(summary = "Listar solicitudes pendientes")
    public ResponseEntity<ApiResponse<List<SolicitudPlanResponse>>> listarPendientes() {

        return ResponseEntity.ok(
                ApiResponse.success(solicitudPlanService.listarPendientes()));
    }

    @PutMapping("/api/v1/admin/solicitudes-plan/{id}/aprobar")
    @Secured("ROLE_ADMIN")
    @Operation(summary = "Aprobar solicitud de plan")
    public ResponseEntity<ApiResponse<SolicitudPlanResponse>> aprobar(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success("Solicitud aprobada", solicitudPlanService.aprobar(id)));
    }

    @PutMapping("/api/v1/admin/solicitudes-plan/{id}/rechazar")
    @Secured("ROLE_ADMIN")
    @Operation(summary = "Rechazar solicitud de plan")
    public ResponseEntity<ApiResponse<SolicitudPlanResponse>> rechazar(
            @PathVariable Long id,
            @RequestBody RevisionSolicitudRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success("Solicitud rechazada", solicitudPlanService.rechazar(id, request)));
    }
}