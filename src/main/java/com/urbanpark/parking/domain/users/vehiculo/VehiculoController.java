package com.urbanpark.parking.domain.users.vehiculo;

import com.urbanpark.parking.domain.users.vehiculo.dto.VehiculoRequest;
import com.urbanpark.parking.domain.users.vehiculo.dto.VehiculoResponse;
import com.urbanpark.parking.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vehiculos")
@RequiredArgsConstructor
public class VehiculoController {

    private final VehiculoService vehiculoService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'PROPIETARIO')")
    public ResponseEntity<ApiResponse<VehiculoResponse>> crear(
            @RequestBody @Valid VehiculoRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.success("Vehículo registrado", vehiculoService.crear(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'AGENTE_SEGURIDAD')")
    public ResponseEntity<ApiResponse<List<VehiculoResponse>>> listar() {
        return ResponseEntity.ok(ApiResponse.success(vehiculoService.listarTodos()));
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'PROPIETARIO')")
    public ResponseEntity<ApiResponse<List<VehiculoResponse>>> listarPorUsuario(
            @PathVariable UUID usuarioId) {
        return ResponseEntity.ok(ApiResponse.success(vehiculoService.listarPorUsuario(usuarioId)));
    }

    @GetMapping("/placa/{placa}")
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'AGENTE_SEGURIDAD')")
    public ResponseEntity<ApiResponse<VehiculoResponse>> buscarPorPlaca(
            @PathVariable String placa) {
        return ResponseEntity.ok(ApiResponse.success(vehiculoService.buscarPorPlaca(placa)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'PROPIETARIO', 'AGENTE_SEGURIDAD')")
    public ResponseEntity<ApiResponse<VehiculoResponse>> buscar(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(vehiculoService.buscarPorId(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'PROPIETARIO')")
    public ResponseEntity<ApiResponse<VehiculoResponse>> actualizar(
            @PathVariable UUID id,
            @RequestBody @Valid VehiculoRequest request) {
        return ResponseEntity.ok(ApiResponse.success(vehiculoService.actualizar(id, request)));
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('ADMIN_CONDOMINIO')")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable UUID id) {
        vehiculoService.desactivar(id);
        return ResponseEntity.ok(ApiResponse.success("Vehículo desactivado", null));
    }
}