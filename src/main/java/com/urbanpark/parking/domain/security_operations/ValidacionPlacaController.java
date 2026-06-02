package com.urbanpark.parking.domain.security_operations;

import com.urbanpark.parking.domain.users.vehiculo.VehiculoService;
import com.urbanpark.parking.domain.users.vehiculo.dto.VehiculoResponse;
import com.urbanpark.parking.domain.users.visitante.VisitanteRepository;
import com.urbanpark.parking.domain.tenant.TenantContext;
import com.urbanpark.parking.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/validar-placa")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('AGENTE_SEGURIDAD', 'ADMIN_CONDOMINIO')")
public class ValidacionPlacaController {

    private final VehiculoService vehiculoService;
    private final VisitanteRepository visitanteRepository;

    @GetMapping("/{placa}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validar(
            @PathVariable String placa) {

        try {
            VehiculoResponse vehiculo = vehiculoService.buscarPorPlaca(placa);

            boolean visitanteActivo = visitanteRepository
                    .findVisitanteActivoPorPlaca(
                            TenantContext.getTenantId(),
                            placa.toUpperCase(),
                            LocalDateTime.now()
                    )
                    .isPresent();

            Map<String, Object> resultado = Map.of(
                    "encontrado", true,
                    "vehiculo", vehiculo,
                    "visitanteAutorizado", visitanteActivo,
                    "activo", vehiculo.isActivo()
            );

            return ResponseEntity.ok(ApiResponse.success(resultado));

        } catch (Exception e) {
            Map<String, Object> noEncontrado = Map.of(
                    "encontrado", false,
                    "placa", placa.toUpperCase(),
                    "mensaje", "Placa no registrada en este condominio"
            );
            return ResponseEntity.ok(ApiResponse.success(noEncontrado));
        }
    }
}