package main.java.com.urbanpark.parking.user.controller;
 
import com.urbanpark.parking.tenant.TenantContext;
import com.urbanpark.parking.user.dto.request.UpdateUserRequest;
import com.urbanpark.parking.user.dto.response.UserResponse;
import com.urbanpark.parking.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
import java.util.Map;
 
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Gestión de usuarios del condominio (RF-18)")
public class UserController {
 
    private final UserService userService;
 
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'AGENTE_SEGURIDAD')")
    @Operation(summary = "Listar todos los usuarios del condominio")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        String tenantId = TenantContext.getCurrentTenant();
        return ResponseEntity.ok(userService.getAllUsers(tenantId));
    }
 
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'AGENTE_SEGURIDAD')")
    @Operation(summary = "Obtener usuario por ID interno")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        String tenantId = TenantContext.getCurrentTenant();
        return ResponseEntity.ok(userService.getUserById(id, tenantId));
    }
 
    @GetMapping("/external/{externalId}")
    @PreAuthorize("hasRole('ADMIN_CONDOMINIO')")
    @Operation(summary = "Obtener usuario por ID externo del condominio")
    public ResponseEntity<UserResponse> getUserByExternalId(@PathVariable String externalId) {
        String tenantId = TenantContext.getCurrentTenant();
        return ResponseEntity.ok(userService.getUserByExternalId(externalId, tenantId));
    }
 
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_CONDOMINIO')")
    @Operation(summary = "Actualizar datos del usuario")
    public ResponseEntity<UserResponse> updateUser(
        @PathVariable Long id,
        @RequestBody @Valid UpdateUserRequest request
    ) {
        String tenantId = TenantContext.getCurrentTenant();
        return ResponseEntity.ok(userService.updateUser(id, request, tenantId));
    }
 
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN_CONDOMINIO')")
    @Operation(summary = "Desactivar un usuario del condominio")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        String tenantId = TenantContext.getCurrentTenant();
        userService.deactivateUser(id, tenantId);
        return ResponseEntity.noContent().build();
    }
 
    /**
     * Endpoint de sincronización manual con el sistema externo del condominio.
     * El JWT externo se pasa en el header Authorization como Bearer.
     * (RF-05)
     */
    @PostMapping("/sync")
    @PreAuthorize("hasRole('ADMIN_CONDOMINIO')")
    @Operation(
        summary = "Sincronizar usuarios desde API del condominio",
        description = "Requiere el JWT del sistema externo en el header Authorization."
    )
    public ResponseEntity<Map<String, Object>> syncUsers(
        @Parameter(description = "JWT del condominio externo (Bearer token)")
        @RequestHeader("Authorization") String authHeader
    ) {
        String tenantId = TenantContext.getCurrentTenant();
        String externalJwt = extractJwt(authHeader);
        int count = userService.syncUsersFromCondominium(tenantId, externalJwt);
        return ResponseEntity.ok(Map.of(
            "tenant", tenantId,
            "synced", count,
            "message", "Sincronización completada"
        ));
    }
 
    private String extractJwt(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new IllegalArgumentException("Header Authorization inválido. Use formato: Bearer <token>");
    }
}