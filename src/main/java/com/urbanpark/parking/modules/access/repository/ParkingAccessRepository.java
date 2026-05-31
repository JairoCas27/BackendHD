package com.urbanpark.parking.modules.access.repository;

import com.urbanpark.parking.modules.access.domain.model.ParkingAccess;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ParkingAccessRepository extends JpaRepository<ParkingAccess, Long> {

    List<ParkingAccess> findByTenantIdOrderByAccessTimestampDesc(String tenantId);

    Page<ParkingAccess> findByTenantId(String tenantId, Pageable pageable);

    List<ParkingAccess> findByTenantIdAndPlateOrderByAccessTimestampDesc(String tenantId, String plate);

    List<ParkingAccess> findByTenantIdAndAccessTimestampBetween(String tenantId, LocalDateTime from, LocalDateTime to);

    @Query("SELECT pa FROM ParkingAccess pa WHERE pa.tenantId = :tenantId AND pa.vehicleOwnerId = :ownerId ORDER BY pa.accessTimestamp DESC")
    List<ParkingAccess> findByVehicleOwnerId(@Param("tenantId") String tenantId, @Param("ownerId") Long ownerId);

    // Último acceso de un vehículo (para saber si está adentro)
    Optional<ParkingAccess> findTopByTenantIdAndPlateOrderByAccessTimestampDesc(String tenantId, String plate);
}