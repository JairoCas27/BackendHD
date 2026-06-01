package com.urbanpark.parking.integration.condominio;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccesoRequest {

    @JsonProperty("usuario_id")
    @NotBlank(message = "El ID de usuario es obligatorio")
    private String usuarioId;

    @JsonProperty("condominio_id")
    @NotBlank(message = "El ID de condominio es obligatorio")
    private String condominioId;

    @JsonProperty("tipo_acceso")
    @NotNull(message = "El tipo de acceso es obligatorio")
    @Pattern(regexp = "ENTRADA|SALIDA", message = "El tipo de acceso debe ser ENTRADA o SALIDA")
    private String tipoAcceso;

    @JsonProperty("vehiculo_id")
    private String vehiculoId;

    @JsonProperty("placa_vehiculo")
    @Pattern(regexp = "^[A-Z]{3}-\\d{3}$|^[A-Z]{2}\\d{3}[A-Z]{2}$", 
             message = "Formato de placa invalido")
    private String placaVehiculo;

    @JsonProperty("tipo_vehiculo")
    @Pattern(regexp = "AUTO|MOTO|CAMIONETA|BICICLETA|OTRO", 
             message = "Tipo de vehiculo no valido")
    private String tipoVehiculo;

    @JsonProperty("espacio_asignado")
    private String espacioAsignado;

    @JsonProperty("motivo_visita")
    private String motivoVisita;

    @JsonProperty("autorizado_por")
    private String autorizadoPor;

    @JsonProperty("fecha_hora")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Builder.Default
    private LocalDateTime fechaHora = LocalDateTime.now();

    @JsonProperty("metodo_autenticacion")
    @Pattern(regexp = "TARJETA|QR|PLACA|BIOMETRICO|MANUAL", 
             message = "Metodo de autenticacion no valido")
    private String metodoAutenticacion;

    @JsonProperty("codigo_acceso")
    private String codigoAcceso;

    @JsonProperty("notas_adicionales")
    private String notasAdicionales;

    @JsonProperty("latitud")
    private Double latitud;

    @JsonProperty("longitud")
    private Double longitud;

    // Métodos de utilidad
    public boolean esEntrada() {
        return "ENTRADA".equalsIgnoreCase(this.tipoAcceso);
    }

    public boolean esSalida() {
        return "SALIDA".equalsIgnoreCase(this.tipoAcceso);
    }

    public boolean tieneVehiculoAsignado() {
        return this.vehiculoId != null && !this.vehiculoId.isEmpty();
    }

    public boolean tieneEspacioAsignado() {
        return this.espacioAsignado != null && !this.espacioAsignado.isEmpty();
    }

    public boolean esVisita() {
        return this.motivoVisita != null && !this.motivoVisita.isEmpty();
    }

    public boolean tieneCodigoAcceso() {
        return this.codigoAcceso != null && !this.codigoAcceso.isEmpty();
    }

    public boolean tieneUbicacionGPS() {
        return this.latitud != null && this.longitud != null;
    }

    // Builder personalizado para validaciones
    public static class AccesoRequestBuilder {
        public AccesoRequest build() {
            AccesoRequest request = new AccesoRequest(usuarioId, condominioId, tipoAcceso, 
                    vehiculoId, placaVehiculo, tipoVehiculo, espacioAsignado, motivoVisita, 
                    autorizadoPor, fechaHora, metodoAutenticacion, codigoAcceso, 
                    notasAdicionales, latitud, longitud);
            
            if (request.fechaHora == null) {
                request.fechaHora = LocalDateTime.now();
            }
            return request;
        }
    }
}