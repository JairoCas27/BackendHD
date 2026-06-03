package com.urbanpark.parking.domain.usuarios.dto;

import com.urbanpark.parking.shared.enums.RolSaas;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CrearUsuarioAdminRequest {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener mínimo 8 caracteres")
    private String password;

    @NotBlank(message = "Los nombres son obligatorios")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidos;

    @NotBlank(message = "El DNI es obligatorio")
    private String dni;

    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;

    @NotNull(message = "El rol es obligatorio")
    private RolSaas rol; // solo ADMIN o SUPERADMIN
}