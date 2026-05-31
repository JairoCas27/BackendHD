package com.urbanpark.parking.modules.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RespuestaAccesoDto {
    private boolean accesoAutorizado;
    private String mensajeResultado; // Ej: "Acceso Concedido" o "Bloqueado: Deuda en mantenimiento"
    private String accionRequerida;  // Ej: "ABRIR_BARRERA" o "MANTENER_CERRADO"
}