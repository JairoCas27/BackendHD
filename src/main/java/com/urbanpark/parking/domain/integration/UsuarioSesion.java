package com.urbanpark.parking.domain.integration;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "usuario_sesiones")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioSesion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "external_user_id", nullable = false)
    private Long externalUserId;

    @Column(name = "condominio_id", nullable = false)
    private UUID condominioId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String rol;

    // Token cookie del sistema externo — "access_token=eyJ..."
    @Column(name = "access_token", length = 2048)
    private String accessToken;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}