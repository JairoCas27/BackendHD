package com.urbanpark.parking.visitor.dto.request;
 
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
 
public record CreateVisitorRequest(
 
    @NotBlank(message = "El nombre del visitante es obligatorio")
    String name,
 
    String idDocument,
 
    String vehiclePlate,
 
    String vehicleDescription,
 
    @NotNull(message = "La fecha de inicio de validez es obligatoria")
    LocalDateTime validFrom,
 
    @NotNull(message = "La fecha de fin de validez es obligatoria")
    @Future(message = "La fecha de expiración debe ser futura")
    LocalDateTime validUntil,
 
    String notes
) {}
 