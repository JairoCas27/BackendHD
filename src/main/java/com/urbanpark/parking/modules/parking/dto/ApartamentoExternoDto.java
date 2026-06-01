package com.urbanpark.parking.modules.parking.dto;

import lombok.Data;

@Data
public class ApartamentoExternoDto {
    private String id;
    private String numero;
    private int maxVehiculosPermitidos; // El campo clave para el motor de reglas
}