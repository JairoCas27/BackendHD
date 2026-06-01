package main.java.com.urbanpark.parking.rules;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Table(name = "restricciones_condominio") //Reglas y Restricciones del condominio
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestriccionesCondominio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "condominio_id", nullable = false, unique = true)
    private Long condominioId;

    @Column(name = "max_vehiculos_por_usuario", nullable = false)
    private Integer maxVehiculosPorUsuario;

    @Column(name = "max_visitas_por_dia", nullable = false)
    private Integer maxVisitasPorDia;

    @Column(name = "hora_apertura", nullable = false)
    private LocalTime horaApertura;

    @Column(name = "hora_cierre", nullable = false)
    private LocalTime horaCierre;

    @Column(name = "permite_visitas_fin_de_semana", nullable = false)
    private Boolean permiteVisitasFinDeSemana;

    @Column(name = "tiempo_maximo_estacionamiento_horas", nullable = false)
    private Integer tiempoMaximoEstacionamientoHoras;

    @Column(name = "requiere_reserva_previa", nullable = false)
    private Boolean requiereReservaPrevia;

    @Column(name = "tarifa_por_hora", nullable = false)
    private Double tarifaPorHora;

    @Column(name = "tarifa_visitante_por_hora", nullable = false)
    private Double tarifaVisitantePorHora;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    // Métodos de negocio
    public boolean estaDentroHorarioPermitido(LocalTime hora) {
        return !hora.isBefore(horaApertura) && !hora.isAfter(horaCierre);
    }

    public boolean puedeRegistrarVehiculo(int vehiculosActuales) {
        return vehiculosActuales < maxVehiculosPorUsuario;
    }

    public boolean permiteVisita(int visitasDelDia, boolean esFinDeSemana) {
        if (esFinDeSemana && !permiteVisitasFinDeSemana) {
            return false;
        }
        return visitasDelDia < maxVisitasPorDia;
    }

    public double calcularTarifa(boolean esVisitante, int horas) {
        if (esVisitante) {
            return tarifaVisitantePorHora * horas;
        }
        return tarifaPorHora * horas;
    }
}