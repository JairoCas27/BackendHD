package com.urbanpark.parking.domain.tenant.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class CondominioRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "La URL de la API es obligatoria")
    private String apiBaseUrl;

    @NotBlank(message = "El nombre del titular es obligatorio")
    private String titularNombre;

    @Email(message = "Email inválido")
    @NotBlank(message = "El email del titular es obligatorio")
    private String titularEmail;

    private String titularTelefono;

    @NotNull(message = "El plan es obligatorio")
    private UUID planId;
}