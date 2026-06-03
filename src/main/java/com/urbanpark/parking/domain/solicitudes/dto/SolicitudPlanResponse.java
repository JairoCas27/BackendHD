package com.urbanpark.parking.domain.solicitudes.dto;

import com.urbanpark.parking.shared.enums.EstadoSolicitud;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SolicitudPlanResponse {

    private Long id;
    private Long titularId;
    private String razonSocialTitular;
    private Long planId;
    private String planNombre;
    private EstadoSolicitud estado;
    private String motivoRechazo;
    private Long revisadoPorId;
    private String revisadoPorNombre;
    private LocalDateTime fechaRevision;
    private LocalDateTime fechaSolicitud;
}