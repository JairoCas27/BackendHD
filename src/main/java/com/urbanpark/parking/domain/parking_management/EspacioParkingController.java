package com.urbanpark.parking.domain.parking_management;

import com.urbanpark.parking.domain.parking_management.dto.EspacioRequest;
import com.urbanpark.parking.domain.parking_management.dto.EspacioResponse;
import com.urbanpark.parking.domain.parking_management.dto.OcupacionResponse;
import com.urbanpark.parking.shared.dto.ApiResponse;
import com.urbanpark.parking.shared.enums.EstadoEspacio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/espacios")
@RequiredArgsConstructor
public class EspacioParkingController {

    private final EspacioParkingService espacioService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN_CONDOMINIO')")
    public ResponseEntity<ApiResponse<EspacioResponse>> crear(
            @RequestBody @Valid EspacioRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.success("Espacio creado", espacioService.crear(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'AGENTE_SEGURIDAD')")
    public ResponseEntity<ApiResponse<List<EspacioResponse>>> listar(
            @RequestParam(required = false) EstadoEspacio estado,
            @RequestParam(required = false) String zona) {

        List<EspacioResponse> espacios;

        if (estado != null) {
            espacios = espacioService.listarPorEstado(estado);
        } else if (zona != null) {
            espacios = espacioService.listarPorZona(zona);
        } else {
            espacios = espacioService.listarTodos();
        }

        return ResponseEntity.ok(ApiResponse.success(espacios));
    }

    @GetMapping("/ocupacion")
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'AGENTE_SEGURIDAD')")
    public ResponseEntity<ApiResponse<OcupacionResponse>> ocupacion() {
        return ResponseEntity.ok(ApiResponse.success(espacioService.consultarOcupacion()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'AGENTE_SEGURIDAD')")
    public ResponseEntity<ApiResponse<EspacioResponse>> buscar(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(espacioService.buscarPorId(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_CONDOMINIO')")
    public ResponseEntity<ApiResponse<EspacioResponse>> actualizar(
            @PathVariable UUID id,
            @RequestBody @Valid EspacioRequest request) {
        return ResponseEntity.ok(ApiResponse.success(espacioService.actualizar(id, request)));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN_CONDOMINIO')")
    public ResponseEntity<ApiResponse<Void>> cambiarEstado(
            @PathVariable UUID id,
            @RequestParam EstadoEspacio estado) {
        espacioService.cambiarEstado(id, estado);
        return ResponseEntity.ok(ApiResponse.success("Estado actualizado", null));
    }
}