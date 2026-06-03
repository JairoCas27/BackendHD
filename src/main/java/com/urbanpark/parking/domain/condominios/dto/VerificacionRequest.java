package com.urbanpark.parking.domain.condominios.dto;

import lombok.Data;

@Data
public class VerificacionRequest {

    // Solo obligatorio si se rechaza
    private String motivoRechazo;
}