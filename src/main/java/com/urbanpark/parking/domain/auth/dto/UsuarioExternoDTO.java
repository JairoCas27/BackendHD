package com.urbanpark.parking.domain.auth.dto;

import lombok.Data;

@Data
public class UsuarioExternoDTO {
    private Long id;
    private String nombres;
    private String apellidos;
    private String correo;
    private String telefono;
    private String rol;
    private boolean activo;
    private Long condominioId;
}