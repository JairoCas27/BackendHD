package com.urbanpark.parking.model;

import com.urbanpark.parking.enums.TipoPlaza;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "plazas")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plaza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String numero;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPlaza tipo;

    @Column(nullable = false)
    private Integer condominioId;

    @Column(nullable = false)
    private boolean ocupada = false;

    @Column(nullable = false)
    private boolean activa = true;
}