package main.java.com.urbanpark.parking.rules;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehiculos_limite")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehiculosLimite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "condominio_id", nullable = false)
    private Long condominioId;

    @Column(name = "vehiculo_id", nullable = false)
    private Long vehiculoId;

    @Column(name = "placa", nullable = false, length = 15)
    private String placa;

    @Column(name = "tipo_vehiculo", nullable = false, length = 30)
    private String tipoVehiculo;  // AUTO, MOTO, CAMIONETA, BICICLETA

    @Column(name = "es_principal", nullable = false)
    private Boolean esPrincipal = false;

    @Column(name = "esta_activo", nullable = false)
    private Boolean estaActivo = true;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    @Column(name = "contador_accesos_mes", nullable = false)
    private Integer contadorAccesosMes = 0;

    @Column(name = "limite_mensual_accesos")
    private Integer limiteMensualAccesos;

    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }

    // Métodos de negocio
    public void registrarAcceso() {
        this.ultimoAcceso = LocalDateTime.now();
        this.contadorAccesosMes++;
    }

    public boolean puedeAcceder() {
        if (!estaActivo) {
            return false;
        }
        if (limiteMensualAccesos != null) {
            return contadorAccesosMes < limiteMensualAccesos;
        }
        return true;
    }

    public void resetearContadorMensual() {
        this.contadorAccesosMes = 0;
    }

    public void marcarComoPrincipal() {
        this.esPrincipal = true;
    }

    public void desactivar() {
        this.estaActivo = false;
    }

    public void reactivar() {
        this.estaActivo = true;
    }

    public boolean esVehiculoActivoPrincipal() {
        return estaActivo && esPrincipal;
    }
}