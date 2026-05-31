package main.java.com.urbanpark.parking.rules;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "vehiculos_limite")
@Data
public class VehiculosLimite {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "max_vehiculos")
    private Integer maxVehiculos;
    
    @Column(name = "tipo_usuario")
    private String tipoUsuario;  // Ej: "RESIDENTE", "VISITANTE", "EMPLEADO"
    
    @Column(name = "condominio_id")
    private Long condominioId;
    
    private Boolean activo;
}