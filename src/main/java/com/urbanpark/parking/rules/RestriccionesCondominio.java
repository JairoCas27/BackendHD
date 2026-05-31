package main.java.com.urbanpark.parking.rules;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "restricciones_condominio")
@Data
public class RestriccionesCondominio {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "condominio_id")
    private Long condominioId;
    
    @Column(name = "max_parqueos_por_apartamento")
    private Integer maxParqueosPorApartamento;
    
    @Column(name = "max_visitantes_simultaneos")
    private Integer maxVisitantesSimultaneos;
    
    @Column(name = "tiempo_maximo_visita_horas")
    private Integer tiempoMaximoVisitaHoras;
    
    @Column(name = "costo_parqueo_visitante")
    private BigDecimal costoParqueoVisitante;
    
    @Column(name = "requiere_reserva_previa")
    private Boolean requiereReservaPrevia;
    
    @Column(name = "permite_acceso_24h")
    private Boolean permiteAcceso24h;
    
    private Boolean activo;
}
