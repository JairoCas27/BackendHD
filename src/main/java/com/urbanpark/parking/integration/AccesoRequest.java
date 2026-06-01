package main.java.com.urbanpark.parking.integration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccesoRequest {

    private String usuarioId;
    private String condominioId;
    private String tipoAcceso;  // ENTRADA, SALIDA
    private String vehiculoId;
}