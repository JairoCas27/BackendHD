package com.urbanpark.parking.domain.users.usuario.dto;

import com.urbanpark.parking.shared.enums.RolParking;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UsuarioResponse {
    private UUID id;
    private String externalId;
    private String nombre;
    private String email;
    private RolParking rolParking;
    private boolean activo;
    private LocalDateTime syncedAt;
}