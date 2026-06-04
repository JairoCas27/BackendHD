// domain/audit/AuditLog.java
package com.urbanpark.parking.domain.audit;

import com.urbanpark.parking.shared.enums.TipoAccionAudit;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_usuario",  columnList = "usuario_saas_id"),
        @Index(name = "idx_audit_accion",   columnList = "accion"),
        @Index(name = "idx_audit_fecha",    columnList = "fecha_hora")
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quién ejecutó la acción (null = acción anónima/sistema)
    @Column(name = "usuario_saas_id")
    private Long usuarioSaasId;

    @Column(name = "usuario_email", length = 150)
    private String usuarioEmail;

    @Column(name = "rol_usuario", length = 50)
    private String rolUsuario;

    // Qué hizo
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 60)
    private TipoAccionAudit accion;

    // Detalle legible
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    // Recurso afectado (ej: "Condominio#12", "UsuarioSaas#3")
    @Column(name = "entidad_afectada", length = 100)
    private String entidadAfectada;

    // HTTP context
    @Column(name = "endpoint", length = 255)
    private String endpoint;

    @Column(name = "metodo_http", length = 10)
    private String metodoHttp;

    @Column(name = "ip_origen", length = 45)
    private String ipOrigen;

    // Resultado
    @Column(nullable = false)
    private boolean exitoso;

    @Column(name = "detalle_error", columnDefinition = "TEXT")
    private String detalleError;

    @Column(name = "fecha_hora", nullable = false, updatable = false)
    private LocalDateTime fechaHora;

    @PrePersist
    public void prePersist() {
        this.fechaHora = LocalDateTime.now();
    }
}