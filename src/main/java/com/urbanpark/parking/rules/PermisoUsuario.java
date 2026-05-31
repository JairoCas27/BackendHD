package main.java.com.urbanpark.parking.rules;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "permiso_usuario")
@Data
public class PermisoUsuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "usuario_id")
    private Long usuarioId;
    
    @Column(name = "tipo_permiso")
    private String tipoPermiso;  // Ej: "CREAR_RESERVA", "VER_REPORTES", "ADMINISTRAR"
    
    @Column(name = "recurso")
    private String recurso;  // Ej: "PARQUEO", "VEHICULO", "USUARIO"
    
    @Column(name = "puede_leer")
    private Boolean puedeLeer;
    
    @Column(name = "puede_escribir")
    private Boolean puedeEscribir;
    
    @Column(name = "puede_eliminar")
    private Boolean puedeEliminar;
    
    private Boolean activo;
}
