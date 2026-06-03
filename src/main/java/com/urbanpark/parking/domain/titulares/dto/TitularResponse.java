package com.urbanpark.parking.domain.titulares.dto;

import com.urbanpark.parking.shared.enums.EstadoPlan;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TitularResponse {

    private Long id;
    private Long usuarioSaasId;
    private String nombreCompletoUsuario;
    private String emailUsuario;
    private String razonSocial;
    private String ruc;
    private String direccionFiscal;
    private String representanteLegal;
    private Long planId;
    private String planNombre;
    private EstadoPlan estadoPlan;
    private LocalDateTime fechaAsignacionPlan;
}