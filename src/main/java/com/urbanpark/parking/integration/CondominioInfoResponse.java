package main.java.com.urbanpark.parking.integration;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CondominioInfoResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("direccion")
    private String direccion;

    @JsonProperty("telefono")
    private String telefono;

    @JsonProperty("email")
    private String email;

    @JsonProperty("total_espacios")
    private Integer totalEspacios;

    @JsonProperty("espacios_disponibles")
    private Integer espaciosDisponibles;

    @JsonProperty("espacios_ocupados")
    private Integer espaciosOcupados;

    @JsonProperty("estado")
    private String estado;

    @JsonProperty("creado_en")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creadoEn;

    @JsonProperty("actualizado_en")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actualizadoEn;

    @JsonProperty("servicios")
    @Builder.Default
    private List<String> servicios = new ArrayList<>();

    @JsonProperty("configuracion")
    private Map<String, Object> configuracion;

    @JsonProperty("administrador")
    private AdministradorInfo administrador;

    @JsonProperty("coordenadas")
    private Coordenadas coordenadas;

    @JsonProperty("horario_atencion")
    private HorarioAtencion horarioAtencion;

    @JsonProperty("tarifas")
    @Builder.Default
    private List<TarifaInfo> tarifas = new ArrayList<>();

    // Métodos de utilidad
    public boolean estaActivo() {
        return "ACTIVO".equalsIgnoreCase(this.estado);
    }

    public boolean estaEnMantenimiento() {
        return "MANTENIMIENTO".equalsIgnoreCase(this.estado);
    }

    public double porcentajeOcupacion() {
        if (totalEspacios == null || totalEspacios == 0) {
            return 0.0;
        }
        int ocupados = espaciosOcupados != null ? espaciosOcupados : (totalEspacios - espaciosDisponibles);
        return (ocupados * 100.0) / totalEspacios;
    }

    public boolean tieneServicio(String servicio) {
        return servicios != null && servicios.stream()
                .anyMatch(s -> s.equalsIgnoreCase(servicio));
    }

    public boolean tieneEspaciosDisponibles() {
        return espaciosDisponibles != null && espaciosDisponibles > 0;
    }

    // Clases internas
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AdministradorInfo {
        private Long id;
        private String nombre;
        private String email;
        private String telefono;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Coordenadas {
        private Double latitud;
        private Double longitud;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HorarioAtencion {
        private String diaInicio;
        private String diaFin;
        private String horaApertura;
        private String horaCierre;
        private Boolean atencion24Horas;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TarifaInfo {
        private String tipoVehiculo;
        private Double tarifaPorHora;
        private Double tarifaPorDia;
        private Double tarifaMensual;
        private String moneda;
    }
}