package com.urbanpark.parking.domain.notifications;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SseEmitterRegistry {

    // Map<usuarioId, SseEmitter>
    private final Map<UUID, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter registrar(UUID usuarioId) {
        // Timeout de 30 minutos por conexión
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);

        emitter.onCompletion(() -> {
            emitters.remove(usuarioId);
            log.debug("SSE completado para usuario {}", usuarioId);
        });

        emitter.onTimeout(() -> {
            emitters.remove(usuarioId);
            log.debug("SSE timeout para usuario {}", usuarioId);
        });

        emitter.onError(e -> {
            emitters.remove(usuarioId);
            log.debug("SSE error para usuario {}: {}", usuarioId, e.getMessage());
        });

        emitters.put(usuarioId, emitter);
        return emitter;
    }

    public void enviarA(UUID usuarioId, Object data) {
        SseEmitter emitter = emitters.get(usuarioId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notificacion")
                        .data(data));
            } catch (Exception e) {
                emitters.remove(usuarioId);
                log.debug("Error enviando SSE a {}: {}", usuarioId, e.getMessage());
            }
        }
    }

    public void broadcast(UUID tenantId, Map<UUID, UUID> usuariosTenant, Object data) {
        usuariosTenant.keySet().forEach(usuarioId -> enviarA(usuarioId, data));
    }

    public boolean tieneConexion(UUID usuarioId) {
        return emitters.containsKey(usuarioId);
    }
}