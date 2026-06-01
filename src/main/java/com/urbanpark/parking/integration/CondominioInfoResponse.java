package com.urbanpark.parking.integration.condominio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CondominioInfoResponse {

    private Long id;
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
    private Integer totalEspacios;
    private Integer espaciosDisponibles;
    private String estado;  // ACTIVO, INACTIVO, MANTENIMIENTO
    private LocalDateTime creadoEn;
    private List<String> servicios;  // estacionamiento, vigilancia, etc.
}