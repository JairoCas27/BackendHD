package com.urbanpark.parking.domain.condominios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CondominioRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "La razón social es obligatoria")
    private String razonSocial;

    @NotBlank(message = "El RUC es obligatorio")
    private String ruc;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @NotBlank(message = "El email del condominio es obligatorio")
    @Email(message = "Email inválido")
    private String emailCondominio;

    @NotBlank(message = "El teléfono del condominio es obligatorio")
    private String telefonoCondominio;

    @NotBlank(message = "La URL de la API es obligatoria")
    private String apiBaseUrl;
}