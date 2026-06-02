package com.urbanpark.parking.domain.reports;

import com.urbanpark.parking.domain.reports.dto.*;
import com.urbanpark.parking.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'SUPERADMIN', 'ADMIN')")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/accesos")
    public ResponseEntity<ApiResponse<ReporteAccesosDTO>> accesos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fin) {
        return ResponseEntity.ok(ApiResponse.success(
                reportService.reporteAccesos(inicio, fin)));
    }

    @GetMapping("/ocupacion")
    public ResponseEntity<ApiResponse<ReporteOcupacionDTO>> ocupacion() {
        return ResponseEntity.ok(ApiResponse.success(
                reportService.reporteOcupacion()));
    }

    @GetMapping("/vehiculo/{vehiculoId}")
    public ResponseEntity<ApiResponse<ReporteVehiculoDTO>> vehiculo(
            @PathVariable UUID vehiculoId) {
        return ResponseEntity.ok(ApiResponse.success(
                reportService.reporteVehiculo(vehiculoId)));
    }

    @GetMapping("/tendencia")
    public ResponseEntity<ApiResponse<List<ReporteAccesosPorDiaDTO>>> tendencia(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fin) {
        return ResponseEntity.ok(ApiResponse.success(
                reportService.tendenciaDiaria(inicio, fin)));
    }

    @GetMapping("/denegados/top")
    public ResponseEntity<ApiResponse<List<TopPlacaDTO>>> topDenegados(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fin) {
        return ResponseEntity.ok(ApiResponse.success(
                reportService.topPlacasDenegadas(inicio, fin)));
    }
}