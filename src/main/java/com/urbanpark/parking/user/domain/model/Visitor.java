package com.urbanpark.parking.user.domain.model;
 
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
 
/**
 * Visitante temporal autorizado por un propietario/inquilino.
 *
 * Un visitante tiene acceso acotado en tiempo y está siempre
 * asociado al usuario que lo autorizó y al condominio (tenant).
 */
@Entity
@Table(
    name = "visitors",
    indexes = {
        @Index(name = "idx_visitor_tenant", columnList = "tenant_id"),
        @Index(name = "idx_visitor_authorized_by", columnList = "authorized_by_user_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Visitor {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(name = "tenant_id", nullable = false)
    private String tenantId;
 
    @Column(nullable = false)
    private String name;
 
    @Column(name = "id_document")
    private String idDocument;
 
    /**
     * Placa del vehículo del visitante (puede ser null si viene a pie).
     */
    @Column(name = "vehicle_plate")
    private String vehiclePlate;
 
    @Column(name = "vehicle_description")
    private String vehicleDescription;
 
    /**
     * Usuario (propietario/inquilino) que autorizó esta visita.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authorized_by_user_id", nullable = false)
    private User authorizedBy;
 
    /**
     * Fecha/hora desde la que el visitante puede ingresar.
     */
    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;
 
    /**
     * Fecha/hora hasta la que el visitante puede ingresar.
     */
    @Column(name = "valid_until", nullable = false)
    private LocalDateTime validUntil;
 
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
 
    @Column
    private String notes;
 
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
 
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
 
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
 
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
 
    public boolean isCurrentlyValid() {
        LocalDateTime now = LocalDateTime.now();
        return Boolean.TRUE.equals(isActive)
            && now.isAfter(validFrom)
            && now.isBefore(validUntil);
    }
}
