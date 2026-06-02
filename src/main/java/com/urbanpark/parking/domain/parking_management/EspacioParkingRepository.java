package com.urbanpark.parking.domain.parking_management;

import com.urbanpark.parking.shared.enums.EstadoEspacio;
import com.urbanpark.parking.shared.enums.TipoEspacio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EspacioParkingRepository extends JpaRepository<EspacioParking, UUID> {

    List<EspacioParking> findAllByTenantId(UUID tenantId);

    List<EspacioParking> findAllByTenantIdAndEstado(UUID tenantId, EstadoEspacio estado);

    List<EspacioParking> findAllByTenantIdAndZona(UUID tenantId, String zona);

    Optional<EspacioParking> findByCodigoAndTenantId(String codigo, UUID tenantId);

    boolean existsByCodigoAndTenantId(String codigo, UUID tenantId);

    // Buscar primer espacio libre por tipo
    Optional<EspacioParking> findFirstByTenantIdAndEstadoAndTipo(
            UUID tenantId,
            EstadoEspacio estado,
            TipoEspacio tipo
    );

    // Contar por estado para métricas de ocupación
    @Query("""
        SELECT e.estado, COUNT(e)
        FROM EspacioParking e
        WHERE e.tenantId = :tenantId
        GROUP BY e.estado
    """)
    List<Object[]> countByEstado(UUID tenantId);
}