package com.urbanpark.parking.modules.access.service;

import com.urbanpark.parking.modules.access.dto.request.RegisterAccessRequest;
import com.urbanpark.parking.modules.access.dto.response.ParkingAccessResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface ParkingAccessService {

    /**
     * Registra un acceso (manual o automático).
     * @param request Datos de la solicitud
     * @param method "AUTOMATIC" o "MANUAL"
     * @param registeredByUserId ID del usuario que registra (puede ser null si automático)
     * @return El acceso registrado
     */
    ParkingAccessResponse registerAccess(RegisterAccessRequest request, String method, Long registeredByUserId);

    /**
     * Obtiene todos los accesos del tenant actual.
     */
    List<ParkingAccessResponse> getAllAccesses();

    /**
     * Obtiene accesos paginados.
     */
    Page<ParkingAccessResponse> getAccessesPaginated(Pageable pageable);

    /**
     * Obtiene accesos por placa.
     */
    List<ParkingAccessResponse> getAccessesByPlate(String plate);

    /**
     * Obtiene accesos por rango de fechas.
     */
    List<ParkingAccessResponse> getAccessesByDateRange(LocalDateTime from, LocalDateTime to);

    /**
     * Obtiene accesos de un propietario específico (por su ID interno).
     */
    List<ParkingAccessResponse> getAccessesByOwner(Long ownerId);

    /**
     * Verifica si un vehículo está actualmente dentro del estacionamiento.
     */
    boolean isVehicleCurrentlyInside(String plate);
}