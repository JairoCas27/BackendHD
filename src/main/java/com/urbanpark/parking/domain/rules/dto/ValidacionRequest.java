package com.urbanpark.parking.domain.rules.dto;

import lombok.Data;

import java.time.LocalTime;

@Data
public class ValidacionRequest {

    /** Hora a evaluar (si no se envía, usa la hora actual del servidor). */
    private LocalTime horaAcceso;

    /** Vehículos activos en el condominio (para LIMITE_VEHICULOS). */
    private Integer vehiculosActivos;

    /** Rol del usuario que intenta acceder (para TIPO_USUARIO). */
    private String rolUsuario;

    /** Si el acceso es de visitante (para VISITANTE_PERMITIDO). */
    private Boolean esVisitante;
}

