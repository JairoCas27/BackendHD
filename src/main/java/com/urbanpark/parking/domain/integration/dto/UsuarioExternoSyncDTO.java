package com.urbanpark.parking.domain.integration.dto;

import lombok.Data;

@Data
public class UsuarioExternoSyncDTO {
    private Long id;
    private String nombres;
    private String apellidos;
    private String correo;
    private String telefono;
    private String rol;
    private boolean activo;
    private Long condominioId;
    private String correoPendiente;
    private boolean correoVerificado;
}