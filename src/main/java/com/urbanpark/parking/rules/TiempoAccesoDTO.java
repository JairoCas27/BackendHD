package main.java.com.urbanpark.parking.rules;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TiempoAccesoDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("condominio_id")
    @NotNull(message = "El ID de condominio es obligatorio")
    private Long condominioId;

    @JsonProperty("nombre_condominio")
    @Size(max = 200)
    private String nombreCondominio;

    @JsonProperty("tipo_usuario")
    @NotBlank(message = "El tipo de usuario es obligatorio")
    @Pattern(regexp = "RESIDENTE|VISITANTE|EMPLEADO|ADMIN|GUARDA|PROPIETARIO|FAMILIAR|PROVEEDOR|CONDUCTOR", 
             message = "Tipo de usuario no valido")
    private String tipoUsuario;

    @JsonProperty("dia_semana")
    @NotBlank
    @Pattern(regexp = "LUNES|MARTES|MIERCOLES|JUEVES|VIERNES|SABADO|DOMINGO|LUN_VIE|FIN_DE_SEMANA|TODOS|DIAS_HABILES", 
             message = "Dia de semana no valido")
    private String diaSemana;

    @JsonProperty("hora_inicio")
    @JsonFormat(pattern = "HH:mm")
    @NotNull
    private LocalTime horaInicio;

    @JsonProperty("hora_fin")
    @JsonFormat(pattern = "HH:mm")
    @NotNull
    private LocalTime horaFin;

    @JsonProperty("acceso_permitido")
    @NotNull
    @Builder.Default
    private Boolean accesoPermitido = true;

    @JsonProperty("requiere_autorizacion")
    @Builder.Default
    private Boolean requiereAutorizacion = false;

    @JsonProperty("requiere_reserva")
    @Builder.Default
    private Boolean requiereReserva = false;

    @JsonProperty("requiere_doble_factor")
    @Builder.Default
    private Boolean requiereDobleFactor = false;

    @JsonProperty("limite_duracion_horas")
    @Builder.Default
    private Integer limiteDuracionHoras = 24;

    @JsonProperty("limite_duracion_minutos")
    @Builder.Default
    private Integer limiteDuracionMinutos = 0;

    @JsonProperty("cantidad_maxima_accesos_dia")
    @Builder.Default
    private Integer cantidadMaximaAccesosDia = 10;

    @JsonProperty("notas")
    @Size(max = 500)
    private String notas;

    @JsonProperty("prioridad")
    @Builder.Default
    private Integer prioridad = 1;

    @JsonProperty("activo")
    @Builder.Default
    private Boolean activo = true;

    @JsonProperty("version")
    @Builder.Default
    private Integer version = 1;

    @JsonProperty("creado_en")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creadoEn;

    @JsonProperty("actualizado_en")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actualizadoEn;

    @JsonProperty("creado_por")
    @Size(max = 100)
    private String creadoPor;

    @JsonProperty("periodos_excepcion")
    @Builder.Default
    private List<PeriodoExcepcion> periodosExcepcion = new ArrayList<>();

    // Métodos de utilidad
    public boolean estaActivo() {
        return this.activo != null && this.activo;
    }

    public boolean estaEnHorarioPermitido(LocalDateTime fechaHora) {
        if (!estaActivo()) return false;
        
        DayOfWeek dia = fechaHora.getDayOfWeek();
        if (!coincideDiaSemana(dia)) return false;

        LocalTime hora = fechaHora.toLocalTime();
        return !hora.isBefore(horaInicio) && !hora.isAfter(horaFin) && accesoPermitido;
    }

    public boolean estaEnHorarioPermitidoAhora() {
        return estaEnHorarioPermitido(LocalDateTime.now());
    }

    public boolean necesitaAutorizacion() {
        return Boolean.TRUE.equals(requiereAutorizacion);
    }

    public boolean necesitaReserva() {
        return Boolean.TRUE.equals(requiereReserva);
    }

    public boolean necesitaDobleFactor() {
        return Boolean.TRUE.equals(requiereDobleFactor);
    }

    public boolean tieneLimiteDuracion() {
        return (limiteDuracionHoras != null && limiteDuracionHoras > 0) || 
               (limiteDuracionMinutos != null && limiteDuracionMinutos > 0);
    }

    public long duracionTotalMinutos() {
        long horas = limiteDuracionHoras != null ? limiteDuracionHoras : 0;
        long minutos = limiteDuracionMinutos != null ? limiteDuracionMinutos : 0;
        return (horas * 60) + minutos;
    }

    public boolean esHorarioNocturno() {
        return horaInicio.isAfter(LocalTime.of(18, 0)) || horaFin.isBefore(LocalTime.of(6, 0));
    }

    public boolean esHorarioDiurno() {
        return !esHorarioNocturno();
    }

    public boolean esFinDeSemana() {
        return "SABADO".equals(diaSemana) || "DOMINGO".equals(diaSemana) || 
               "FIN_DE_SEMANA".equals(diaSemana);
    }

    public boolean esDiaLaboral() {
        return "LUNES".equals(diaSemana) || "MARTES".equals(diaSemana) || 
               "MIERCOLES".equals(diaSemana) || "JUEVES".equals(diaSemana) || 
               "VIERNES".equals(diaSemana) || "LUN_VIE".equals(diaSemana) || 
               "DIAS_HABILES".equals(diaSemana);
    }

    public boolean esPermanente() {
        return "TODOS".equals(diaSemana);
    }

    public long duracionMinutos() {
        if (horaFin.isBefore(horaInicio)) {
            return java.time.Duration.between(horaInicio, LocalTime.MAX).toMinutes() +
                   java.time.Duration.between(LocalTime.MIN, horaFin).toMinutes() + 1;
        }
        return java.time.Duration.between(horaInicio, horaFin).toMinutes();
    }

    public boolean excedeLimiteAccesos(int accesosActuales) {
        return cantidadMaximaAccesosDia != null && accesosActuales >= cantidadMaximaAccesosDia;
    }

    public boolean tieneExcepciones() {
        return periodosExcepcion != null && !periodosExcepcion.isEmpty();
    }

    public boolean estaEnPeriodoExcepcion(LocalDateTime fechaHora) {
        if (!tieneExcepciones()) return false;
        return periodosExcepcion.stream().anyMatch(p -> p.contiene(fechaHora));
    }

    private boolean coincideDiaSemana(DayOfWeek dia) {
        if ("TODOS".equals(diaSemana)) return true;
        if ("LUN_VIE".equals(diaSemana) || "DIAS_HABILES".equals(diaSemana)) {
            return dia != DayOfWeek.SATURDAY && dia != DayOfWeek.SUNDAY;
        }
        if ("FIN_DE_SEMANA".equals(diaSemana)) {
            return dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY;
        }
        return diaSemana.equals(convertirDiaSemana(dia));
    }

    private String convertirDiaSemana(DayOfWeek dia) {
        return switch (dia) {
            case MONDAY -> "LUNES";
            case TUESDAY -> "MARTES";
            case WEDNESDAY -> "MIERCOLES";
            case THURSDAY -> "JUEVES";
            case FRIDAY -> "VIERNES";
            case SATURDAY -> "SABADO";
            case SUNDAY -> "DOMINGO";
        };
    }

    // Builders estáticos
    public static TiempoAccesoDTO horarioResidente(Long condominioId, String dia, LocalTime inicio, LocalTime fin) {
        return TiempoAccesoDTO.builder()
                .condominioId(condominioId)
                .tipoUsuario("RESIDENTE")
                .diaSemana(dia)
                .horaInicio(inicio)
                .horaFin(fin)
                .accesoPermitido(true)
                .requiereAutorizacion(false)
                .requiereReserva(false)
                .requiereDobleFactor(false)
                .limiteDuracionHoras(24)
                .cantidadMaximaAccesosDia(100)
                .prioridad(5)
                .build();
    }

    public static TiempoAccesoDTO horarioVisitante(Long condominioId, String dia, LocalTime inicio, LocalTime fin) {
        return TiempoAccesoDTO.builder()
                .condominioId(condominioId)
                .tipoUsuario("VISITANTE")
                .diaSemana(dia)
                .horaInicio(inicio)
                .horaFin(fin)
                .accesoPermitido(true)
                .requiereAutorizacion(true)
                .requiereReserva(true)
                .requiereDobleFactor(false)
                .limiteDuracionHoras(4)
                .cantidadMaximaAccesosDia(2)
                .prioridad(3)
                .build();
    }

    public static TiempoAccesoDTO horarioGuardia(Long condominioId) {
        return TiempoAccesoDTO.builder()
                .condominioId(condominioId)
                .tipoUsuario("GUARDA")
                .diaSemana("TODOS")
                .horaInicio(LocalTime.of(0, 0))
                .horaFin(LocalTime.of(23, 59))
                .accesoPermitido(true)
                .requiereAutorizacion(false)
                .requiereReserva(false)
                .requiereDobleFactor(false)
                .limiteDuracionHoras(24)
                .cantidadMaximaAccesosDia(999)
                .prioridad(10)
                .build();
    }

    public static TiempoAccesoDTO horarioProveedor(Long condominioId, String dia, LocalTime inicio, LocalTime fin) {
        return TiempoAccesoDTO.builder()
                .condominioId(condominioId)
                .tipoUsuario("PROVEEDOR")
                .diaSemana(dia)
                .horaInicio(inicio)
                .horaFin(fin)
                .accesoPermitido(true)
                .requiereAutorizacion(true)
                .requiereReserva(true)
                .requiereDobleFactor(true)
                .limiteDuracionHoras(2)
                .cantidadMaximaAccesosDia(1)
                .prioridad(2)
                .build();
    }

    // Clase interna
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PeriodoExcepcion {
        private Long id;
        private String tipo;  // FERIADO, MANTENIMIENTO, EVENTO_ESPECIAL, EMERGENCIA
        private String descripcion;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime fechaInicio;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime fechaFin;
        private Boolean accesoPermitido;
        private String horarioEspecialInicio;
        private String horarioEspecialFin;
        private String creadoPor;

        public boolean contiene(LocalDateTime fechaHora) {
            return !fechaHora.isBefore(fechaInicio) && !fechaHora.isAfter(fechaFin);
        }
    }
}