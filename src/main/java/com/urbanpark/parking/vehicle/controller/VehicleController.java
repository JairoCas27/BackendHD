package com.urbanpark.parking.vehicle.controller;
 
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.urbanpark.parking.tenant.TenantContext;
import com.urbanpark.parking.vehicle.dto.request.CreateVehicleRequest;
import com.urbanpark.parking.vehicle.dto.request.UpdateVehicleRequest;
import com.urbanpark.parking.vehicle.dto.response.VehicleResponse;
import com.urbanpark.parking.vehicle.service.VehicleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
 
@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@Tag(name = "Vehicles", description = "Gestión de vehículos por usuario (RF-19)")
public class VehicleController {
 
    private final VehicleService vehicleService;
 
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'AGENTE_SEGURIDAD')")
    @Operation(summary = "Listar todos los vehículos del condominio")
    public ResponseEntity<List<VehicleResponse>> getAllVehicles() {
        String tenantId = TenantContext.getCurrentTenant();
        return ResponseEntity.ok(vehicleService.getAllVehicles(tenantId));
    }
 
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener vehículo por ID")
    public ResponseEntity<VehicleResponse> getVehicleById(@PathVariable Long id) {
        String tenantId = TenantContext.getCurrentTenant();
        return ResponseEntity.ok(vehicleService.getVehicleById(id, tenantId));
    }
 
    @GetMapping("/plate/{plate}")
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'AGENTE_SEGURIDAD')")
    @Operation(summary = "Buscar vehículo por placa")
    public ResponseEntity<VehicleResponse> getVehicleByPlate(@PathVariable String plate) {
        String tenantId = TenantContext.getCurrentTenant();
        return ResponseEntity.ok(vehicleService.getVehicleByPlate(plate, tenantId));
    }
 
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'AGENTE_SEGURIDAD', 'PROPIETARIO')")
    @Operation(summary = "Listar vehículos de un usuario")
    public ResponseEntity<List<VehicleResponse>> getVehiclesByUser(@PathVariable Long userId) {
        String tenantId = TenantContext.getCurrentTenant();
        return ResponseEntity.ok(vehicleService.getVehiclesByUser(userId, tenantId));
    }
 
    @PostMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'PROPIETARIO')")
    @Operation(summary = "Registrar un nuevo vehículo para un usuario")
    public ResponseEntity<VehicleResponse> registerVehicle(
        @PathVariable Long userId,
        @RequestBody @Valid CreateVehicleRequest request
    ) {
        String tenantId = TenantContext.getCurrentTenant();
        VehicleResponse response = vehicleService.registerVehicle(userId, request, tenantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
 
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'PROPIETARIO')")
    @Operation(summary = "Actualizar datos del vehículo")
    public ResponseEntity<VehicleResponse> updateVehicle(
        @PathVariable Long id,
        @RequestBody @Valid UpdateVehicleRequest request
    ) {
        String tenantId = TenantContext.getCurrentTenant();
        return ResponseEntity.ok(vehicleService.updateVehicle(id, request, tenantId));
    }
 
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'PROPIETARIO')")
    @Operation(summary = "Eliminar vehículo del registro")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        String tenantId = TenantContext.getCurrentTenant();
        vehicleService.deleteVehicle(id, tenantId);
        return ResponseEntity.noContent().build();
    }
 
    @PostMapping("/sync")
    @PreAuthorize("hasRole('ADMIN_CONDOMINIO')")
    @Operation(summary = "Sincronizar vehículos desde API del condominio")
    public ResponseEntity<Map<String, Object>> syncVehicles(
        @RequestHeader("Authorization") String authHeader
    ) {
        String tenantId = TenantContext.getCurrentTenant();
        String externalJwt = extractJwt(authHeader);
        int count = vehicleService.syncVehiclesFromCondominium(tenantId, externalJwt);
        return ResponseEntity.ok(Map.of(
            "tenant", tenantId,
            "synced", count,
            "message", "Sincronización de vehículos completada"
        ));
    }
 
    private String extractJwt(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new IllegalArgumentException("Header Authorization inválido.");
    }
}