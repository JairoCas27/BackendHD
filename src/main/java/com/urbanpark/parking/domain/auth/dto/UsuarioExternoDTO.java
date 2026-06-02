package com.urbanpark.parking.domain.auth.dto;

import lombok.Data;

@Data
public class UsuarioExternoDTO {
    private Long id;
    private String correo;
    private String nombres;
    private String apellidos;
    private String rol; // ADMINISTRADOR_CONDOMINIO | AGENTE_SEGURIDAD | PROPIETARIO
}