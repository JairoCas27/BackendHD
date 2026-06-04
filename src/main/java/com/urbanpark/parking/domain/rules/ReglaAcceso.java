package com.urbanpark.parking.domain.rules;

import com.urbanpark.parking.domain.condominios.Condominio;
import com.urbanpark.parking.shared.enums.TipoRegla;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reglas_acceso")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ReglaAcceso {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "condominio_id", nullable = false)
    private Condominio condominio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoRegla tipo;

    @Column(nullable = false)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    /** JSON con parámetros según el tipo.*/
    @Column(nullable = false, columnDefinition = "TEXT")
    private String configuracion;

    @Column(nullable = false)
    @Builder.Default
    private boolean activa = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}
