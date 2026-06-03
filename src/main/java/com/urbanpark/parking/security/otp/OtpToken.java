package com.urbanpark.parking.security.otp;

import com.urbanpark.parking.domain.usuarios.UsuarioSaas;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "otp_tokens")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class OtpToken {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioSaas usuario;

    @Column(nullable = false)
    private String codigoHash;

    @Column(nullable = false)
    private LocalDateTime expiraEn;

    @Column(nullable = false)
    private boolean usado;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    public void prePersist() { this.fechaCreacion = LocalDateTime.now(); }
}