package main.java.com.urbanpark.parking.visitor.controller;
 
import com.urbanpark.parking.tenant.TenantContext;
import com.urbanpark.parking.visitor.dto.request.CreateVisitorRequest;
import com.urbanpark.parking.visitor.dto.response.VisitorResponse;
import com.urbanpark.parking.visitor.service.VisitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
 
@RestController
@RequestMapping("/api/visitors")
@RequiredArgsConstructor
@Tag(name = "Visitors", description = "Gestión de visitantes temporales (RF-20)")
public class VisitorController {
 
    private final VisitorService visitorService;
 
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'AGENTE_SEGURIDAD')")
    @Operation(summary = "Listar todos los visitantes actualmente válidos en el condominio")
    public ResponseEntity<List<VisitorResponse>> getActiveVisitors() {
        String tenantId = TenantContext.getCurrentTenant();
        return ResponseEntity.ok(visitorService.getActiveVisitors(tenantId));
    }
 
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'PROPIETARIO')")
    @Operation(summary = "Listar visitantes autorizados por un usuario")
    public ResponseEntity<List<VisitorResponse>> getVisitorsByUser(@PathVariable Long userId) {
        String tenantId = TenantContext.getCurrentTenant();
        return ResponseEntity.ok(visitorService.getVisitorsByUser(userId, tenantId));
    }
 
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener visitante por ID")
    public ResponseEntity<VisitorResponse> getVisitorById(@PathVariable Long id) {
        String tenantId = TenantContext.getCurrentTenant();
        return ResponseEntity.ok(visitorService.getVisitorById(id, tenantId));
    }
 
    @GetMapping("/by-plate/{plate}")
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'AGENTE_SEGURIDAD')")
    @Operation(
        summary = "Buscar visitantes activos por placa",
        description = "Usado por el módulo de control de acceso para validar ingreso de visitantes."
    )
    public ResponseEntity<List<VisitorResponse>> getActiveVisitorsByPlate(@PathVariable String plate) {
        String tenantId = TenantContext.getCurrentTenant();
        return ResponseEntity.ok(visitorService.getActiveVisitorsByPlate(plate, tenantId));
    }
 
    @PostMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('PROPIETARIO', 'ADMIN_CONDOMINIO')")
    @Operation(summary = "Registrar un visitante para un usuario")
    public ResponseEntity<VisitorResponse> registerVisitor(
        @PathVariable Long userId,
        @RequestBody @Valid CreateVisitorRequest request
    ) {
        String tenantId = TenantContext.getCurrentTenant();
        VisitorResponse response = visitorService.registerVisitor(userId, request, tenantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
 
    @PatchMapping("/{id}/revoke")
    @PreAuthorize("hasAnyRole('PROPIETARIO', 'ADMIN_CONDOMINIO')")
    @Operation(summary = "Revocar un visitante (desactivar antes de su expiración)")
    public ResponseEntity<Void> revokeVisitor(@PathVariable Long id) {
        String tenantId = TenantContext.getCurrentTenant();
        visitorService.revokeVisitor(id, tenantId);
        return ResponseEntity.noContent().build();
    }
}