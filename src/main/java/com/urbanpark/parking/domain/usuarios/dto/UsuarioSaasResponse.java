package com.urbanpark.parking.domain.usuarios.dto;

import com.urbanpark.parking.shared.enums.EstadoUsuarioSaas;
import com.urbanpark.parking.shared.enums.OrigenRegistro;
import com.urbanpark.parking.shared.enums.RolSaas;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UsuarioSaasResponse {

    private Long id;
    private String email;
    private String nombres;
    private String apellidos;
    private String nombreCompleto;
    private String dni;
    private String telefono;
    private RolSaas rol;
    private EstadoUsuarioSaas estado;
    private OrigenRegistro origenRegistro;
    private Long creadoPorId;
    private String creadoPorNombre;
    private boolean esBaseProtegido;
    private LocalDateTime fechaRegistro;
}