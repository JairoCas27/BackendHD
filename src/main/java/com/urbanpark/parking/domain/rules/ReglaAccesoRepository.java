package com.urbanpark.parking.domain.rules;

import com.urbanpark.parking.shared.enums.TipoRegla;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReglaAccesoRepository extends JpaRepository<ReglaAcceso, UUID> {

    List<ReglaAcceso> findAllByTenantId(UUID tenantId);

    List<ReglaAcceso> findAllByTenantIdAndActivoTrue(UUID tenantId);

    List<ReglaAcceso> findAllByTenantIdAndTipoAndActivoTrue(UUID tenantId, TipoRegla tipo);
}