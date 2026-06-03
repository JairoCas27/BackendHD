package com.urbanpark.parking.domain.titulares.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TitularRequest {

    @NotBlank(message = "La razón social es obligatoria")
    private String razonSocial;

    @NotBlank(message = "El RUC es obligatorio")
    private String ruc;

    @NotBlank(message = "La dirección fiscal es obligatoria")
    private String direccionFiscal;

    @NotBlank(message = "El representante legal es obligatorio")
    private String representanteLegal;
}