package com.urbanpark.parking.domain.parking;

import com.urbanpark.parking.shared.enums.MetodoAcceso;
import com.urbanpark.parking.shared.enums.TipoEvento;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "accesos_vehiculares")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccesoVehicular {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "vehiculo_id", nullable = false)
    private UUID vehiculoId;

    @Column(nullable = false)
    private String placa;

    @Column(name = "espacio_id")
    private UUID espacioId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evento", nullable = false)
    private TipoEvento tipoEvento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetodoAcceso metodo;

    @Column(name = "agente_id")
    private UUID agenteId;

    @Column(name = "timestamp_entrada", nullable = false)
    private LocalDateTime timestampEntrada;

    @Column(name = "timestamp_salida")
    private LocalDateTime timestampSalida;

    @Column(name = "duracion_minutos")
    private Integer duracionMinutos;

    @Column(nullable = false)
    private boolean autorizado;

    @Column(name = "motivo_rechazo")
    private String motivoRechazo;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}