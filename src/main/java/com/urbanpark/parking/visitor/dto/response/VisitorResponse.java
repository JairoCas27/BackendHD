package com.urbanpark.parking.visitor.dto.response;
 
import java.time.LocalDateTime;
 
public record VisitorResponse(
    Long id,
    String tenantId,
    String name,
    String idDocument,
    String vehiclePlate,
    String vehicleDescription,
    Long authorizedByUserId,
    String authorizedByName,
    LocalDateTime validFrom,
    LocalDateTime validUntil,
    Boolean isActive,
    String notes,
    boolean currentlyValid
) {}
 