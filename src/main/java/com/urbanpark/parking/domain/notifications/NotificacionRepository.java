package com.urbanpark.parking.domain.notifications;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface NotificacionRepository extends JpaRepository<Notificacion, UUID> {

    // Notificaciones del tenant (broadcast + personales)
    @Query("""
        SELECT n FROM Notificacion n
        WHERE n.tenantId = :tenantId
        AND (n.destinatarioId IS NULL OR n.destinatarioId = :usuarioId)
        ORDER BY n.createdAt DESC
    """)
    Page<Notificacion> findByDestinatario(UUID tenantId, UUID usuarioId, Pageable pageable);

    // Solo no leídas
    @Query("""
        SELECT n FROM Notificacion n
        WHERE n.tenantId = :tenantId
        AND (n.destinatarioId IS NULL OR n.destinatarioId = :usuarioId)
        AND n.leida = false
        ORDER BY n.createdAt DESC
    """)
    Page<Notificacion> findNoLeidasByDestinatario(
            UUID tenantId, UUID usuarioId, Pageable pageable);

    // Contar no leídas
    @Query("""
        SELECT COUNT(n) FROM Notificacion n
        WHERE n.tenantId = :tenantId
        AND (n.destinatarioId IS NULL OR n.destinatarioId = :usuarioId)
        AND n.leida = false
    """)
    long countNoLeidas(UUID tenantId, UUID usuarioId);

    // Marcar todas como leídas
    @Modifying
    @Query("""
        UPDATE Notificacion n SET n.leida = true
        WHERE n.tenantId = :tenantId
        AND (n.destinatarioId IS NULL OR n.destinatarioId = :usuarioId)
        AND n.leida = false
    """)
    void marcarTodasLeidas(UUID tenantId, UUID usuarioId);
}