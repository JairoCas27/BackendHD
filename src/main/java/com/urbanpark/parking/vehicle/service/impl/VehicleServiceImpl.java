package main.java.com.urbanpark.parking.vehicle.service.impl;
 
import com.urbanpark.parking.integration.client.CondominiumApiClient;
import com.urbanpark.parking.integration.dto.ExternalCondominiumDtos.ExternalVehicleDto;
import com.urbanpark.parking.user.domain.model.User;
import com.urbanpark.parking.user.exception.UserNotFoundException;
import com.urbanpark.parking.user.repository.UserRepository;
import com.urbanpark.parking.vehicle.domain.model.Vehicle;
import com.urbanpark.parking.vehicle.dto.request.CreateVehicleRequest;
import com.urbanpark.parking.vehicle.dto.request.UpdateVehicleRequest;
import com.urbanpark.parking.vehicle.dto.response.VehicleResponse;
import com.urbanpark.parking.vehicle.exception.VehicleNotFoundException;
import com.urbanpark.parking.vehicle.exception.VehiclePlateAlreadyExistsException;
import com.urbanpark.parking.vehicle.mapper.VehicleMapper;
import com.urbanpark.parking.vehicle.repository.VehicleRepository;
import com.urbanpark.parking.vehicle.service.VehicleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.util.List;
 
@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {
 
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final VehicleMapper vehicleMapper;
    private final CondominiumApiClient condominiumApiClient;
 
    @Override
    @Transactional
    public VehicleResponse registerVehicle(Long userId, CreateVehicleRequest request, String tenantId) {
        User owner = userRepository.findByIdAndTenantId(userId, tenantId)
            .orElseThrow(() -> new UserNotFoundException(userId));
 
        String normalizedPlate = request.plate().toUpperCase().trim();
 
        if (vehicleRepository.existsByPlateAndTenantId(normalizedPlate, tenantId)) {
            throw new VehiclePlateAlreadyExistsException(normalizedPlate);
        }
 
        Vehicle vehicle = Vehicle.builder()
            .tenantId(tenantId)
            .plate(normalizedPlate)
            .brand(request.brand())
            .model(request.model())
            .color(request.color())
            .type(request.type())
            .owner(owner)
            .isActive(true)
            .build();
 
        return vehicleMapper.toResponse(vehicleRepository.save(vehicle));
    }
 
    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> getVehiclesByUser(Long userId, String tenantId) {
        // Validamos que el usuario exista en el tenant
        if (!userRepository.existsByExternalIdAndTenantId(userId.toString(), tenantId)
            && userRepository.findByIdAndTenantId(userId, tenantId).isEmpty()) {
            throw new UserNotFoundException(userId);
        }
        return vehicleRepository.findAllByOwnerIdAndTenantId(userId, tenantId)
            .stream()
            .map(vehicleMapper::toResponse)
            .toList();
    }
 
    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> getAllVehicles(String tenantId) {
        return vehicleRepository.findAllByTenantId(tenantId)
            .stream()
            .map(vehicleMapper::toResponse)
            .toList();
    }
 
    @Override
    @Transactional(readOnly = true)
    public VehicleResponse getVehicleByPlate(String plate, String tenantId) {
        String normalizedPlate = plate.toUpperCase().trim();
        Vehicle vehicle = vehicleRepository.findByPlateAndTenantId(normalizedPlate, tenantId)
            .orElseThrow(() -> new VehicleNotFoundException(normalizedPlate, tenantId));
        return vehicleMapper.toResponse(vehicle);
    }
 
    @Override
    @Transactional(readOnly = true)
    public VehicleResponse getVehicleById(Long id, String tenantId) {
        Vehicle vehicle = vehicleRepository.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new VehicleNotFoundException(id));
        return vehicleMapper.toResponse(vehicle);
    }
 
    @Override
    @Transactional
    public VehicleResponse updateVehicle(Long id, UpdateVehicleRequest request, String tenantId) {
        Vehicle vehicle = vehicleRepository.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new VehicleNotFoundException(id));
 
        if (request.brand() != null) vehicle.setBrand(request.brand());
        if (request.model() != null) vehicle.setModel(request.model());
        if (request.color() != null) vehicle.setColor(request.color());
        if (request.type() != null) vehicle.setType(request.type());
        if (request.isActive() != null) vehicle.setIsActive(request.isActive());
 
        return vehicleMapper.toResponse(vehicleRepository.save(vehicle));
    }
 
    @Override
    @Transactional
    public void deleteVehicle(Long id, String tenantId) {
        Vehicle vehicle = vehicleRepository.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new VehicleNotFoundException(id));
        vehicleRepository.delete(vehicle);
        log.info("Vehículo {} eliminado del tenant {}", id, tenantId);
    }
 
    @Override
    @Transactional
    public int syncVehiclesFromCondominium(String tenantId, String externalJwt) {
        log.info("Iniciando sincronización de vehículos para tenant: {}", tenantId);
 
        List<ExternalVehicleDto> externalVehicles = condominiumApiClient.getAllVehicles(externalJwt);
        int synced = 0;
 
        for (ExternalVehicleDto dto : externalVehicles) {
            try {
                // Solo sincronizamos si el propietario ya existe en nuestro SaaS
                userRepository.findByExternalIdAndTenantId(dto.userId(), tenantId)
                    .ifPresent(owner -> syncSingleVehicle(dto, owner, tenantId));
                synced++;
            } catch (Exception e) {
                log.error("Error sincronizando vehículo externo {} en tenant {}: {}",
                    dto.plate(), tenantId, e.getMessage());
            }
        }
 
        log.info("Sincronización de vehículos completada para tenant {}. Procesados: {}/{}",
            tenantId, synced, externalVehicles.size());
        return synced;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getVehicleOwnerByPlate(String plate, String tenantId) {
        String normalizedPlate = plate.toUpperCase().trim();
        return vehicleRepository.findByPlateAndTenantId(normalizedPlate, tenantId)
                .filter(Vehicle::getIsActive)
                .map(Vehicle::getOwner);
    }
 
    // ──────────────────────────────────────────────────────────────────────────
    // HELPERS
    // ──────────────────────────────────────────────────────────────────────────
 
    private void syncSingleVehicle(ExternalVehicleDto dto, User owner, String tenantId) {
        String plate = dto.plate().toUpperCase().trim();
 
        vehicleRepository.findByPlateAndTenantId(plate, tenantId)
            .ifPresentOrElse(
                existing -> {
                    existing.setBrand(dto.brand());
                    existing.setModel(dto.model());
                    existing.setColor(dto.color());
                    existing.setType(dto.type());
                    vehicleRepository.save(existing);
                },
                () -> {
                    Vehicle newVehicle = Vehicle.builder()
                        .tenantId(tenantId)
                        .plate(plate)
                        .brand(dto.brand())
                        .model(dto.model())
                        .color(dto.color())
                        .type(dto.type())
                        .owner(owner)
                        .isActive(true)
                        .build();
                    vehicleRepository.save(newVehicle);
                    log.info("Vehículo sincronizado: placa={}, tenant={}", plate, tenantId);
                }
            );
    }
}