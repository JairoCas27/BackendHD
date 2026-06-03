package com.urbanpark.parking.domain.notifications.contactanos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ContactoRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debe proporcionar un formato de correo válido")
    private String correo;

    @NotBlank(message = "El mensaje no puede estar vacío")
    @Size(max = 1000, message = "El mensaje no puede superar los 1000 caracteres")
    private String mensaje;
}