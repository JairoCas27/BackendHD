package com.urbanpark.parking.domain.audit;

import com.urbanpark.parking.shared.enums.TipoAccionAudit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    // Paginado — consultas de historial pueden ser enormes
    Page<AuditLog> findAllByTenantId(UUID tenantId, Pageable pageable);

    Page<AuditLog> findAllByTenantIdAndAccion(
            UUID tenantId, TipoAccionAudit accion, Pageable pageable);

    Page<AuditLog> findAllByTenantIdAndUsuarioId(
            UUID tenantId, UUID usuarioId, Pageable pageable);

    Page<AuditLog> findAllByTenantIdAndCreatedAtBetween(
            UUID tenantId, LocalDateTime inicio, LocalDateTime fin, Pageable pageable);

    // Superadmin — todos los tenants
    Page<AuditLog> findAll(Pageable pageable);

    @Query("""
        SELECT a FROM AuditLog a
        WHERE a.tenantId = :tenantId
        AND a.entidad = :entidad
        AND a.entidadId = :entidadId
        ORDER BY a.createdAt DESC
    """)
    List<AuditLog> findHistorialEntidad(UUID tenantId, String entidad, String entidadId);
}