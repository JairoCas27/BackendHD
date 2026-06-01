package com.urbanpark.parking.modules.security;

import com.urbanpark.parking.modules.security.dto.ControlAccesoDto;
import com.urbanpark.parking.modules.security.dto.RespuestaAccesoDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/porteria")
public class PorteriaController {

    private final ReglasAccesoService reglasService;

    public PorteriaController(ReglasAccesoService reglasService) {
        this.reglasService = reglasService;
    }

    @PostMapping("/evaluar-reglas")
    public ResponseEntity<RespuestaAccesoDto> evaluarReglas(@RequestBody ControlAccesoDto solicitud) {
        return ResponseEntity.ok(reglasService.evaluarAcceso(solicitud));
    }
}