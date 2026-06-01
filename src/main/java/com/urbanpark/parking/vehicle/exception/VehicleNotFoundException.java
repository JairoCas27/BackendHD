package main.java.com.urbanpark.parking.vehicle.exception;
 
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
 
@ResponseStatus(HttpStatus.NOT_FOUND)
public class VehicleNotFoundException extends RuntimeException {
    public VehicleNotFoundException(Long id) {
        super("Vehículo no encontrado con id: " + id);
    }
    public VehicleNotFoundException(String plate, String tenantId) {
        super("Vehículo con placa '" + plate + "' no encontrado en el condominio '" + tenantId + "'");
    }
}
 