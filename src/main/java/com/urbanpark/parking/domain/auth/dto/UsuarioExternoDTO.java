package com.urbanpark.parking.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UsuarioExternoDTO {
    private Long id;

    @JsonProperty("correo")
    private String correo;

    private String nombres;
    private String apellidos;
    private String rol;
}