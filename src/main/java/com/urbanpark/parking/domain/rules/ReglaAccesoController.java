package com.urbanpark.parking.domain.rules;

import com.urbanpark.parking.domain.rules.dto.ReglaRequest;
import com.urbanpark.parking.domain.rules.dto.ReglaResponse;
import com.urbanpark.parking.domain.rules.dto.ValidacionRequest;
import com.urbanpark.parking.domain.rules.dto.ValidacionResult;
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
@RequiredArgsConstructor
@Tag(name = "Reglas de acceso", description = "Reglas configurables por condominio del cliente")
@SecurityRequirement(name = "bearerAuth")
public class ReglaAccesoController {

    private final ReglaAccesoService reglaAccesoService;

    @PostMapping("/api/v1/me/condominios/{condominioId}/reglas")
    @Secured("ROLE_CLIENTE")
    @Operation(
            summary = "Crear regla de acceso",
            description = "Crea una regla para un condominio propio. " +
                    "La configuración es JSON según el tipo (horario, límite de vehículos, etc.). " +
                    "Requiere JWT de cliente y que el condominio pertenezca al titular autenticado."
    )
    public ResponseEntity<ApiResponse<ReglaResponse>> crear(
            @PathVariable Long condominioId,
            @Valid @RequestBody ReglaRequest request) {

        ReglaResponse response = reglaAccesoService.crear(condominioId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Regla creada exitosamente", response));
    }

    @GetMapping("/api/v1/me/condominios/{condominioId}/reglas")
    @Secured("ROLE_CLIENTE")
    @Operation(
            summary = "Listar reglas del condominio",
            description = "Devuelve todas las reglas (activas e inactivas) del condominio indicado, " +
                    "siempre que el condominio sea del cliente autenticado."
    )
    public ResponseEntity<ApiResponse<List<ReglaResponse>>> listar(
            @PathVariable Long condominioId) {

        return ResponseEntity.ok(
                ApiResponse.success(reglaAccesoService.listarPorCondominio(condominioId)));
    }

    @GetMapping("/api/v1/me/condominios/{condominioId}/reglas/{reglaId}")
    @Secured("ROLE_CLIENTE")
    @Operation(
            summary = "Obtener regla por ID",
            description = "Consulta el detalle de una regla específica dentro de un condominio propio."
    )
    public ResponseEntity<ApiResponse<ReglaResponse>> obtener(
            @PathVariable Long condominioId,
            @PathVariable Long reglaId) {

        return ResponseEntity.ok(
                ApiResponse.success(reglaAccesoService.obtener(condominioId, reglaId)));
    }

    @PutMapping("/api/v1/me/condominios/{condominioId}/reglas/{reglaId}")
    @Secured("ROLE_CLIENTE")
    @Operation(
            summary = "Actualizar regla",
            description = "Modifica tipo, nombre, descripción, configuración JSON y estado activo de una regla existente."
    )
    public ResponseEntity<ApiResponse<ReglaResponse>> actualizar(
            @PathVariable Long condominioId,
            @PathVariable Long reglaId,
            @Valid @RequestBody ReglaRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Regla actualizada",
                        reglaAccesoService.actualizar(condominioId, reglaId, request)));
    }

    @DeleteMapping("/api/v1/me/condominios/{condominioId}/reglas/{reglaId}")
    @Secured("ROLE_CLIENTE")
    @Operation(
            summary = "Eliminar regla",
            description = "Elimina permanentemente una regla del condominio. No se puede deshacer."
    )
    public ResponseEntity<ApiResponse<Void>> eliminar(
            @PathVariable Long condominioId,
            @PathVariable Long reglaId) {

        reglaAccesoService.eliminar(condominioId, reglaId);
        return ResponseEntity.ok(ApiResponse.success("Regla eliminada", null));
    }

    @PostMapping("/api/v1/me/condominios/{condominioId}/reglas/validar")
    @Secured("ROLE_CLIENTE")
    @Operation(
            summary = "Validar acceso contra reglas activas",
            description = "Ejecuta el RuleEngine sobre las reglas activas del condominio. " +
                    "Útil para probar horarios, límites y permisos antes de integrar con el módulo de parking."
    )
    public ResponseEntity<ApiResponse<ValidacionResult>> validar(
            @PathVariable Long condominioId,
            @RequestBody ValidacionRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        reglaAccesoService.validar(condominioId, request)));
    }
}

