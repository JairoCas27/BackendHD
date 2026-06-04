// domain/audit/AuditLogRepository.java
package com.urbanpark.parking.domain.audit;

import com.urbanpark.parking.shared.enums.TipoAccionAudit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findAllByOrderByFechaHoraDesc(Pageable pageable);

    Page<AuditLog> findByUsuarioSaasIdOrderByFechaHoraDesc(Long usuarioSaasId, Pageable pageable);

    Page<AuditLog> findByAccionOrderByFechaHoraDesc(TipoAccionAudit accion, Pageable pageable);

    @Query("""
        SELECT a FROM AuditLog a
        WHERE (:usuarioId   IS NULL OR a.usuarioSaasId = :usuarioId)
          AND (:accion      IS NULL OR a.accion        = :accion)
          AND (:exitoso     IS NULL OR a.exitoso       = :exitoso)
          AND (:desde       IS NULL OR a.fechaHora    >= :desde)
          AND (:hasta       IS NULL OR a.fechaHora    <= :hasta)
        ORDER BY a.fechaHora DESC
    """)
    Page<AuditLog> filtrar(
            @Param("usuarioId") Long usuarioId,
            @Param("accion")    TipoAccionAudit accion,
            @Param("exitoso")   Boolean exitoso,
            @Param("desde")     LocalDateTime desde,
            @Param("hasta")     LocalDateTime hasta,
            Pageable pageable
    );
}