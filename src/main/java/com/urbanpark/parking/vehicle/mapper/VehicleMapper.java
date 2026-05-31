package com.urbanpark.parking.vehicle.mapper;
 
import com.urbanpark.parking.vehicle.domain.model.Vehicle;
import com.urbanpark.parking.vehicle.dto.response.VehicleResponse;
import org.springframework.stereotype.Component;
 
@Component
public class VehicleMapper {
 
    public VehicleResponse toResponse(Vehicle vehicle) {
        return new VehicleResponse(
            vehicle.getId(),
            vehicle.getTenantId(),
            vehicle.getPlate(),
            vehicle.getBrand(),
            vehicle.getModel(),
            vehicle.getColor(),
            vehicle.getType(),
            vehicle.getOwner().getId(),
            vehicle.getOwner().getName(),
            vehicle.getIsActive(),
            vehicle.getCreatedAt()
        );
    }
}