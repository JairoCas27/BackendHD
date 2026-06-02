package com.urbanpark.parking.domain.notifications;

import com.urbanpark.parking.domain.notifications.dto.NotificacionResponse;
import com.urbanpark.parking.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;
    private final SseEmitterRegistry sseRegistry;

    // SSE — el frontend conecta aquí para recibir push en tiempo real
    @GetMapping(value = "/stream/{usuarioId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("isAuthenticated()")
    public SseEmitter stream(@PathVariable UUID usuarioId) {
        return sseRegistry.registrar(usuarioId);
    }

    @GetMapping("/mias/{usuarioId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<NotificacionResponse>>> listarMias(
            @PathVariable UUID usuarioId,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                notificacionService.listarMias(usuarioId, pageable)));
    }

    @GetMapping("/no-leidas/{usuarioId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<NotificacionResponse>>> noLeidas(
            @PathVariable UUID usuarioId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                notificacionService.listarNoLeidas(usuarioId, pageable)));
    }

    @GetMapping("/no-leidas/{usuarioId}/count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Long>> countNoLeidas(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(ApiResponse.success(
                notificacionService.contarNoLeidas(usuarioId)));
    }

    @PatchMapping("/{id}/leer")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> marcarLeida(@PathVariable UUID id) {
        notificacionService.marcarLeida(id);
        return ResponseEntity.ok(ApiResponse.success("Notificación marcada como leída", null));
    }

    @PatchMapping("/leer-todas/{usuarioId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> marcarTodasLeidas(@PathVariable UUID usuarioId) {
        notificacionService.marcarTodasLeidas(usuarioId);
        return ResponseEntity.ok(ApiResponse.success("Todas marcadas como leídas", null));
    }
}