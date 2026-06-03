package com.urbanpark.parking.domain.notifications.contactanos;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "contacto_mensajes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactoMensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String correo;

    @Column(nullable = false, length = 1000)
    private String mensaje;

    @Column(name = "codigo_seguimiento", nullable = false, unique = true)
    private String codigoSeguimiento;

    @Column(name = "fecha_envio", nullable = false)
    private LocalDateTime fechaEnvio;

    @Column(length = 1000)
    private String respuesta;

    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;

    @Column(nullable = false)
    private boolean respondido;

    @Column(name = "usuario_respuesta_id")
    private Long usuarioRespuestaId; // Guarda el ID del ADMIN/SUPERADMIN que respondió

    @Column(name = "usuario_respuesta_email")
private String usuarioRespuestaEmail;




    @PrePersist
    protected void onCreate() {
        this.fechaEnvio = LocalDateTime.now();
        this.respondido = false;
        // Código único de seguimiento para el cliente (Ej: CON-A8F2C3D1)
        this.codigoSeguimiento = "CON-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}