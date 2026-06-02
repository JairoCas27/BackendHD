package com.urbanpark.parking.domain.security_operations;

import com.urbanpark.parking.shared.enums.EstadoIncidente;
import com.urbanpark.parking.shared.enums.NivelIncidente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IncidenteRepository extends JpaRepository<Incidente, UUID> {

    List<Incidente> findAllByTenantId(UUID tenantId);

    List<Incidente> findAllByTenantIdAndEstado(UUID tenantId, EstadoIncidente estado);

    List<Incidente> findAllByTenantIdAndNivel(UUID tenantId, NivelIncidente nivel);

    List<Incidente> findAllByAgenteIdAndTenantId(UUID agenteId, UUID tenantId);
}