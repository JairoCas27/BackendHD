package com.urbanpark.parking.modules.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {
    private String token;
    private String tipoToken; 
    private String nombreUsuario;
    private String rol;
    private String tenantId;
}