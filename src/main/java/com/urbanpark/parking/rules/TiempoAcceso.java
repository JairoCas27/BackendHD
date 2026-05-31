package main.java.com.urbanpark.parking.rules;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "tiempo_acceso")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TiempoAcceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "condominio_id", nullable = false)
    private Long condominioId;

    @Column(name = "tipo_usuario", nullable = false, length = 30)
    private String tipoUsuario;  // RESIDENTE, VISITANTE, EMPLEADO, ADMIN

    @Column(name = "dia_semana", nullable = false, length = 10)
    private String diaSemana;  // LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO, DOMINGO

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Column(name = "acceso_permitido", nullable = false)
    private Boolean accesoPermitido;

    @Column(name = "requiere_autorizacion", nullable = false)
    private Boolean requiereAutorizacion;

    @Column(name = "notas", length = 255)
    private String notas;

    // Métodos de negocio
    public boolean estaEnHorarioPermitido(LocalDateTime fechaHora) {
        DayOfWeek dia = fechaHora.getDayOfWeek();
        String diaActual = convertirDiaSemana(dia);
        
        if (!this.diaSemana.equals(diaActual)) {
            return false;
        }

        LocalTime hora = fechaHora.toLocalTime();
        return !hora.isBefore(horaInicio) && !hora.isAfter(horaFin) && accesoPermitido;
    }

    public boolean necesitaAutorizacion() {
        return requiereAutorizacion;
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

    public static TiempoAcceso crearHorarioResidente(Long condominioId, String dia, LocalTime inicio, LocalTime fin) {
        return TiempoAcceso.builder()
                .condominioId(condominioId)
                .tipoUsuario("RESIDENTE")
                .diaSemana(dia)
                .horaInicio(inicio)
                .horaFin(fin)
                .accesoPermitido(true)
                .requiereAutorizacion(false)
                .build();
    }

    public static TiempoAcceso crearHorarioVisitante(Long condominioId, String dia, LocalTime inicio, LocalTime fin) {
        return TiempoAcceso.builder()
                .condominioId(condominioId)
                .tipoUsuario("VISITANTE")
                .diaSemana(dia)
                .horaInicio(inicio)
                .horaFin(fin)
                .accesoPermitido(true)
                .requiereAutorizacion(true)
                .build();
    }
}