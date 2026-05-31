package main.java.com.urbanpark.parking.user.domain.model;
 
import com.urbanpark.parking.user.domain.model.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
 
/**
 * Vehículo registrado en el SaaS por un usuario del condominio.
 *
 * Un vehículo siempre pertenece a un usuario (owner) y a un tenant.
 * La placa es el identificador operativo principal para control de acceso.
 */
@Entity
@Table(
    name = "vehicles",
    uniqueConstraints = {
        // Una placa solo puede estar registrada una vez por condominio
        @UniqueConstraint(columnNames = {"plate", "tenant_id"})
    },
    indexes = {
        @Index(name = "idx_vehicle_tenant", columnList = "tenant_id"),
        @Index(name = "idx_vehicle_plate_tenant", columnList = "plate, tenant_id"),
        @Index(name = "idx_vehicle_owner", columnList = "owner_user_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(name = "tenant_id", nullable = false)
    private String tenantId;
 
    /**
     * Placa del vehículo. Identificador principal para acceso.
     * Se almacena en mayúsculas para búsquedas consistentes.
     */
    @Column(nullable = false, length = 20)
    private String plate;
 
    @Column
    private String brand;
 
    @Column
    private String model;
 
    @Column
    private String color;
 
    @Column
    private String type; // sedan, suv, moto, camioneta, etc.
 
    /**
     * Propietario del vehículo dentro del SaaS.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private User owner;
 
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
 
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
 
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
 
    @PrePersist
    protected void onCreate() {
        // Normalizamos placa a mayúsculas antes de persistir
        if (this.plate != null) {
            this.plate = this.plate.toUpperCase().trim();
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
 
    @PreUpdate
    protected void onUpdate() {
        if (this.plate != null) {
            this.plate = this.plate.toUpperCase().trim();
        }
        this.updatedAt = LocalDateTime.now();
    }
}