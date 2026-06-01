package com.urbanpark.parking.modules.access.controller;

import com.urbanpark.parking.modules.access.dto.request.RegisterAccessRequest;
import com.urbanpark.parking.modules.access.dto.response.ParkingAccessResponse;
import com.urbanpark.parking.modules.access.service.ParkingAccessService;
import com.urbanpark.parking.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/access")
@RequiredArgsConstructor
public class AccessController {

    private final ParkingAccessService accessService;

    /**
     * Registrar un acceso (manual). Solo AGENTE_SEGURIDAD puede registrar manual.
     */
    @PostMapping("/register")
    @PreAuthorize("hasRole('AGENTE_SEGURIDAD')")
    public ResponseEntity<ParkingAccessResponse> registerAccess(@Valid @RequestBody RegisterAccessRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId(); // Obtiene el ID del usuario autenticado
        ParkingAccessResponse response = accessService.registerAccess(request, "MANUAL", currentUserId);
        return ResponseEntity.ok(response);
    }

    /**
     * Registrar un acceso automático (simulado por detección de placa). Cualquier autenticado podría llamarlo.
     * En producción probablemente lo haría un microservicio de lectores de placas.
     */
    @PostMapping("/auto")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ParkingAccessResponse> autoAccess(@Valid @RequestBody RegisterAccessRequest request) {
        ParkingAccessResponse response = accessService.registerAccess(request, "AUTOMATIC", null);
        return ResponseEntity.ok(response);
    }

    /**
     * Listar todos los accesos del condominio (para admin/seguridad).
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'AGENTE_SEGURIDAD')")
    public ResponseEntity<List<ParkingAccessResponse>> getAllAccesses() {
        return ResponseEntity.ok(accessService.getAllAccesses());
    }

    /**
     * Accesos paginados.
     */
    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'AGENTE_SEGURIDAD')")
    public ResponseEntity<Page<ParkingAccessResponse>> getAccessesPage(
            @PageableDefault(size = 20, sort = "accessTimestamp", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(accessService.getAccessesPaginated(pageable));
    }

    /**
     * Historial por placa.
     */
    @GetMapping("/vehicle/{plate}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ParkingAccessResponse>> getAccessesByPlate(@PathVariable String plate) {
        return ResponseEntity.ok(accessService.getAccessesByPlate(plate));
    }

    /**
     * Historial por rango de fechas.
     */
    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'AGENTE_SEGURIDAD')")
    public ResponseEntity<List<ParkingAccessResponse>> getAccessesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(accessService.getAccessesByDateRange(from, to));
    }

    /**
     * Historial de accesos de un propietario específico (por su ID interno).
     */
    @GetMapping("/owner/{ownerId}")
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'AGENTE_SEGURIDAD', 'PROPIETARIO')")
    public ResponseEntity<List<ParkingAccessResponse>> getAccessesByOwner(@PathVariable Long ownerId) {
        // Si es PROPIETARIO, solo puede ver sus propios accesos
        if (SecurityUtils.hasRole("PROPIETARIO") && !ownerId.equals(SecurityUtils.getCurrentUserId())) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(accessService.getAccessesByOwner(ownerId));
    }

    /**
     * Verificar si un vehículo está dentro.
     */
    @GetMapping("/inside/{plate}")
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'AGENTE_SEGURIDAD')")
    public ResponseEntity<Boolean> isVehicleInside(@PathVariable String plate) {
        return ResponseEntity.ok(accessService.isVehicleCurrentlyInside(plate));
    }
}