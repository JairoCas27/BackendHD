package main.java.com.urbanpark.parking.rules;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
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
public class VehiculosLimiteDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("usuario_id")
    @NotNull(message = "El ID de usuario es obligatorio")
    private Long usuarioId;

    @JsonProperty("nombre_usuario")
    private String nombreUsuario;

    @JsonProperty("condominio_id")
    @NotNull(message = "El ID de condominio es obligatorio")
    private Long condominioId;

    @JsonProperty("vehiculo_id")
    private Long vehiculoId;

    @JsonProperty("placa")
    @NotBlank(message = "La placa es obligatoria")
    @Pattern(regexp = "^[A-Z0-9]{3,}-?[A-Z0-9]{2,}$", message = "Formato de placa invalido")
    private String placa;

    @JsonProperty("tipo_vehiculo")
    @Pattern(regexp = "AUTO|MOTO|CAMIONETA|BICICLETA|CAMION|MICROBUS|OTRO", 
             message = "Tipo de vehiculo no valido")
    @Builder.Default
    private String tipoVehiculo = "AUTO";

    @JsonProperty("marca")
    private String marca;

    @JsonProperty("modelo")
    private String modelo;

    @JsonProperty("color")
    private String color;

    @JsonProperty("es_principal")
    @Builder.Default
    private Boolean esPrincipal = false;

    @JsonProperty("esta_activo")
    @Builder.Default
    private Boolean estaActivo = true;

    @JsonProperty("esta_dentro")
    @Builder.Default
    private Boolean estaDentro = false;

    @JsonProperty("fecha_registro")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaRegistro;

    @JsonProperty("ultimo_acceso")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime ultimoAcceso;

    @JsonProperty("ultima_salida")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime ultimaSalida;

    @JsonProperty("contador_accesos_mes")
    @Min(0)
    @Builder.Default
    private Integer contadorAccesosMes = 0;

    @JsonProperty("contador_accesos_total")
    @Min(0)
    @Builder.Default
    private Integer contadorAccesosTotal = 0;

    @JsonProperty("limite_mensual_accesos")
    @Min(0)
    private Integer limiteMensualAccesos;

    @JsonProperty("limite_diario_accesos")
    @Min(0)
    @Builder.Default
    private Integer limiteDiarioAccesos = 10;

    @JsonProperty("accesos_hoy")
    @Min(0)
    @Builder.Default
    private Integer accesosHoy = 0;

    @JsonProperty("espacio_asignado")
    private String espacioAsignado;

    @JsonProperty("tarjeta_acceso")
    private String tarjetaAcceso;

    @JsonProperty("codigo_qr")
    private String codigoQr;

    @JsonProperty("notas")
    private String notas;

    // Métodos de utilidad
    public boolean estaActivo() {
        return this.estaActivo != null && this.estaActivo;
    }

    public boolean esPrincipal() {
        return this.esPrincipal != null && this.esPrincipal;
    }

    public boolean estaDentro() {
        return this.estaDentro != null && this.estaDentro;
    }

    public boolean puedeAcceder() {
        if (!estaActivo()) return false;
        if (limiteMensualAccesos != null && contadorAccesosMes >= limiteMensualAccesos) return false;
        if (limiteDiarioAccesos != null && accesosHoy >= limiteDiarioAccesos) return false;
        return true;
    }

    public boolean tieneLimiteMensual() {
        return limiteMensualAccesos != null && limiteMensualAccesos > 0;
    }

    public boolean tieneEspacioAsignado() {
        return espacioAsignado != null && !espacioAsignado.isEmpty();
    }

    public boolean tieneTarjetaAcceso() {
        return tarjetaAcceso != null && !tarjetaAcceso.isEmpty();
    }

    public boolean tieneCodigoQr() {
        return codigoQr != null && !codigoQr.isEmpty();
    }

    public void registrarEntrada() {
        this.estaDentro = true;
        this.ultimoAcceso = LocalDateTime.now();
        this.contadorAccesosMes++;
        this.contadorAccesosTotal++;
        this.accesosHoy++;
    }

    public void registrarSalida() {
        this.estaDentro = false;
        this.ultimaSalida = LocalDateTime.now();
    }

    public void resetearContadorMensual() {
        this.contadorAccesosMes = 0;
    }

    public void resetearContadorDiario() {
        this.accesosHoy = 0;
    }

    public void marcarComoPrincipal() {
        this.esPrincipal = true;
    }

    public void desactivar() {
        this.estaActivo = false;
        if (Boolean.TRUE.equals(this.estaDentro)) {
            this.ultimaSalida = LocalDateTime.now();
            this.estaDentro = false;
        }
    }

    public void reactivar() {
        this.estaActivo = true;
    }

    public boolean esVehiculoActivoPrincipal() {
        return estaActivo() && esPrincipal();
    }

    public boolean esAuto() {
        return "AUTO".equalsIgnoreCase(tipoVehiculo);
    }

    public boolean esMoto() {
        return "MOTO".equalsIgnoreCase(tipoVehiculo);
    }

    public boolean esCamioneta() {
        return "CAMIONETA".equalsIgnoreCase(tipoVehiculo);
    }

    public String obtenerEstadoActual() {
        if (!estaActivo()) return "INACTIVO";
        if (estaDentro()) return "DENTRO";
        return "FUERA";
    }

    public long minutosDesdeUltimoAcceso() {
        if (ultimoAcceso == null) return -1;
        return java.time.Duration.between(ultimoAcceso, LocalDateTime.now()).toMinutes();
    }
}