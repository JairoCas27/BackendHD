package com.urbanpark.parking.domain.usuarios;

import com.urbanpark.parking.shared.enums.EstadoUsuarioSaas;
import com.urbanpark.parking.shared.enums.OrigenRegistro;
import com.urbanpark.parking.shared.enums.RolSaas;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios_saas")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class UsuarioSaas {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String nombres;

    @Column(nullable = false)
    private String apellidos;

    @Column(nullable = false, unique = true)
    private String dni;

    @Column(nullable = false)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolSaas rol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoUsuarioSaas estado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrigenRegistro origenRegistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por_id")
    private UsuarioSaas creadoPor;

    @Column(nullable = false)
    private boolean esBaseProtegido;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    public void prePersist() {
        this.fechaRegistro = LocalDateTime.now();
    }

    public String getNombreCompleto() {
        return this.nombres + " " + this.apellidos;
    }
}