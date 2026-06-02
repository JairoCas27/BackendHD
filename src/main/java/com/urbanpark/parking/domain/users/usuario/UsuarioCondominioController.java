package com.urbanpark.parking.domain.users.usuario;

import com.urbanpark.parking.domain.users.usuario.dto.UsuarioResponse;
import com.urbanpark.parking.shared.dto.ApiResponse;
import com.urbanpark.parking.shared.enums.RolParking;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioCondominioController {

    private final UsuarioCondominioService usuarioService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'SUPERADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<UsuarioResponse>>> listar(
            @RequestParam(required = false) RolParking rol) {

        List<UsuarioResponse> usuarios = rol != null
                ? usuarioService.listarPorRol(rol)
                : usuarioService.listarTodos();

        return ResponseEntity.ok(ApiResponse.success(usuarios));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'SUPERADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<UsuarioResponse>> buscar(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(usuarioService.buscarPorId(id)));
    }

    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMIN_CONDOMINIO')")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable UUID id) {
        usuarioService.activar(id);
        return ResponseEntity.ok(ApiResponse.success("Usuario activado", null));
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('ADMIN_CONDOMINIO')")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable UUID id) {
        usuarioService.desactivar(id);
        return ResponseEntity.ok(ApiResponse.success("Usuario desactivado", null));
    }
}