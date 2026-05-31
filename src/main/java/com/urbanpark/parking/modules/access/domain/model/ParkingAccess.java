package com.urbanpark.parking.modules.access.domain.model;

import com.urbanpark.parking.modules.access.domain.enums.AccessStatus;
import com.urbanpark.parking.modules.access.domain.enums.AccessType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "parking_access", indexes = {
    @Index(name = "idx_access_tenant", columnList = "tenant_id"),
    @Index(name = "idx_access_plate", columnList = "plate"),
    @Index(name = "idx_access_timestamp", columnList = "access_timestamp")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "plate", nullable = false, length = 20)
    private String plate;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_type", nullable = false)
    private AccessType accessType; // ENTRADA, SALIDA

    @Enumerated(EnumType.STRING)
    @Column(name = "access_status", nullable = false)
    private AccessStatus accessStatus; // APROBADO, DENEGADO, MANUAL

    @Column(name = "access_timestamp", nullable = false)
    private LocalDateTime accessTimestamp;

    @Column(name = "method", nullable = false)
    private String method; // AUTOMATIC, MANUAL

    @Column(name = "registered_by_user_id")
    private Long registeredByUserId; // ID del usuario que registró (si manual)

    @Column(name = "vehicle_owner_id")
    private Long vehicleOwnerId; // ID del propietario del vehículo (si existe)

    @Column(length = 500)
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.accessTimestamp == null) {
            this.accessTimestamp = LocalDateTime.now();
        }
        if (this.plate != null) {
            this.plate = this.plate.toUpperCase().trim();
        }
    }
}