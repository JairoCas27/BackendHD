package com.urbanpark.parking.domain.titulares;

import com.urbanpark.parking.domain.condominios.Condominio;
import com.urbanpark.parking.domain.planes.Plan;
import com.urbanpark.parking.domain.usuarios.UsuarioSaas;
import com.urbanpark.parking.shared.enums.EstadoPlan;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "titulares")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Titular {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_saas_id", nullable = false, unique = true)
    private UsuarioSaas usuarioSaas;

    @Column(nullable = false)
    private String razonSocial;

    @Column(nullable = false, unique = true)
    private String ruc;

    @Column(nullable = false)
    private String direccionFiscal;

    @Column(nullable = false)
    private String representanteLegal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @Enumerated(EnumType.STRING)
    private EstadoPlan estadoPlan;

    private LocalDateTime fechaAsignacionPlan;

    @OneToMany(mappedBy = "titular", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Condominio> condominios = new ArrayList<>();
}