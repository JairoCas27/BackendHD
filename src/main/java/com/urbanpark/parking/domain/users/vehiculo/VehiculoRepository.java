package com.urbanpark.parking.domain.users.vehiculo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VehiculoRepository extends JpaRepository<Vehiculo, UUID> {

    List<Vehiculo> findAllByTenantId(UUID tenantId);

    List<Vehiculo> findAllByUsuarioIdAndTenantId(UUID usuarioId, UUID tenantId);

    Optional<Vehiculo> findByPlacaAndTenantId(String placa, UUID tenantId);

    boolean existsByPlacaAndTenantId(String placa, UUID tenantId);
}