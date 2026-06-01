package main.java.com.urbanpark.parking.integration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioExterno {

    private Long id;
    private String email;
    private String nombre;
    private String apellido;
    private String telefono;
    private String tipoUsuario;  // RESIDENTE, ADMIN, GUARDA, VISITANTE
    private Long condominioId;
    private Boolean activo;
    private LocalDateTime ultimaActualizacion;
}
