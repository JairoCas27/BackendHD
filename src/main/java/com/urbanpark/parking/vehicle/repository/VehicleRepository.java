package main.java.com.urbanpark.parking.vehicle.repository;
 
import com.urbanpark.parking.vehicle.domain.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import java.util.List;
import java.util.Optional;
 
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
 
    Optional<Vehicle> findByPlateAndTenantId(String plate, String tenantId);
 
    Optional<Vehicle> findByIdAndTenantId(Long id, String tenantId);
 
    List<Vehicle> findAllByOwnerIdAndTenantId(Long ownerId, String tenantId);
 
    List<Vehicle> findAllByTenantId(String tenantId);
 
    boolean existsByPlateAndTenantId(String plate, String tenantId);
 
    boolean existsByPlateAndTenantIdAndIdNot(String plate, String tenantId, Long excludeId);
}
 