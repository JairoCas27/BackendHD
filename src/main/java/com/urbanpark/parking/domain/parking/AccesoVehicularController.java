package com.urbanpark.parking.domain.parking;

import com.urbanpark.parking.domain.parking.dto.AccesoResponse;
import com.urbanpark.parking.domain.parking.dto.RegistroEntradaRequest;
import com.urbanpark.parking.domain.parking.dto.RegistroSalidaRequest;
import com.urbanpark.parking.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accesos")
@RequiredArgsConstructor
public class AccesoVehicularController {

    private final AccesoVehicularService accesoService;

    @PostMapping("/entrada")
    @PreAuthorize("hasAnyRole('AGENTE_SEGURIDAD', 'ADMIN_CONDOMINIO')")
    public ResponseEntity<ApiResponse<AccesoResponse>> registrarEntrada(
            @RequestBody @Valid RegistroEntradaRequest request) {
        AccesoResponse response = accesoService.registrarEntrada(request);
        String mensaje = response.isAutorizado() ? "Entrada registrada" : "Acceso denegado";
        return ResponseEntity.status(response.isAutorizado() ? 201 : 403)
                .body(ApiResponse.success(mensaje, response));
    }

    @PostMapping("/salida")
    @PreAuthorize("hasAnyRole('AGENTE_SEGURIDAD', 'ADMIN_CONDOMINIO')")
    public ResponseEntity<ApiResponse<AccesoResponse>> registrarSalida(
            @RequestBody @Valid RegistroSalidaRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Salida registrada", accesoService.registrarSalida(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'AGENTE_SEGURIDAD')")
    public ResponseEntity<ApiResponse<List<AccesoResponse>>> listar(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {

        List<AccesoResponse> accesos = (inicio != null && fin != null)
                ? accesoService.listarPorRango(inicio, fin)
                : accesoService.listarTodos();

        return ResponseEntity.ok(ApiResponse.success(accesos));
    }

    @GetMapping("/vehiculo/{vehiculoId}")
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'PROPIETARIO', 'AGENTE_SEGURIDAD')")
    public ResponseEntity<ApiResponse<List<AccesoResponse>>> listarPorVehiculo(
            @PathVariable UUID vehiculoId) {
        return ResponseEntity.ok(
                ApiResponse.success(accesoService.listarPorVehiculo(vehiculoId)));
    }
}