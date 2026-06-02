package com.urbanpark.parking.domain.notifications;

import com.urbanpark.parking.domain.notifications.dto.NotificacionResponse;
import com.urbanpark.parking.domain.tenant.TenantContext;
import com.urbanpark.parking.shared.enums.TipoNotificacion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final SseEmitterRegistry sseRegistry;

    // Envío asíncrono — llamado desde otros servicios
    @Async
    public void enviar(
            UUID tenantId,
            UUID destinatarioId,   // null = broadcast al tenant
            TipoNotificacion tipo,
            String titulo,
            String mensaje,
            String entidadRefId
    ) {
        try {
            // 1. Persistir en BD
            Notificacion notificacion = Notificacion.builder()
                    .tenantId(tenantId)
                    .destinatarioId(destinatarioId)
                    .tipo(tipo)
                    .titulo(titulo)
                    .mensaje(mensaje)
                    .entidadRefId(entidadRefId)
                    .leida(false)
                    .build();

            Notificacion guardada = notificacionRepository.save(notificacion);

            // 2. Push SSE en tiempo real
            NotificacionResponse response = toResponse(guardada);

            if (destinatarioId != null) {
                sseRegistry.enviarA(destinatarioId, response);
            }
            // Para broadcast completo del tenant se implementa
            // cuando se integra con feature/tenant el mapa de usuarios activos

        } catch (Exception e) {
            log.error("Error enviando notificación [{} - {}]: {}", tipo, tenantId, e.getMessage());
        }
    }

    // Overload sin entidadRefId
    @Async
    public void enviar(
            UUID tenantId,
            UUID destinatarioId,
            TipoNotificacion tipo,
            String titulo,
            String mensaje
    ) {
        enviar(tenantId, destinatarioId, tipo, titulo, mensaje, null);
    }

    public Page<NotificacionResponse> listarMias(UUID usuarioId, Pageable pageable) {
        return notificacionRepository
                .findByDestinatario(TenantContext.getTenantId(), usuarioId, pageable)
                .map(this::toResponse);
    }

    public Page<NotificacionResponse> listarNoLeidas(UUID usuarioId, Pageable pageable) {
        return notificacionRepository
                .findNoLeidasByDestinatario(TenantContext.getTenantId(), usuarioId, pageable)
                .map(this::toResponse);
    }

    public long contarNoLeidas(UUID usuarioId) {
        return notificacionRepository.countNoLeidas(TenantContext.getTenantId(), usuarioId);
    }

    @Transactional
    public void marcarLeida(UUID notificacionId) {
        notificacionRepository.findById(notificacionId).ifPresent(n -> {
            n.setLeida(true);
            notificacionRepository.save(n);
        });
    }

    @Transactional
    public void marcarTodasLeidas(UUID usuarioId) {
        notificacionRepository.marcarTodasLeidas(TenantContext.getTenantId(), usuarioId);
    }

    private NotificacionResponse toResponse(Notificacion n) {
        return NotificacionResponse.builder()
                .id(n.getId())
                .tipo(n.getTipo())
                .titulo(n.getTitulo())
                .mensaje(n.getMensaje())
                .entidadRefId(n.getEntidadRefId())
                .leida(n.isLeida())
                .createdAt(n.getCreatedAt())
                .build();
    }
}