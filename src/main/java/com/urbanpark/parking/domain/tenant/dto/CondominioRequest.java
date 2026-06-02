package com.urbanpark.parking.domain.tenant.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

    @NotBlank(message = "El DNI del titular es obligatorio")
    @Pattern(regexp = "\\d{8}", message = "El DNI debe tener 8 digitos")
    private String titularDni;

    @Email(message = "Email invalido")
    @NotBlank(message = "El email del titular es obligatorio")
    private String titularEmail;

    private String titularTelefono;

    @NotNull(message = "El plan es obligatorio")
    private UUID planId;
}