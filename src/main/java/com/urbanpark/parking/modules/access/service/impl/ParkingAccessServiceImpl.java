package com.urbanpark.parking.modules.access.service.impl;

import com.urbanpark.parking.core.exception.BusinessException;
import com.urbanpark.parking.modules.access.domain.enums.AccessStatus;
import com.urbanpark.parking.modules.access.domain.enums.AccessType;
import com.urbanpark.parking.modules.access.domain.model.ParkingAccess;
import com.urbanpark.parking.modules.access.dto.request.RegisterAccessRequest;
import com.urbanpark.parking.modules.access.dto.response.ParkingAccessResponse;
import com.urbanpark.parking.modules.access.exception.AccessDeniedException;
import com.urbanpark.parking.modules.access.exception.VehicleNotAuthorizedException;
import com.urbanpark.parking.modules.access.mapper.ParkingAccessMapper;
import com.urbanpark.parking.modules.access.repository.ParkingAccessRepository;
import com.urbanpark.parking.modules.access.service.ParkingAccessService;
import com.urbanpark.parking.modules.access.validation.AccessValidator;
import com.urbanpark.parking.tenant.TenantContext;
import com.urbanpark.parking.user.domain.model.User;
import com.urbanpark.parking.user.repository.UserRepository;
import com.urbanpark.parking.vehicle.domain.model.Vehicle;
import com.urbanpark.parking.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParkingAccessServiceImpl implements ParkingAccessService {

    private final ParkingAccessRepository accessRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final ParkingAccessMapper accessMapper;
    private final AccessValidator accessValidator;

    @Override
    @Transactional
    public ParkingAccessResponse registerAccess(RegisterAccessRequest request, String method, Long registeredByUserId) {
        String tenantId = TenantContext.getCurrentTenant();
        String normalizedPlate = request.plate().toUpperCase().trim();

        // 1. Validar existencia del vehículo
        Vehicle vehicle = vehicleRepository.findByPlateAndTenantId(normalizedPlate, tenantId)
                .orElse(null);

        User owner = null;
        if (vehicle != null && vehicle.getIsActive()) {
            owner = vehicle.getOwner();
        }

        // 2. Aplicar reglas de negocio (RF-11)
        boolean authorized;
        String denialReason = null;
        try {
            authorized = accessValidator.isAccessAuthorized(vehicle, owner, request.accessType(), tenantId);
        } catch (VehicleNotAuthorizedException e) {
            authorized = false;
            denialReason = e.getMessage();
        }

        AccessStatus status;
        if (authorized) {
            status = AccessStatus.APROBADO;
        } else {
            status = AccessStatus.DENEGADO;
            log.warn("Acceso denegado para placa {} en tenant {}: {}", normalizedPlate, tenantId, denialReason);
        }

        // 3. Para entradas, verificar que no esté ya dentro; para salidas, verificar que esté dentro (opcional)
        if (request.accessType() == AccessType.ENTRADA && isVehicleCurrentlyInside(normalizedPlate)) {
            throw new BusinessException("El vehículo ya se encuentra dentro del estacionamiento. Registre primero la salida.");
        }
        if (request.accessType() == AccessType.SALIDA && !isVehicleCurrentlyInside(normalizedPlate)) {
            throw new BusinessException("No se puede registrar salida porque el vehículo no está dentro.");
        }

        // 4. Crear el registro de acceso
        ParkingAccess access = ParkingAccess.builder()
                .tenantId(tenantId)
                .plate(normalizedPlate)
                .accessType(request.accessType())
                .accessStatus(status)
                .accessTimestamp(LocalDateTime.now())
                .method(method)
                .registeredByUserId(registeredByUserId)
                .vehicleOwnerId(owner != null ? owner.getId() : null)
                .notes(request.notes())
                .build();

        ParkingAccess saved = accessRepository.save(access);
        log.info("Acceso registrado: {} - {} - {} - {}", normalizedPlate, request.accessType(), status, method);

        return accessMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParkingAccessResponse> getAllAccesses() {
        String tenantId = TenantContext.getCurrentTenant();
        return accessRepository.findByTenantIdOrderByAccessTimestampDesc(tenantId).stream()
                .map(accessMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ParkingAccessResponse> getAccessesPaginated(Pageable pageable) {
        String tenantId = TenantContext.getCurrentTenant();
        return accessRepository.findByTenantId(tenantId, pageable).map(accessMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParkingAccessResponse> getAccessesByPlate(String plate) {
        String tenantId = TenantContext.getCurrentTenant();
        String normalizedPlate = plate.toUpperCase().trim();
        return accessRepository.findByTenantIdAndPlateOrderByAccessTimestampDesc(tenantId, normalizedPlate).stream()
                .map(accessMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParkingAccessResponse> getAccessesByDateRange(LocalDateTime from, LocalDateTime to) {
        String tenantId = TenantContext.getCurrentTenant();
        return accessRepository.findByTenantIdAndAccessTimestampBetween(tenantId, from, to).stream()
                .map(accessMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParkingAccessResponse> getAccessesByOwner(Long ownerId) {
        String tenantId = TenantContext.getCurrentTenant();
        return accessRepository.findByVehicleOwnerId(tenantId, ownerId).stream()
                .map(accessMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isVehicleCurrentlyInside(String plate) {
        String tenantId = TenantContext.getCurrentTenant();
        String normalizedPlate = plate.toUpperCase().trim();
        var lastAccessOpt = accessRepository.findTopByTenantIdAndPlateOrderByAccessTimestampDesc(tenantId, normalizedPlate);
        if (lastAccessOpt.isEmpty()) return false;
        ParkingAccess last = lastAccessOpt.get();
        return last.getAccessType() == AccessType.ENTRADA && last.getAccessStatus() == AccessStatus.APROBADO;
    }
}