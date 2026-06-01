package com.urbanpark.parking.modules.access.validation;

import com.urbanpark.parking.modules.access.domain.enums.AccessType;
import com.urbanpark.parking.modules.access.exception.AccessDeniedException;
import com.urbanpark.parking.modules.access.exception.VehicleNotAuthorizedException;
import com.urbanpark.parking.user.domain.enums.UserStatus;
import com.urbanpark.parking.user.domain.model.User;
import com.urbanpark.parking.vehicle.domain.model.Vehicle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AccessValidator {

    /**
     * Determina si un vehículo está autorizado para acceder.
     * @return true si autorizado, false si no (lanza excepción o retorna false)
     * @throws VehicleNotAuthorizedException si no autorizado con razón específica
     */
    public boolean isAccessAuthorized(Vehicle vehicle, User owner, AccessType accessType, String tenantId) {
        // Caso 1: Vehículo no registrado en el SaaS
        if (vehicle == null) {
            throw new VehicleNotAuthorizedException("Vehículo no registrado en el sistema. Contacte al administrador.");
        }

        // Caso 2: Vehículo inactivo
        if (!Boolean.TRUE.equals(vehicle.getIsActive())) {
            throw new VehicleNotAuthorizedException("El vehículo está desactivado. No puede ingresar.");
        }

        // Caso 3: Propietario no existe o está inactivo
        if (owner == null) {
            throw new VehicleNotAuthorizedException("El vehículo no está asociado a un usuario activo.");
        }
        if (owner.getStatus() != UserStatus.ACTIVE) {
            throw new VehicleNotAuthorizedException("El propietario del vehículo no está activo. Contacte al administrador.");
        }

        // Reglas adicionales pueden venir de feature/rules (por ejemplo, límite de vehículos dentro, horarios)
        // Por ahora, todo vehículo activo con dueño activo está autorizado
        log.debug("Acceso autorizado para vehículo {} (dueño: {})", vehicle.getPlate(), owner.getName());
        return true;
    }
}