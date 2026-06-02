package com.urbanpark.parking.domain.tenant;

import com.urbanpark.parking.domain.tenant.dto.CondominioRequest;
import com.urbanpark.parking.domain.tenant.dto.CondominioResponse;
import com.urbanpark.parking.shared.dto.ApiResponse;
import com.urbanpark.parking.shared.enums.EstadoCondominio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/condominios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPERADMIN')")
public class CondominioController {

    private final CondominioService condominioService;

    @PostMapping
    public ResponseEntity<ApiResponse<CondominioResponse>> crear(
            @RequestBody @Valid CondominioRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.success("Condominio creado", condominioService.crear(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CondominioResponse>>> listar() {
        return ResponseEntity.ok(ApiResponse.success(condominioService.listarTodos()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CondominioResponse>> buscar(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(condominioService.buscarPorId(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CondominioResponse>> actualizar(
            @PathVariable UUID id,
            @RequestBody @Valid CondominioRequest request) {
        return ResponseEntity.ok(ApiResponse.success(condominioService.actualizar(id, request)));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<Void>> cambiarEstado(
            @PathVariable UUID id,
            @RequestParam EstadoCondominio estado) {
        condominioService.cambiarEstado(id, estado);
        return ResponseEntity.ok(ApiResponse.success("Estado actualizado", null));
    }
}