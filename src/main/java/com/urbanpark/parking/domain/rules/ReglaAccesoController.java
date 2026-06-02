package com.urbanpark.parking.domain.rules;

import com.urbanpark.parking.domain.rules.dto.ReglaRequest;
import com.urbanpark.parking.domain.rules.dto.ReglaResponse;
import com.urbanpark.parking.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reglas")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN_CONDOMINIO')")
public class ReglaAccesoController {

    private final ReglaAccesoService reglaService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReglaResponse>> crear(
            @RequestBody @Valid ReglaRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.success("Regla creada", reglaService.crear(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReglaResponse>>> listar(
            @RequestParam(required = false) boolean soloActivas) {
        List<ReglaResponse> reglas = soloActivas
                ? reglaService.listarActivas()
                : reglaService.listarTodas();
        return ResponseEntity.ok(ApiResponse.success(reglas));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReglaResponse>> buscar(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(reglaService.buscarPorId(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReglaResponse>> actualizar(
            @PathVariable UUID id,
            @RequestBody @Valid ReglaRequest request) {
        return ResponseEntity.ok(ApiResponse.success(reglaService.actualizar(id, request)));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable UUID id) {
        reglaService.activar(id);
        return ResponseEntity.ok(ApiResponse.success("Regla activada", null));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable UUID id) {
        reglaService.desactivar(id);
        return ResponseEntity.ok(ApiResponse.success("Regla desactivada", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable UUID id) {
        reglaService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.success("Regla eliminada", null));
    }
}