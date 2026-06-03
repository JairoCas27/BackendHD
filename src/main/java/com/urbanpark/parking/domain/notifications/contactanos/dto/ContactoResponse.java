package com.urbanpark.parking.domain.notifications.contactanos.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContactoResponse {
    private Long id;
    private String nombre;
    private String correo;
    private String mensaje;
    private String codigoSeguimiento;
    private LocalDateTime fechaEnvio;
    private boolean respondido;
    private String respuesta;
    private LocalDateTime fechaRespuesta;
    private Long usuarioRespuestaId;
}