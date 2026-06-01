package com.urbanpark.parking.modules.access.dto.response;

import com.urbanpark.parking.modules.access.domain.enums.AccessStatus;
import com.urbanpark.parking.modules.access.domain.enums.AccessType;
import java.time.LocalDateTime;

public record ParkingAccessResponse(
    Long id,
    String tenantId,
    String plate,
    AccessType accessType,
    AccessStatus accessStatus,
    LocalDateTime accessTimestamp,
    String method,
    Long registeredByUserId,
    Long vehicleOwnerId,
    String notes,
    LocalDateTime createdAt
) {}