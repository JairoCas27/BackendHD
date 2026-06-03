package com.urbanpark.parking.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "El código OTP es obligatorio")
    private String otp;

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener mínimo 8 caracteres")
    private String nuevaPassword;
}