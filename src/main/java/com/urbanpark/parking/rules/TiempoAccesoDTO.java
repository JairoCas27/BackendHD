package main.java.com.urbanpark.parking.rules;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TiempoAccesoDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("condominio_id")
    @NotNull(message = "El ID de condominio es obligatorio")
    private Long condominioId;

    @JsonProperty("nombre_condominio")
    private String nombreCondominio;

    @JsonProperty("tipo_usuario")
    @NotBlank(message = "El tipo de usuario es obligatorio")
    @Pattern(regexp = "RESIDENTE|VISITANTE|EMPLEADO|ADMIN|GUARDA|PROPIETARIO", 
             message = "Tipo de usuario no valido")
    private String tipoUsuario;

    @JsonProperty("dia_semana")
    @NotBlank
    @Pattern(regexp = "LUNES|MARTES|MIERCOLES|JUEVES|VIERNES|SABADO|DOMINGO|LUN_VIE|FIN_DE_SEMANA|TODOS", 
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

    @JsonProperty("limite_duracion_horas")
    @Builder.Default
    private Integer limiteDuracionHoras = 24;

    @JsonProperty("notas")
    private String notas;

    @JsonProperty("prioridad")
    @Builder.Default
    private Integer prioridad = 1;

    @JsonProperty("activo")
    @Builder.Default
    private Boolean activo = true;

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

    public boolean tieneLimiteDuracion() {
        return limiteDuracionHoras != null && limiteDuracionHoras > 0;
    }

    public boolean esHorarioNocturno() {
        return horaInicio.isAfter(LocalTime.of(18, 0)) || 
               horaFin.isBefore(LocalTime.of(6, 0));
    }

    public boolean esFinDeSemana() {
        return "SABADO".equals(diaSemana) || "DOMINGO".equals(diaSemana) || 
               "FIN_DE_SEMANA".equals(diaSemana);
    }

    public boolean esDiaLaboral() {
        return "LUNES".equals(diaSemana) || "MARTES".equals(diaSemana) || 
               "MIERCOLES".equals(diaSemana) || "JUEVES".equals(diaSemana) || 
               "VIERNES".equals(diaSemana) || "LUN_VIE".equals(diaSemana);
    }

    public long duracionMinutos() {
        if (horaFin.isBefore(horaInicio)) {
            return java.time.Duration.between(horaInicio, LocalTime.MAX).toMinutes() +
                   java.time.Duration.between(LocalTime.MIN, horaFin).toMinutes() + 1;
        }
        return java.time.Duration.between(horaInicio, horaFin).toMinutes();
    }

    private boolean coincideDiaSemana(DayOfWeek dia) {
        if ("TODOS".equals(diaSemana)) return true;
        if ("LUN_VIE".equals(diaSemana)) {
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
                .limiteDuracionHoras(4)
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
                .prioridad(10)
                .build();
    }
}
