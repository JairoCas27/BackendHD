package com.urbanpark.parking.domain.saas.plan;

import com.urbanpark.parking.shared.enums.EstadoPlan;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "planes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "max_espacios", nullable = false)
    private int maxEspacios;

    @Column(name = "max_usuarios", nullable = false)
    private int maxUsuarios;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPlan estado;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}