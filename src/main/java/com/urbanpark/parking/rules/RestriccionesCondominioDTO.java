package main.java.com.urbanpark.parking.rules;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestriccionesCondominioDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("condominio_id")
    @NotNull(message = "El ID de condominio es obligatorio")
    private Long condominioId;

    @JsonProperty("nombre_condominio")
    private String nombreCondominio;

    @JsonProperty("max_vehiculos_por_usuario")
    @NotNull
    @Min(value = 1, message = "Minimo 1 vehiculo")
    @Max(value = 10, message = "Maximo 10 vehiculos")
    @Builder.Default
    private Integer maxVehiculosPorUsuario = 2;

    @JsonProperty("max_visitas_por_dia")
    @NotNull
    @Min(value = 1)
    @Max(value = 50)
    @Builder.Default
    private Integer maxVisitasPorDia = 10;

    @JsonProperty("max_visitas_simultaneas")
    @Min(value = 1)
    @Max(value = 20)
    @Builder.Default
    private Integer maxVisitasSimultaneas = 5;

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

    @JsonProperty("permite_visitas_fin_de_semana")
    @Builder.Default
    private Boolean permiteVisitasFinDeSemana = true;

    @JsonProperty("permite_visitas_feriados")
    @Builder.Default
    private Boolean permiteVisitasFeriados = true;

    @JsonProperty("tiempo_maximo_estacionamiento_horas")
    @Min(1)
    @Max(72)
    @Builder.Default
    private Integer tiempoMaximoEstacionamientoHoras = 24;

    @JsonProperty("tiempo_gracia_minutos")
    @Min(0)
    @Max(60)
    @Builder.Default
    private Integer tiempoGraciaMinutos = 15;

    @JsonProperty("requiere_reserva_previa")
    @Builder.Default
    private Boolean requiereReservaPrevia = false;

    @JsonProperty("requiere_aprobacion_guardia")
    @Builder.Default
    private Boolean requiereAprobacionGuardia = false;

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

    @JsonProperty("moneda")
    @Builder.Default
    private String moneda = "PEN";

    @JsonProperty("activo")
    @Builder.Default
    private Boolean activo = true;

    @JsonProperty("excepciones")
    @Builder.Default
    private List<ExcepcionRestriccion> excepciones = new ArrayList<>();

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

    public boolean puedeRegistrarVehiculo(int vehiculosActuales) {
        return vehiculosActuales < maxVehiculosPorUsuario;
    }

    public boolean permiteVisita(int visitasDelDia, boolean esFinDeSemana, boolean esFeriado) {
        if (esFinDeSemana && !Boolean.TRUE.equals(permiteVisitasFinDeSemana)) return false;
        if (esFeriado && !Boolean.TRUE.equals(permiteVisitasFeriados)) return false;
        return visitasDelDia < maxVisitasPorDia;
    }

    public boolean permiteVisitaSimultanea(int visitasActuales) {
        return visitasActuales < maxVisitasSimultaneas;
    }

    public double calcularTarifa(boolean esVisitante, int horas, boolean esResidenteMensual) {
        if (esResidenteMensual) return 0.0;
        if (esVisitante) return tarifaVisitantePorHora * horas;
        return tarifaPorHora * horas;
    }

    public boolean requiereReserva() {
        return Boolean.TRUE.equals(requiereReservaPrevia);
    }

    public boolean requiereAprobacion() {
        return Boolean.TRUE.equals(requiereAprobacionGuardia);
    }

    public long minutosHastaCierre(LocalTime horaActual) {
        if (horaActual.isAfter(horaCierre)) return 0;
        return java.time.Duration.between(horaActual, horaCierre).toMinutes();
    }

    public boolean tieneExcepciones() {
        return excepciones != null && !excepciones.isEmpty();
    }

    // Clase interna
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ExcepcionRestriccion {
        private String tipo;  // FECHA_ESPECIFICA, EVENTO_ESPECIAL, USUARIO_ESPECIFICO
        private String descripcion;
        private String fechaInicio;
        private String fechaFin;
        private Boolean permiteAcceso;
        private String horarioEspecialInicio;
        private String horarioEspecialFin;
    }
}
