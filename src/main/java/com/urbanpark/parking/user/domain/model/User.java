package com.urbanpark.parking.user.domain.model;
 
import com.urbanpark.parking.user.domain.enums.UserRole;
import com.urbanpark.parking.user.domain.enums.UserStatus;
import com.urbanpark.parking.vehicle.domain.model.Vehicle;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
 
/**
 * Entidad central de usuario en el SaaS.
 *
 * Representa a cualquier actor operativo (propietario, inquilino, agente de seguridad,
 * admin de condominio) que fue sincronizado o validado desde el sistema externo
 * del condominio.
 *
 * Un usuario siempre pertenece a un tenant (condominio) y puede tener
 * vehículos y visitantes asociados.
 */
@Entity
@Table(
    name = "users",
    uniqueConstraints = {
        // Un usuario externo solo puede existir una vez por condominio
        @UniqueConstraint(columnNames = {"external_id", "tenant_id"})
    },
    indexes = {
        @Index(name = "idx_user_tenant", columnList = "tenant_id"),
        @Index(name = "idx_user_external", columnList = "external_id, tenant_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    /**
     * ID del usuario en el sistema externo del condominio.
     * Se usa para sincronización y evitar duplicados.
     */
    @Column(name = "external_id", nullable = false)
    private String externalId;
 
    /**
     * ID del condominio al que pertenece este usuario (multi-tenant).
     */
    @Column(name = "tenant_id", nullable = false)
    private String tenantId;
 
    @Column(nullable = false)
    private String name;
 
    @Column(nullable = false)
    private String email;
 
    @Column(name = "phone_number")
    private String phoneNumber;
 
    /**
     * Número de unidad / apartamento asociado (puede ser null para agentes).
     */
    @Column(name = "apartment_number")
    private String apartmentNumber;
 
    /**
     * ID del apartamento en el sistema externo (para sincronización).
     */
    @Column(name = "external_apartment_id")
    private String externalApartmentId;
 
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;
 
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;
 
    /**
     * Vehículos registrados por este usuario en el SaaS.
     */
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Vehicle> vehicles = new ArrayList<>();
 
    /**
     * Visitantes autorizados por este usuario.
     */
    @OneToMany(mappedBy = "authorizedBy", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Visitor> visitors = new ArrayList<>();
 
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
 
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
 
    @Column(name = "last_synced_at")
    private LocalDateTime lastSyncedAt;
 
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
 
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}