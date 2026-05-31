package com.urbanpark.parking.vehicle.dto.request;
 
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
 
public record CreateVehicleRequest(
 
    @NotBlank(message = "La placa es obligatoria")
    @Size(min = 2, max = 20, message = "La placa debe tener entre 2 y 20 caracteres")
    String plate,
 
    @Size(max = 50)
    String brand,
 
    @Size(max = 50)
    String model,
 
    @Size(max = 30)
    String color,
 
    @Size(max = 30)
    String type
) {}