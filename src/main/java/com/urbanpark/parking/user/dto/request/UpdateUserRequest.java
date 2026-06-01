package main.java.com.urbanpark.parking.user.dto.request;
 
import com.urbanpark.parking.user.domain.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
 
/**
 * Permite actualizar campos editables del usuario dentro del SaaS.
 * Los datos de identidad (externalId, role) solo se actualizan vía sincronización.
 */
public record UpdateUserRequest(
 
    @Size(min = 2, max = 100)
    String name,
 
    @Email
    String email,
 
    @Size(max = 20)
    String phoneNumber,
 
    UserStatus status
) {}
 