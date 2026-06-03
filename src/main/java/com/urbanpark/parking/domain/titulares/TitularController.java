package com.urbanpark.parking.domain.titulares;

import com.urbanpark.parking.domain.titulares.dto.TitularRequest;
import com.urbanpark.parking.domain.titulares.dto.TitularResponse;
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
@Tag(name = "Titulares", description = "Gestión de datos legales del titular")
@SecurityRequirement(name = "bearerAuth")
public class TitularController {

    private final TitularService titularService;

    // ─── Endpoints del propio CLIENTE ────────────────────────

    @PostMapping("/api/v1/me/titular")
    @Secured("ROLE_CLIENTE")
    @Operation(summary = "Completar datos del titular")
    public ResponseEntity<ApiResponse<TitularResponse>> completar(
            @Valid @RequestBody TitularRequest request) {

        TitularResponse response = titularService.completarDatos(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Datos del titular registrados", response));
    }

    @GetMapping("/api/v1/me/titular")
    @Secured("ROLE_CLIENTE")
    @Operation(summary = "Ver mis datos de titular")
    public ResponseEntity<ApiResponse<TitularResponse>> verMiTitular() {

        return ResponseEntity.ok(
                ApiResponse.success(titularService.obtenerMiTitular()));
    }

    @PutMapping("/api/v1/me/titular")
    @Secured("ROLE_CLIENTE")
    @Operation(summary = "Actualizar mis datos de titular")
    public ResponseEntity<ApiResponse<TitularResponse>> actualizar(
            @Valid @RequestBody TitularRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success("Datos actualizados", titularService.actualizar(request)));
    }

    // ─── Endpoints para ADMIN ─────────────────────────────────

    @GetMapping("/api/v1/admin/titulares")
    @Secured("ROLE_ADMIN")
    @Operation(summary = "Listar todos los titulares")
    public ResponseEntity<ApiResponse<List<TitularResponse>>> listar() {

        return ResponseEntity.ok(
                ApiResponse.success(titularService.listarTodos()));
    }

    @GetMapping("/api/v1/admin/titulares/{id}")
    @Secured("ROLE_ADMIN")
    @Operation(summary = "Ver titular por ID")
    public ResponseEntity<ApiResponse<TitularResponse>> obtener(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(titularService.obtenerPorId(id)));
    }
}