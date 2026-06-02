package com.urbanpark.parking.domain.integration;

import com.urbanpark.parking.shared.enums.EstadoSync;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sync_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private String tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSync estado;

    @Column(name = "registros_procesados")
    private int registrosProcesados;

    @Column(name = "registros_creados")
    private int registrosCreados;

    @Column(name = "registros_actualizados")
    private int registrosActualizados;

    @Column(name = "mensaje_error")
    private String mensajeError;

    @Column(name = "duracion_ms")
    private long duracionMs;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}