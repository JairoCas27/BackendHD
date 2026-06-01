package com.urbanpark.parking.modules.parking;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "estacionamientos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EstacionamientoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "condominio_id", nullable = false)
    private String condominioId; 

    @Column(name = "apartamento_id")
    private String apartamentoId; 

    @Column(nullable = false, length = 50)
    private String codigo; 

    @Column(nullable = false, length = 50)
    private String sector; 

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoParking estado = EstadoParking.DISPONIBLE;

    @Column(name = "creado_el", updatable = false)
    private LocalDateTime creadoEl = LocalDateTime.now();

    public enum EstadoParking {
        DISPONIBLE,
        OCUPADO,
        MANTENIMIENTO
    }
}