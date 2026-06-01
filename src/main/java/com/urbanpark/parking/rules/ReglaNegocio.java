package main.java.com.urbanpark.parking.rules;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reglas_negocio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReglaNegocio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "tipo_regla", nullable = false, length = 50)
    private String tipoRegla;  // ACCESO, VEHICULO, RESERVA, PAGO, VISITA

    @Column(name = "condominio_id", nullable = false)
    private Long condominioId;

    @Column(name = "condicion", nullable = false, length = 500)
    private String condicion;  // expresión o lógica de la regla

    @Column(name = "accion", nullable = false, length = 100)
    private String accion;  // PERMITIR, DENEGAR, NOTIFICAR, COBRAR

    @Column(name = "prioridad", nullable = false)
    private Integer prioridad = 1;

    @Column(name = "activa", nullable = false)
    private Boolean activa = true;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @PrePersist
    protected void onCreate() {
        creadoEn = LocalDateTime.now();
    }

    public boolean evaluar(Object contexto) {
        // Lógica de evaluación según la condición
        return activa;
    }
}