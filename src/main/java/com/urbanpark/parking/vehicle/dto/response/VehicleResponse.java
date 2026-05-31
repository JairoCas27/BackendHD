package main.java.com.urbanpark.parking.vehicle.dto.response;
 
import java.time.LocalDateTime;
 
public record VehicleResponse(
    Long id,
    String tenantId,
    String plate,
    String brand,
    String model,
    String color,
    String type,
    Long ownerId,
    String ownerName,
    Boolean isActive,
    LocalDateTime createdAt
) {}
 