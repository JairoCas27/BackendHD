package com.urbanpark.parking.domain.users.visitante;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VisitanteRepository extends JpaRepository<Visitante, UUID> {

    List<Visitante> findAllByTenantId(UUID tenantId);

    List<Visitante> findAllByPropietarioIdAndTenantId(UUID propietarioId, UUID tenantId);

    @Query("""
        SELECT v FROM Visitante v
        WHERE v.tenantId = :tenantId
        AND v.placaVehiculo = :placa
        AND v.activo = true
        AND v.fechaInicio <= :ahora
        AND v.fechaFin >= :ahora
    """)
    Optional<Visitante> findVisitanteActivoPorPlaca(UUID tenantId, String placa, LocalDateTime ahora);
}