package com.urbanpark.parking.modules.access.mapper;

import com.urbanpark.parking.modules.access.domain.model.ParkingAccess;
import com.urbanpark.parking.modules.access.dto.response.ParkingAccessResponse;
import org.springframework.stereotype.Component;

@Component
public class ParkingAccessMapper {

    public ParkingAccessResponse toResponse(ParkingAccess access) {
        return new ParkingAccessResponse(
            access.getId(),
            access.getTenantId(),
            access.getPlate(),
            access.getAccessType(),
            access.getAccessStatus(),
            access.getAccessTimestamp(),
            access.getMethod(),
            access.getRegisteredByUserId(),
            access.getVehicleOwnerId(),
            access.getNotes(),
            access.getCreatedAt()
        );
    }
}