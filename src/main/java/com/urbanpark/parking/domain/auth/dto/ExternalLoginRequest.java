package com.urbanpark.parking.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class ExternalLoginRequest {

    @NotNull(message = "El condominioId es obligatorio")
    private UUID condominioId;

    @Email(message = "Email invalido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotBlank(message = "El password es obligatorio")
    private String password;
}