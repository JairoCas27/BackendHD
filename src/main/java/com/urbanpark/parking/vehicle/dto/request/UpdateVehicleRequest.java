package main.java.com.urbanpark.parking.vehicle.dto.request;
 
import jakarta.validation.constraints.Size;
 
public record UpdateVehicleRequest(
 
    @Size(max = 50)
    String brand,
 
    @Size(max = 50)
    String model,
 
    @Size(max = 30)
    String color,
 
    @Size(max = 30)
    String type,
 
    Boolean isActive
) {}
 