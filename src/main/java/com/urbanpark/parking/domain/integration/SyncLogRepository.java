package com.urbanpark.parking.domain.integration;

import com.urbanpark.parking.shared.enums.EstadoSync;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SyncLogRepository extends JpaRepository<SyncLog, UUID> {

    Page<SyncLog> findAllByTenantIdOrderByCreatedAtDesc(UUID tenantId, Pageable pageable);

    List<SyncLog> findAllByTenantIdAndEstado(UUID tenantId, EstadoSync estado);

    Optional<SyncLog> findTopByTenantIdAndTipoOrderByCreatedAtDesc(UUID tenantId, String tipo);
}