package main.java.com.urbanpark.parking.integration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EspacioParqueo {

    private Long id;
    private String codigo;  // A-01, B-15, etc.
    private String tipo;  // AUTO, MOTO, DISCAPACITADO, VISITANTE
    private Boolean disponible;
    private Long condominioId;
    private String ubicacion;  // Sotano 1, Planta baja, etc.
}