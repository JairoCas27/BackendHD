package main.java.com.urbanpark.parking.rules;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestriccionesCondominioDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("condominio_id")
    @NotNull(message = "El ID de condominio es obligatorio")
    private Long condominioId;

    @JsonProperty("nombre_condominio")
    @Size(max = 200)
    private String nombreCondominio;

    @JsonProperty("direccion_condominio")
    @Size(max = 300)
    private String direccionCondominio;

    @JsonProperty("max_vehiculos_por_usuario")
    @NotNull
    @Min(value = 1, message = "Minimo 1 vehiculo")
    @Max(value = 20, message = "Maximo 20 vehiculos")
    @Builder.Default
    private Integer maxVehiculosPorUsuario = 2;

    @JsonProperty("max_vehiculos_por_departamento")
    @Min(value = 1)
    @Max(value = 30)
    @Builder.Default
    private Integer maxVehiculosPorDepartamento = 5;

    @JsonProperty("max_visitas_por_dia")
    @NotNull
    @Min(value = 1)
    @Max(value = 100)
    @Builder.Default
    private Integer maxVisitasPorDia = 10;

    @JsonProperty("max_visitas_simultaneas")
    @Min(value = 1)
    @Max(value = 50)
    @Builder.Default
    private Integer maxVisitasSimultaneas = 5;

    @JsonProperty("max_duracion_visita_horas")
    @Min(value = 1)
    @Max(value = 72)
    @Builder.Default
    private Integer maxDuracionVisitaHoras = 12;

    @JsonProperty("hora_apertura")
    @JsonFormat(pattern = "HH:mm")
    @NotNull
    @Builder.Default
    private LocalTime horaApertura = LocalTime.of(6, 0);

    @JsonProperty("hora_cierre")
    @JsonFormat(pattern = "HH:mm")
    @NotNull
    @Builder.Default
    private LocalTime horaCierre = LocalTime.of(22, 0);

    @JsonProperty("permite_acceso_24h_residentes")
    @Builder.Default
    private Boolean permiteAcceso24hResidentes = false;

    @JsonProperty("permite_visitas_fin_de_semana")
    @Builder.Default
    private Boolean permiteVisitasFinDeSemana = true;

    @JsonProperty("permite_visitas_feriados")
    @Builder.Default
    private Boolean permiteVisitasFeriados = true;

    @JsonProperty("permite_visitas_nocturnas")
    @Builder.Default
    private Boolean permiteVisitasNocturnas = false;

    @JsonProperty("hora_inicio_visitas_nocturnas")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaInicioVisitasNocturnas;

    @JsonProperty("hora_fin_visitas_nocturnas")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaFinVisitasNocturnas;

    @JsonProperty("tiempo_maximo_estacionamiento_horas")
    @Min(1)
    @Max(168)
    @Builder.Default
    private Integer tiempoMaximoEstacionamientoHoras = 24;

    @JsonProperty("tiempo_gracia_entrada_minutos")
    @Min(0)
    @Max(120)
    @Builder.Default
    private Integer tiempoGraciaEntradaMinutos = 15;

    @JsonProperty("tiempo_gracia_salida_minutos")
    @Min(0)
    @Max(120)
    @Builder.Default
    private Integer tiempoGraciaSalidaMinutos = 15;

    @JsonProperty("requiere_reserva_previa")
    @Builder.Default
    private Boolean requiereReservaPrevia = false;

    @JsonProperty("requiere_aprobacion_guardia")
    @Builder.Default
    private Boolean requiereAprobacionGuardia = false;

    @JsonProperty("requiere_aprobacion_admin")
    @Builder.Default
    private Boolean requiereAprobacionAdmin = false;

    @JsonProperty("notificar_admin_nueva_visita")
    @Builder.Default
    private Boolean notificarAdminNuevaVisita = true;

    @JsonProperty("notificar_residente_visita")
    @Builder.Default
    private Boolean notificarResidenteVisita = true;

    @JsonProperty("tarifa_por_hora")
    @Min(0)
    @Builder.Default
    private Double tarifaPorHora = 0.0;

    @JsonProperty("tarifa_visitante_por_hora")
    @Min(0)
    @Builder.Default
    private Double tarifaVisitantePorHora = 5.0;

    @JsonProperty("tarifa_residente_mensual")
    @Min(0)
    @Builder.Default
    private Double tarifaResidenteMensual = 0.0;

    @JsonProperty("tarifa_visitante_dia_completo")
    @Min(0)
    @Builder.Default
    private Double tarifaVisitanteDiaCompleto = 30.0;

    @JsonProperty("tarifa_penalizacion_exceso_tiempo")
    @Min(0)
    @Builder.Default
    private Double tarifaPenalizacionExcesoTiempo = 10.0;

    @JsonProperty("moneda")
    @Size(max = 3)
    @Builder.Default
    private String moneda = "PEN";

    @JsonProperty("activo")
    @Builder.Default
    private Boolean activo = true;

    @JsonProperty("version")
    @Builder.Default
    private Integer version = 1;

    @JsonProperty("creado_en")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.time.LocalDateTime creadoEn;

    @JsonProperty("actualizado_en")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.time.LocalDateTime actualizadoEn;

    @JsonProperty("creado_por")
    @Size(max = 100)
    private String creadoPor;

    @JsonProperty("excepciones")
    @Builder.Default
    private List<ExcepcionRestriccion> excepciones = new ArrayList<>();

    @JsonProperty("zonas_restringidas")
    @Builder.Default
    private List<ZonaRestringida> zonasRestringidas = new ArrayList<>();

    @JsonProperty("configuracion_adicional")
    @Builder.Default
    private Map<String, Object> configuracionAdicional = new HashMap<>();

    // Métodos de utilidad
    public boolean estaActivo() {
        return this.activo != null && this.activo;
    }

    public boolean estaDentroHorarioPermitido(LocalTime hora) {
        if (hora == null) return false;
        return !hora.isBefore(horaApertura) && !hora.isAfter(horaCierre);
    }

    public boolean estaAbiertoAhora() {
        return estaDentroHorarioPermitido(LocalTime.now());
    }

    public boolean permiteAccesoResidente24h() {
        return Boolean.TRUE.equals(permiteAcceso24hResidentes);
    }

    public boolean puedeRegistrarVehiculo(int vehiculosActuales) {
        return vehiculosActuales < maxVehiculosPorUsuario;
    }

    public boolean puedeRegistrarVehiculoEnDepartamento(int vehiculosActuales) {
        return vehiculosActuales < maxVehiculosPorDepartamento;
    }

    public boolean permiteVisita(int visitasDelDia, boolean esFinDeSemana, boolean esFeriado) {
        if (esFinDeSemana && !Boolean.TRUE.equals(permiteVisitasFinDeSemana)) return false;
        if (esFeriado && !Boolean.TRUE.equals(permiteVisitasFeriados)) return false;
        return visitasDelDia < maxVisitasPorDia;
    }

    public boolean permiteVisitaSimultanea(int visitasActuales) {
        return visitasActuales < maxVisitasSimultaneas;
    }

    public boolean permiteVisitaNocturna(LocalTime hora) {
        if (!Boolean.TRUE.equals(permiteVisitasNocturnas)) return false;
        if (horaInicioVisitasNocturnas == null || horaFinVisitasNocturnas == null) return false;
        return !hora.isBefore(horaInicioVisitasNocturnas) && !hora.isAfter(horaFinVisitasNocturnas);
    }

    public boolean excedeDuracionVisitaPermitida(int horas) {
        return horas > maxDuracionVisitaHoras;
    }

    public double calcularTarifa(boolean esVisitante, int horas, boolean esResidenteMensual, boolean esDiaCompleto) {
        if (esResidenteMensual) return 0.0;
        if (esDiaCompleto && esVisitante) return tarifaVisitanteDiaCompleto;
        if (esVisitante) return tarifaVisitantePorHora * horas;
        return tarifaPorHora * horas;
    }

    public double calcularPenalizacionExcesoTiempo(int horasExceso) {
        return tarifaPenalizacionExcesoTiempo * horasExceso;
    }

    public boolean requiereReserva() {
        return Boolean.TRUE.equals(requiereReservaPrevia);
    }

    public boolean requiereAprobacion() {
        return Boolean.TRUE.equals(requiereAprobacionGuardia) || Boolean.TRUE.equals(requiereAprobacionAdmin);
    }

    public boolean requiereAprobacionAdmin() {
        return Boolean.TRUE.equals(requiereAprobacionAdmin);
    }

    public boolean notificarAdmin() {
        return Boolean.TRUE.equals(notificarAdminNuevaVisita);
    }

    public boolean notificarResidente() {
        return Boolean.TRUE.equals(notificarResidenteVisita);
    }

    public long minutosHastaCierre(LocalTime horaActual) {
        if (horaActual.isAfter(horaCierre)) return 0;
        return java.time.Duration.between(horaActual, horaCierre).toMinutes();
    }

    public boolean estaCercaCierre(LocalTime horaActual, int minutosUmbral) {
        return minutosHastaCierre(horaActual) <= minutosUmbral;
    }

    public boolean tieneExcepciones() {
        return excepciones != null && !excepciones.isEmpty();
    }

    public boolean tieneZonasRestringidas() {
        return zonasRestringidas != null && !zonasRestringidas.isEmpty();
    }

    public boolean tieneConfiguracionAdicional() {
        return configuracionAdicional != null && !configuracionAdicional.isEmpty();
    }

    public void agregarExcepcion(ExcepcionRestriccion excepcion) {
        if (this.excepciones == null) this.excepciones = new ArrayList<>();
        this.excepciones.add(excepcion);
    }

    public void incrementarVersion() {
        this.version = (this.version != null ? this.version : 0) + 1;
    }

    // Clases internas
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ExcepcionRestriccion {
        private Long id;
        private String tipo;  // FECHA_ESPECIFICA, EVENTO_ESPECIAL, USUARIO_ESPECIFICO, VEHICULO_ESPECIFICO
        private String descripcion;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private String fechaInicio;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private String fechaFin;
        private Boolean permiteAcceso;
        private String horarioEspecialInicio;
        private String horarioEspecialFin;
        private Long usuarioId;
        private String placaVehiculo;
        private String motivo;
        private String creadoPor;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ZonaRestringida {
        private Long id;
        private String codigoZona;
        private String nombreZona;
        private String tipoRestriccion;  // HORARIO, PERMANENTE, POR_USUARIO, POR_VEHICULO
        private String descripcion;
        private LocalTime horarioRestriccionInicio;
        private LocalTime horarioRestriccionFin;
        private Boolean activa;
    }
}