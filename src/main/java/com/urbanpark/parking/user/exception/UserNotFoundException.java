package main.java.com.urbanpark.parking.user.exception;
 
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
 
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("Usuario no encontrado con id: " + id);
    }
    public UserNotFoundException(String externalId, String tenantId) {
        super("Usuario externo '" + externalId + "' no encontrado en el condominio '" + tenantId + "'");
    }
}
 