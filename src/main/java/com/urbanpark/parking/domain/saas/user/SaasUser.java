package com.urbanpark.parking.domain.saas.user;

import com.urbanpark.parking.shared.enums.RolSaas;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "saas_users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaasUser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nombre;

    @Column(unique = true)
    private String dni;

    private String telefono;

    private String cargo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolSaas rol;

    @Column(nullable = false)
    private boolean activo;

    @Column(name = "es_base", nullable = false)
    private boolean esBase;   // true = no se puede eliminar

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}