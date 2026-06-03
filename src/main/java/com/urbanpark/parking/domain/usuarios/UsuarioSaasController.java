package com.urbanpark.parking.domain.usuarios;

import com.urbanpark.parking.domain.usuarios.dto.*;
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
@RequestMapping("/api/v1/admin/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios Internos", description = "Gestión de ADMIN y SUPERADMIN")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioSaasController {

    private final UsuarioSaasService usuarioSaasService;

    @PostMapping
    @Secured("ROLE_SUPERADMIN")
    @Operation(summary = "Crear ADMIN o SUPERADMIN")
    public ResponseEntity<ApiResponse<UsuarioSaasResponse>> crear(
            @Valid @RequestBody CrearUsuarioAdminRequest request) {

        UsuarioSaasResponse response = usuarioSaasService.crearUsuarioInterno(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Usuario creado exitosamente", response));
    }

    @GetMapping
    @Secured("ROLE_SUPERADMIN")
    @Operation(summary = "Listar todos los ADMIN y SUPERADMIN")
    public ResponseEntity<ApiResponse<List<UsuarioSaasResponse>>> listar() {

        return ResponseEntity.ok(
                ApiResponse.success(usuarioSaasService.listarUsuariosInternos()));
    }

    @GetMapping("/clientes")
    @Secured("ROLE_ADMIN")
    @Operation(summary = "Listar todos los clientes")
    public ResponseEntity<ApiResponse<List<UsuarioSaasResponse>>> listarClientes() {

        return ResponseEntity.ok(
                ApiResponse.success(usuarioSaasService.listarClientes()));
    }

    @GetMapping("/{id}")
    @Secured("ROLE_ADMIN")
    @Operation(summary = "Obtener usuario por ID")
    public ResponseEntity<ApiResponse<UsuarioSaasResponse>> obtener(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(usuarioSaasService.obtenerPorId(id)));
    }

    @PutMapping("/{id}/estado")
    @Secured("ROLE_ADMIN")
    @Operation(summary = "Actualizar estado de un usuario")
    public ResponseEntity<ApiResponse<UsuarioSaasResponse>> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarEstadoRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success("Estado actualizado", usuarioSaasService.actualizarEstado(id, request)));
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_SUPERADMIN")
    @Operation(summary = "Eliminar usuario (no aplica al SUPERADMIN base)")
    public ResponseEntity<ApiResponse<Void>> eliminar(
            @PathVariable Long id) {

        usuarioSaasService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.success("Usuario eliminado", null));
    }
}