package com.urbanpark.parking.domain.integration;

import com.urbanpark.parking.domain.integration.dto.ConexionStatusDTO;
import com.urbanpark.parking.domain.integration.dto.HealthExternoDTO;
import com.urbanpark.parking.domain.integration.dto.SyncResultDTO;
import com.urbanpark.parking.domain.tenant.Condominio;
import com.urbanpark.parking.domain.tenant.CondominioRepository;
import com.urbanpark.parking.shared.dto.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/integraciones")
@RequiredArgsConstructor
public class IntegrationController {

    private final SyncService syncService;
    private final IntegrationClient integrationClient;
    private final CondominioRepository condominioRepository;
    private final SyncLogRepository syncLogRepository;

    @PostMapping("/{tenantId}/sync")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<SyncResultDTO>> sync(@PathVariable UUID tenantId) {
        Condominio condominio = findCondominio(tenantId);
        SyncResultDTO resultado = syncService.sincronizarCondominio(condominio);
        return ResponseEntity.ok(ApiResponse.success("Sincronización completada", resultado));
    }

    @GetMapping("/{tenantId}/status")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<ConexionStatusDTO>> status(@PathVariable UUID tenantId) {
        Condominio condominio = findCondominio(tenantId);

        HealthExternoDTO health = integrationClient.obtenerHealth(condominio);
        boolean disponible = health != null && "UP".equalsIgnoreCase(health.getStatus());

        var ultimoSync = syncLogRepository
                .findTopByTenantIdAndTipoOrderByCreatedAtDesc(tenantId, "USUARIOS")
                .orElse(null);

        ConexionStatusDTO statusDTO = ConexionStatusDTO.builder()
                .tenantId(tenantId)
                .apiUrl(condominio.getApiBaseUrl())
                .disponible(disponible)
                .healthStatus(health != null ? health.getStatus() : "DOWN")
                .ultimaSync(ultimoSync != null ? ultimoSync.getCreatedAt() : null)
                .estadoUltimaSync(ultimoSync != null ? ultimoSync.getEstado().name() : "NUNCA")
                .build();

        return ResponseEntity.ok(ApiResponse.success(statusDTO));
    }

    @GetMapping("/{tenantId}/sync/logs")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<SyncLog>>> logs(
            @PathVariable UUID tenantId,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                syncLogRepository.findAllByTenantIdOrderByCreatedAtDesc(tenantId, pageable)));
    }

    private Condominio findCondominio(UUID tenantId) {
        return condominioRepository.findById(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Condominio no encontrado"));
    }
}