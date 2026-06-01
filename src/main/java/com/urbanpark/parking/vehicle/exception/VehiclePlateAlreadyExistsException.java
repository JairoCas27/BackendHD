package com.urbanpark.parking.vehicle.exception;
 
import com.urbanpark.parking.core.exception.BusinessException;
 
public class VehiclePlateAlreadyExistsException extends BusinessException {
    public VehiclePlateAlreadyExistsException(String plate) {
        super("Ya existe un vehículo registrado con la placa '" + plate + "' en este condominio.");
    }
}
 