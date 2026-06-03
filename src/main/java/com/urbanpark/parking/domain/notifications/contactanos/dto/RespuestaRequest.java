package com.urbanpark.parking.domain.notifications.contactanos.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RespuestaRequest {
    @NotBlank(message = "La respuesta no puede estar vacía")
    private String respuesta;
}