package main.java.com.urbanpark.parking.rules;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "permiso_usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermisoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "tipo_permiso", nullable = false, length = 50)
    private String tipoPermiso;  // CREAR_RESERVA, VER_REPORTES, ADMINISTRAR, GESTIONAR_USUARIOS

    @Column(name = "recurso", nullable = false, length = 50)
    private String recurso;  // PARQUEO, VEHICULO, USUARIO, CONDOMINIO

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    @Column(name = "creado_por")
    private String creadoPor;

    @PrePersist
    protected void onCreate() {
        creadoEn = LocalDateTime.now();
        actualizadoEn = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        actualizadoEn = LocalDateTime.now();
    }

    // Métodos de negocio
    public boolean tienePermiso(String tipoPermisoRequerido, String recursoRequerido) {
        return this.activo 
            && this.tipoPermiso.equals(tipoPermisoRequerido) 
            && this.recurso.equals(recursoRequerido);
    }

    public void desactivar() {
        this.activo = false;
    }

    public void activar() {
        this.activo = true;
    }
}