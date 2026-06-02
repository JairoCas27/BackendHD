package com.urbanpark.parking.domain.tenant;

import com.urbanpark.parking.domain.saas.plan.Plan;
import com.urbanpark.parking.shared.enums.EstadoCondominio;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "condominios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Condominio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "api_base_url", nullable = false)
    private String apiBaseUrl;

    @Column(name = "titular_nombre", nullable = false)
    private String titularNombre;

    @Column(name = "titular_email", nullable = false, unique = true)
    private String titularEmail;

    @Column(name = "titular_telefono")
    private String titularTelefono;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCondominio estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @CreationTimestamp
    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;
}