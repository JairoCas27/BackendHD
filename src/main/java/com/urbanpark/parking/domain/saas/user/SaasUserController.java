package com.urbanpark.parking.domain.saas.user;

import com.urbanpark.parking.domain.saas.user.dto.SaasUserRequestDTO;
import com.urbanpark.parking.domain.saas.user.dto.SaasUserResponseDTO;
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
@RequestMapping("/api/v1/saas/usuarios")
@RequiredArgsConstructor
public class SaasUserController {

    private final SaasUserService saasUserService;

    @GetMapping
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<ApiResponse<List<SaasUserResponseDTO>>> listar() {
        return ResponseEntity.ok(ApiResponse.success(saasUserService.listarTodos()));
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<ApiResponse<SaasUserResponseDTO>> crear(
            @RequestBody @Valid SaasUserRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Usuario creado", saasUserService.crear(request)));
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable UUID id) {
        saasUserService.desactivar(id);
        return ResponseEntity.ok(ApiResponse.success("Usuario desactivado", null));
    }

    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable UUID id) {
        saasUserService.activar(id);
        return ResponseEntity.ok(ApiResponse.success("Usuario activado", null));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable UUID id) {
        saasUserService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.success("Usuario eliminado correctamente", null));
    }
}