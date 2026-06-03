package com.urbanpark.parking.domain.condominios.dto;

import com.urbanpark.parking.shared.enums.EstadoCondominio;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CondominioResponse {

    private Long id;
    private Long titularId;
    private String razonSocialTitular;
    private String nombre;
    private String slug;
    private String razonSocial;
    private String ruc;
    private String direccion;
    private String emailCondominio;
    private String telefonoCondominio;
    private String apiBaseUrl;
    private EstadoCondominio estado;
    private Long verificadoPorId;
    private String verificadoPorNombre;
    private String motivoRechazo;
    private LocalDateTime fechaVerificacion;
    private LocalDateTime fechaRegistro;
}