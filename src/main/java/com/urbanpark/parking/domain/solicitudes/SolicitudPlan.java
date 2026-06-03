package com.urbanpark.parking.domain.solicitudes;

import com.urbanpark.parking.domain.planes.Plan;
import com.urbanpark.parking.domain.titulares.Titular;
import com.urbanpark.parking.domain.usuarios.UsuarioSaas;
import com.urbanpark.parking.shared.enums.EstadoSolicitud;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes_plan")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class SolicitudPlan {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "titular_id", nullable = false)
    private Titular titular;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSolicitud estado;

    private String motivoRechazo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "revisado_por_id")
    private UsuarioSaas revisadoPor;

    private LocalDateTime fechaRevision;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaSolicitud;

    @PrePersist
    public void prePersist() {
        this.fechaSolicitud = LocalDateTime.now();
    }
}