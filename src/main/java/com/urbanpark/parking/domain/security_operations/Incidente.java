package com.urbanpark.parking.domain.security_operations;

import com.urbanpark.parking.shared.enums.NivelIncidente;
import com.urbanpark.parking.shared.enums.EstadoIncidente;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "incidentes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Incidente {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "agente_id", nullable = false)
    private UUID agenteId;

    @Column(name = "acceso_id")
    private UUID accesoId;

    @Column(nullable = false)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelIncidente nivel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoIncidente estado;

    @Column(name = "placa_involucrada")
    private String placaInvolucrada;

    @Column(name = "resolucion")
    private String resolucion;

    @Column(name = "resuelto_at")
    private LocalDateTime resueltoAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}