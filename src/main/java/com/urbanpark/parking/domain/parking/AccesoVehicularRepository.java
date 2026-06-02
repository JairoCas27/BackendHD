package com.urbanpark.parking.domain.parking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccesoVehicularRepository extends JpaRepository<AccesoVehicular, UUID> {

    List<AccesoVehicular> findAllByTenantId(UUID tenantId);

    List<AccesoVehicular> findAllByTenantIdAndVehiculoId(UUID tenantId, UUID vehiculoId);

    // Buscar acceso abierto (entrada sin salida) por placa
    @Query("""
        SELECT a FROM AccesoVehicular a
        WHERE a.tenantId = :tenantId
        AND a.placa = :placa
        AND a.timestampSalida IS NULL
        AND a.autorizado = true
    """)
    Optional<AccesoVehicular> findAccesoAbiertoPorPlaca(UUID tenantId, String placa);

    // Contar vehículos activos dentro del parking
    @Query("""
        SELECT COUNT(a) FROM AccesoVehicular a
        WHERE a.tenantId = :tenantId
        AND a.timestampSalida IS NULL
        AND a.autorizado = true
    """)
    int countVehiculosActivos(UUID tenantId);

    // Historial por rango de fechas
    List<AccesoVehicular> findAllByTenantIdAndTimestampEntradaBetween(
            UUID tenantId,
            LocalDateTime inicio,
            LocalDateTime fin
    );
}