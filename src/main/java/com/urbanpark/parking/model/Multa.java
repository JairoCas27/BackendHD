package com.urbanpark.parking.model;

import com.urbanpark.parking.enums.EstadoMulta;
import com.urbanpark.parking.enums.TipoInfraccion;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "multas")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Multa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "log_acceso_id")
    private LogAccesoVehicular logAcceso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoInfraccion tipoInfraccion;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal monto;

    private Integer apartamentoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoMulta estado = EstadoMulta.PENDIENTE;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String generadaPor;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(nullable = false)
    private Integer condominioId;
}