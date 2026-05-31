package main.java.com.urbanpark.parking.user.mapper;
 
import com.urbanpark.parking.user.domain.model.User;
import com.urbanpark.parking.user.dto.response.UserResponse;
import org.springframework.stereotype.Component;
 
@Component
public class UserMapper {
 
    public UserResponse toResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getExternalId(),
            user.getTenantId(),
            user.getName(),
            user.getEmail(),
            user.getPhoneNumber(),
            user.getApartmentNumber(),
            user.getRole(),
            user.getStatus(),
            user.getCreatedAt(),
            user.getLastSyncedAt()
        );
    }
}