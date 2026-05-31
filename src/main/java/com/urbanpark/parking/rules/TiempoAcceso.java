package main.java.com.urbanpark.parking.rules;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalTime;

@Entity
@Table(name = "tiempo_acceso")
@Data
public class TiempoAcceso {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "hora_inicio")
    private LocalTime horaInicio;
    
    @Column(name = "hora_fin")
    private LocalTime horaFin;
    
    @Column(name = "dia_semana")
    private String diaSemana;  // Ej: "LUNES", "MARTES", "TODOS"
    
    @Column(name = "tipo_acceso")
    private String tipoAcceso;  // Ej: "ENTRADA", "SALIDA", "AMBOS"
    
    @Column(name = "condominio_id")
    private Long condominioId;
    
    private Boolean activo;
}