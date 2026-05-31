package com.urbanpark.parking.modules.parking;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.urbanpark.parking.modules.parking.dto.EstacionamientoCreateDto;

@RestController
@RequestMapping("/api/estacionamientos")
public class EstacionamientoController {

    private final EstacionamientoService service;

    public EstacionamientoController(EstacionamientoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<EstacionamientoEntity> crear(@RequestBody EstacionamientoCreateDto dto) {
        return ResponseEntity.ok(service.crearEstacionamiento(dto));
    }

    @GetMapping
    public ResponseEntity<List<EstacionamientoEntity>> listar() {
        return ResponseEntity.ok(service.listarPorCondominio());
    }

    @PutMapping("/{id}/asignar-apartamento/{apartamentoId}")
    public ResponseEntity<EstacionamientoEntity> asignarApartamento(
            @PathVariable String id, 
            @PathVariable String apartamentoId) {
        return ResponseEntity.ok(service.asignarApartamento(id, apartamentoId));
    }

    // UNIFICACIÓN ESTRATÉGICA: Usamos la ruta exacta que quiere el grupo para reportes, 
    // pero con tu lógica blindada libre de brechas de seguridad.
    @GetMapping("/api/v1/reports/occupation")
    public ResponseEntity<com.urbanpark.parking.modules.parking.dto.ReporteOperativoDto> obtenerReportes() {
        return ResponseEntity.ok(service.generarReporteCompleto());
    }
}