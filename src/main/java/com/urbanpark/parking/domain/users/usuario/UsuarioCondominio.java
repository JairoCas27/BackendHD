package com.urbanpark.parking.domain.users.usuario;

import com.urbanpark.parking.shared.enums.RolParking;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "usuarios_condominio",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"external_id", "tenant_id"}
        )
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioCondominio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "external_id", nullable = false)
    private String externalId;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol_parking", nullable = false)
    private RolParking rolParking;

    @Column(nullable = false)
    private boolean activo;

    @Column(name = "synced_at")
    private LocalDateTime syncedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}