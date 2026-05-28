package com.urbanpark.parking.model;

import com.urbanpark.parking.enums.MetodoAcceso;
import com.urbanpark.parking.enums.TipoOcupante;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "logs_acceso_vehicular")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogAccesoVehicular {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String placa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plaza_id")
    private Plaza plaza;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoOcupante tipoOcupante;

    private Integer apartamentoId;

    private String vehiculoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetodoAcceso metodo;

    @Column(nullable = false)
    private LocalDateTime timestampEntrada;

    private LocalDateTime timestampSalida;

    @Column(nullable = false)
    private String agenteId;

    @Column(nullable = false)
    private Integer condominioId;
}