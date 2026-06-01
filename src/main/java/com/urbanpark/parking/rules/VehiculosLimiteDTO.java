package main.java.com.urbanpark.parking.rules;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VehiculosLimiteDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("usuario_id")
    @NotNull(message = "El ID de usuario es obligatorio")
    private Long usuarioId;

    @JsonProperty("nombre_usuario")
    @Size(max = 200)
    private String nombreUsuario;

    @JsonProperty("email_usuario")
    @Size(max = 150)
    private String emailUsuario;

    @JsonProperty("condominio_id")
    @NotNull(message = "El ID de condominio es obligatorio")
    private Long condominioId;

    @JsonProperty("nombre_condominio")
    @Size(max = 200)
    private String nombreCondominio;

    @JsonProperty("vehiculo_id")
    private Long vehiculoId;

    @JsonProperty("placa")
    @NotBlank(message = "La placa es obligatoria")
    @Pattern(regexp = "^[A-Z0-9]{3,}-?[A-Z0-9]{2,}$", message = "Formato de placa invalido")
    @Size(max = 15)
    private String placa;

    @JsonProperty("tipo_vehiculo")
    @Pattern(regexp = "AUTO|MOTO|CAMIONETA|BICICLETA|CAMION|MICROBUS|MINIVAN|OTRO", 
             message = "Tipo de vehiculo no valido")
    @Builder.Default
    private String tipoVehiculo = "AUTO";

    @JsonProperty("marca")
    @Size(max = 50)
    private String marca;

    @JsonProperty("modelo")
    @Size(max = 50)
    private String modelo;

    @JsonProperty("color")
    @Size(max = 30)
    private String color;

    @JsonProperty("anio_fabricacion")
    @Min(1900)
    private Integer anioFabricacion;

    @JsonProperty("numero_chasis")
    @Size(max = 50)
    private String numeroChasis;

    @JsonProperty("numero_motor")
    @Size(max = 50)
    private String numeroMotor;

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

    @JsonProperty("limite_horas_estacionamiento_mes")
    @Min(0)
    private Integer limiteHorasEstacionamientoMes;

    @JsonProperty("horas_estacionadas_mes")
    @Min(0)
    @Builder.Default
    private Integer horasEstacionadasMes = 0;

    @JsonProperty("espacio_asignado")
    @Size(max = 20)
    private String espacioAsignado;

    @JsonProperty("tipo_espacio_asignado")
    @Size(max = 30)
    private String tipoEspacioAsignado;

    @JsonProperty("tarjeta_acceso")
    @Size(max = 50)
    private String tarjetaAcceso;

    @JsonProperty("codigo_qr")
    @Size(max = 500)
    private String codigoQr;

    @JsonProperty("codigo_barras")
    @Size(max = 100)
    private String codigoBarras;

    @JsonProperty("dispositivo_ble")
    @Size(max = 50)
    private String dispositivoBle;

    @JsonProperty("notas")
    @Size(max = 500)
    private String notas;

    @JsonProperty("foto_vehiculo_url")
    @Size(max = 300)
    private String fotoVehiculoUrl;

    @JsonProperty("documentos_verificados")
    @Builder.Default
    private Boolean documentosVerificados = false;

    @JsonProperty("fecha_verificacion_documentos")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaVerificacionDocumentos;

    @JsonProperty("verificado_por")
    @Size(max = 100)
    private String verificadoPor;

    @JsonProperty("historial_accesos")
    @Builder.Default
    private List<HistorialAccesoVehiculo> historialAccesos = new ArrayList<>();

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
        if (limiteHorasEstacionamientoMes != null && horasEstacionadasMes >= limiteHorasEstacionamientoMes) return false;
        return true;
    }

    public boolean tieneLimiteMensual() {
        return limiteMensualAccesos != null && limiteMensualAccesos > 0;
    }

    public boolean tieneLimiteHorasMensual() {
        return limiteHorasEstacionamientoMes != null && limiteHorasEstacionamientoMes > 0;
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

    public boolean tieneCodigoBarras() {
        return codigoBarras != null && !codigoBarras.isEmpty();
    }

    public boolean tieneDispositivoBle() {
        return dispositivoBle != null && !dispositivoBle.isEmpty();
    }

    public boolean documentosVerificados() {
        return Boolean.TRUE.equals(documentosVerificados);
    }

    public boolean necesitaVerificacion() {
        return !documentosVerificados();
    }

    public void registrarEntrada(String metodoAcceso, String espacio, String guardia) {
        this.estaDentro = true;
        this.ultimoAcceso = LocalDateTime.now();
        this.contadorAccesosMes++;
        this.contadorAccesosTotal++;
        this.accesosHoy++;
        agregarHistorial("ENTRADA", metodoAcceso, espacio, guardia);
    }

    public void registrarSalida(String metodoAcceso, String guardia) {
        this.estaDentro = false;
        this.ultimaSalida = LocalDateTime.now();
        if (this.ultimoAcceso != null) {
            long horas = java.time.Duration.between(this.ultimoAcceso, this.ultimaSalida).toHours();
            this.horasEstacionadasMes += (int) horas;
        }
        agregarHistorial("SALIDA", metodoAcceso, null, guardia);
    }

    public void resetearContadorMensual() {
        this.contadorAccesosMes = 0;
        this.horasEstacionadasMes = 0;
    }

    public void resetearContadorDiario() {
        this.accesosHoy = 0;
    }

    public void marcarComoPrincipal() {
        this.esPrincipal = true;
    }

    public void desactivar(String motivoDesactivacion) {
        this.estaActivo = false;
        if (Boolean.TRUE.equals(this.estaDentro)) {
            this.ultimaSalida = LocalDateTime.now();
            this.estaDentro = false;
        }
        this.notas = (this.notas != null ? this.notas + "; " : "") + "Desactivado: " + motivoDesactivacion;
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

    public boolean esBicicleta() {
        return "BICICLETA".equalsIgnoreCase(tipoVehiculo);
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

    public long horasEstacionadoActualmente() {
        if (!estaDentro() || ultimoAcceso == null) return 0;
        return java.time.Duration.between(ultimoAcceso, LocalDateTime.now()).toHours();
    }

    public boolean excedeTiempoMaximo(int horasMaximas) {
        return horasEstacionadoActualmente() > horasMaximas;
    }

    public double calcularUsoMensualPorcentaje() {
        if (limiteHorasEstacionamientoMes == null || limiteHorasEstacionamientoMes == 0) return 0.0;
        return (horasEstacionadasMes * 100.0) / limiteHorasEstacionamientoMes;
    }

    public boolean usoMensualCercanoAlLimite(double porcentajeUmbral) {
        return calcularUsoMensualPorcentaje() >= porcentajeUmbral;
    }

    private void agregarHistorial(String tipoEvento, String metodoAcceso, String espacio, String guardia) {
        if (this.historialAccesos == null) {
            this.historialAccesos = new ArrayList<>();
        }
        this.historialAccesos.add(HistorialAccesoVehiculo.builder()
                .tipoEvento(tipoEvento)
                .metodoAcceso(metodoAcceso)
                .espacio(espacio)
                .guardia(guardia)
                .fechaEvento(LocalDateTime.now())
                .build());
    }

    // Clase interna
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HistorialAccesoVehiculo {
        private Long id;
        private String tipoEvento;  // ENTRADA, SALIDA, DENEGADO, ALERTA
        private String metodoAcceso;  // TARJETA, QR, PLACA, BIOMETRICO, MANUAL
        private String espacio;
        private String guardia;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime fechaEvento;
        private String notas;
    }
}