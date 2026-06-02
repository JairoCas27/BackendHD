package com.urbanpark.parking.domain.parking_management;

import com.urbanpark.parking.shared.enums.EstadoEspacio;
import com.urbanpark.parking.shared.enums.TipoEspacio;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "espacios_parking",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"codigo", "tenant_id"}
        )
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EspacioParking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private String codigo;

    @Column(nullable = false)
    private String zona;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEspacio tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEspacio estado;

    @Column(name = "vehiculo_actual_id")
    private UUID vehiculoActualId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}