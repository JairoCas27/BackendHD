package com.urbanpark.parking.domain.condominios;

import com.urbanpark.parking.domain.condominios.dto.CondominioRequest;
import com.urbanpark.parking.domain.condominios.dto.CondominioResponse;
import com.urbanpark.parking.domain.condominios.dto.VerificacionRequest;
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
@Tag(name = "Condominios", description = "Gestión de condominios")
@SecurityRequirement(name = "bearerAuth")
public class CondominioController {

    private final CondominioService condominioService;

    // ─── CLIENTE ──────────────────────────────────────────────

    @PostMapping("/api/v1/me/condominios")
    @Secured("ROLE_CLIENTE")
    @Operation(summary = "Registrar un condominio")
    public ResponseEntity<ApiResponse<CondominioResponse>> registrar(
            @Valid @RequestBody CondominioRequest request) {

        CondominioResponse response = condominioService.registrar(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Condominio registrado, pendiente de verificación por el administrador",
                        response));
    }

    @GetMapping("/api/v1/me/condominios")
    @Secured("ROLE_CLIENTE")
    @Operation(summary = "Listar mis condominios")
    public ResponseEntity<ApiResponse<List<CondominioResponse>>> listarMios() {

        return ResponseEntity.ok(
                ApiResponse.success(condominioService.listarMisCondominios()));
    }

    // ─── ADMIN ────────────────────────────────────────────────

    @GetMapping("/api/v1/admin/condominios")
    @Secured("ROLE_ADMIN")
    @Operation(summary = "Listar todos los condominios")
    public ResponseEntity<ApiResponse<List<CondominioResponse>>> listarTodos() {

        return ResponseEntity.ok(
                ApiResponse.success(condominioService.listarTodos()));
    }

    @GetMapping("/api/v1/admin/condominios/pendientes")
    @Secured("ROLE_ADMIN")
    @Operation(summary = "Listar condominios pendientes de verificación")
    public ResponseEntity<ApiResponse<List<CondominioResponse>>> listarPendientes() {

        return ResponseEntity.ok(
                ApiResponse.success(condominioService.listarPendientes()));
    }

    @PutMapping("/api/v1/admin/condominios/{id}/aprobar")
    @Secured("ROLE_ADMIN")
    @Operation(summary = "Aprobar condominio")
    public ResponseEntity<ApiResponse<CondominioResponse>> aprobar(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success("Condominio aprobado", condominioService.aprobar(id)));
    }

    @PutMapping("/api/v1/admin/condominios/{id}/rechazar")
    @Secured("ROLE_ADMIN")
    @Operation(summary = "Rechazar condominio")
    public ResponseEntity<ApiResponse<CondominioResponse>> rechazar(
            @PathVariable Long id,
            @RequestBody VerificacionRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success("Condominio rechazado", condominioService.rechazar(id, request)));
    }
}