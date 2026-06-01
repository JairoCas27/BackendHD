package com.urbanpark.parking.modules.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ControlAccesoDto {
    private String placa;
    private String apartamentoId;
    private String tipoAcceso; // "INGRESO" o "SALIDA"
}