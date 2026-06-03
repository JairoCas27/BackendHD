package com.urbanpark.parking.domain.condominios;

import com.urbanpark.parking.domain.titulares.Titular;
import com.urbanpark.parking.domain.usuarios.UsuarioSaas;
import com.urbanpark.parking.shared.enums.EstadoCondominio;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "condominios")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Condominio {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "titular_id", nullable = false)
    private Titular titular;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private String razonSocial;

    @Column(nullable = false)
    private String ruc;

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false)
    private String emailCondominio;

    @Column(nullable = false)
    private String telefonoCondominio;

    @Column(nullable = false)
    private String apiBaseUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCondominio estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verificado_por_id")
    private UsuarioSaas verificadoPor;

    private String motivoRechazo;

    private LocalDateTime fechaVerificacion;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    public void prePersist() {
        this.fechaRegistro = LocalDateTime.now();
    }
}