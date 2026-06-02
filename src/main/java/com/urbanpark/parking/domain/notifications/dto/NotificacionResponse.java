package com.urbanpark.parking.domain.notifications.dto;

import com.urbanpark.parking.shared.enums.TipoNotificacion;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class NotificacionResponse {
    private UUID id;
    private TipoNotificacion tipo;
    private String titulo;
    private String mensaje;
    private String entidadRefId;
    private boolean leida;
    private LocalDateTime createdAt;
}