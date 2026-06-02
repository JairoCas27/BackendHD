package com.urbanpark.parking.domain.users.visitante;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "visitantes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Visitante {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "propietario_id", nullable = false)
    private UUID propietarioId;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "placa_vehiculo")
    private String placaVehiculo;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDateTime fechaFin;

    @Column(nullable = false)
    private boolean activo;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}