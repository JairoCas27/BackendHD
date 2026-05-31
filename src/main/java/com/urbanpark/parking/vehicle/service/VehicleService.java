package main.java.com.urbanpark.parking.vehicle.service;
 
import com.urbanpark.parking.vehicle.dto.request.CreateVehicleRequest;
import com.urbanpark.parking.vehicle.dto.request.UpdateVehicleRequest;
import com.urbanpark.parking.vehicle.dto.response.VehicleResponse;
 
import java.util.List;
 
public interface VehicleService {
 
    /** Registra un vehículo para un usuario. */
    VehicleResponse registerVehicle(Long userId, CreateVehicleRequest request, String tenantId);
 
    /** Lista todos los vehículos de un usuario. */
    List<VehicleResponse> getVehiclesByUser(Long userId, String tenantId);
 
    /** Lista todos los vehículos del condominio (para admin/seguridad). */
    List<VehicleResponse> getAllVehicles(String tenantId);
 
    /** Busca un vehículo por su placa en el condominio. */
    VehicleResponse getVehicleByPlate(String plate, String tenantId);
 
    /** Obtiene un vehículo por ID interno. */
    VehicleResponse getVehicleById(Long id, String tenantId);
 
    /** Actualiza datos descriptivos del vehículo. */
    VehicleResponse updateVehicle(Long id, UpdateVehicleRequest request, String tenantId);
 
    /** Elimina un vehículo del registro del SaaS. */
    void deleteVehicle(Long id, String tenantId);
 
    /**
     * Sincroniza vehículos desde la API externa del condominio. (RF-05)
     */
    int syncVehiclesFromCondominium(String tenantId, String externalJwt);
}