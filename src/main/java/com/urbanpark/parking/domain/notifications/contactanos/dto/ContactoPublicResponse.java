package com.urbanpark.parking.domain.notifications.contactanos.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ContactoPublicResponse {
    private String codigoSeguimiento;
    private String correo;
    private LocalDateTime fechaEnvio;
    private String mensaje;
    private String nombre;
}