package com.urbanpark.parking.user.dto.response;
 
import com.urbanpark.parking.user.domain.enums.UserRole;
import com.urbanpark.parking.user.domain.enums.UserStatus;
import java.time.LocalDateTime;
 
public record UserResponse(
    Long id,
    String externalId,
    String tenantId,
    String name,
    String email,
    String phoneNumber,
    String apartmentNumber,
    UserRole role,
    UserStatus status,
    LocalDateTime createdAt,
    LocalDateTime lastSyncedAt
) {}
 