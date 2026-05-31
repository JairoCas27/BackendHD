package main.java.com.urbanpark.parking.integration.dto;
 
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
 
/**
 * DTOs que representan la respuesta de la API externa del condominio.
 * Se usan únicamente para sincronización (RF-05) y se mapean
 * a entidades internas del SaaS.
 *
 * @JsonIgnoreProperties(ignoreUnknown = true) para tolerancia
 * ante cambios en la API externa.
 */
public class ExternalCondominiumDtos {
 
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ExternalUserDto(
        @JsonProperty("id") String id,
        @JsonProperty("nombre") String name,
        @JsonProperty("email") String email,
        @JsonProperty("telefono") String phoneNumber,
        @JsonProperty("rol") String role,
        @JsonProperty("apartamentoId") String apartmentId,
        @JsonProperty("numeroApartamento") String apartmentNumber
    ) {}
 
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ExternalVehicleDto(
        @JsonProperty("id") String id,
        @JsonProperty("placa") String plate,
        @JsonProperty("marca") String brand,
        @JsonProperty("modelo") String model,
        @JsonProperty("color") String color,
        @JsonProperty("tipo") String type,
        @JsonProperty("usuarioId") String userId
    ) {}
 
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ExternalApartmentDto(
        @JsonProperty("id") String id,
        @JsonProperty("numero") String number,
        @JsonProperty("torre") String tower,
        @JsonProperty("piso") String floor
    ) {}
}