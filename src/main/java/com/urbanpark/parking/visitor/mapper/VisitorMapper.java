package com.urbanpark.parking.visitor.mapper;
 
import com.urbanpark.parking.user.domain.model.Visitor;
import com.urbanpark.parking.visitor.dto.response.VisitorResponse;
import org.springframework.stereotype.Component;
 
@Component
public class VisitorMapper {
 
    public VisitorResponse toResponse(Visitor visitor) {
        return new VisitorResponse(
            visitor.getId(),
            visitor.getTenantId(),
            visitor.getName(),
            visitor.getIdDocument(),
            visitor.getVehiclePlate(),
            visitor.getVehicleDescription(),
            visitor.getAuthorizedBy().getId(),
            visitor.getAuthorizedBy().getName(),
            visitor.getValidFrom(),
            visitor.getValidUntil(),
            visitor.getIsActive(),
            visitor.getNotes(),
            visitor.isCurrentlyValid()
        );
    }
}