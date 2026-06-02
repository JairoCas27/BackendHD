package com.urbanpark.parking.domain.integration;

import com.urbanpark.parking.domain.integration.dto.ConexionStatusDTO;
import com.urbanpark.parking.domain.integration.dto.HealthExternoDTO;
import com.urbanpark.parking.domain.tenant.Condominio;
import com.urbanpark.parking.domain.tenant.CondominioRepository;
import com.urbanpark.parking.shared.dto.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/integraciones")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
public class IntegrationController {

    private final IntegrationClient integrationClient;
    private final CondominioRepository condominioRepository;

    @GetMapping("/{condominioId}/status")
    public ResponseEntity<ApiResponse<ConexionStatusDTO>> status(
            @PathVariable UUID condominioId) {

        Condominio condominio = condominioRepository.findById(condominioId)
                .orElseThrow(() -> new EntityNotFoundException("Condominio no encontrado"));

        HealthExternoDTO health = integrationClient.obtenerHealth(condominio);
        boolean disponible = health != null && "UP".equalsIgnoreCase(health.getStatus());

        ConexionStatusDTO statusDTO = ConexionStatusDTO.builder()
                .condominioId(condominioId)
                .apiUrl(condominio.getApiBaseUrl())
                .disponible(disponible)
                .healthStatus(health != null ? health.getStatus() : "DOWN")
                .build();

        return ResponseEntity.ok(ApiResponse.success(statusDTO));
    }
}