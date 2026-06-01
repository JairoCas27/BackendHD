package com.urbanpark.parking.modules.parking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EstacionamientoCreateDto {
    private String codigo; // Ej: "A-101"
    private String sector; // Ej: "Sótano 1"
}