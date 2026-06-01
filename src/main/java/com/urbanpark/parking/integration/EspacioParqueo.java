package main.java.com.urbanpark.parking.integration;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EspacioParqueo {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("codigo")
    private String codigo;

    @JsonProperty("tipo")
    private String tipo;

    @JsonProperty("estado")
    private String estado;

    @JsonProperty("disponible")
    private Boolean disponible;

    @JsonProperty("condominio_id")
    private Long condominioId;

    @JsonProperty("ubicacion")
    private String ubicacion;

    @JsonProperty("nivel")
    private String nivel;

    @JsonProperty("seccion")
    private String seccion;

    @JsonProperty("dimensiones")
    private String dimensiones;

    @JsonProperty("ancho_metros")
    private Double anchoMetros;

    @JsonProperty("largo_metros")
    private Double largoMetros;

    @JsonProperty("es_accesible")
    private Boolean esAccesible;

    @JsonProperty("tiene_sombra")
    private Boolean tieneSombra;

    @JsonProperty("tiene_carga_electrica")
    private Boolean tieneCargaElectrica;

    @JsonProperty("ocupado_por")
    private OcupanteInfo ocupadoPor;

    @JsonProperty("reservado_hasta")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reservadoHasta;

    @JsonProperty("ultima_actualizacion")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime ultimaActualizacion;

    @JsonProperty("notas")
    private String notas;

    // Métodos de utilidad
    public boolean estaDisponible() {
        return this.disponible != null && this.disponible && 
               !estaReservado() && !estaOcupado();
    }

    public boolean estaOcupado() {
        return this.ocupadoPor != null;
    }

    public boolean estaReservado() {
        return this.reservadoHasta != null && 
               this.reservadoHasta.isAfter(LocalDateTime.now());
    }

    public boolean esParaAuto() {
        return "AUTO".equalsIgnoreCase(this.tipo);
    }

    public boolean esParaMoto() {
        return "MOTO".equalsIgnoreCase(this.tipo);
    }

    public boolean esParaDiscapacitados() {
        return "DISCAPACITADO".equalsIgnoreCase(this.tipo) || 
               Boolean.TRUE.equals(this.esAccesible);
    }

    public boolean esParaVisitante() {
        return "VISITANTE".equalsIgnoreCase(this.tipo);
    }

    public boolean tieneCargaElectrica() {
        return Boolean.TRUE.equals(this.tieneCargaElectrica);
    }

    public double areaMetrosCuadrados() {
        if (anchoMetros == null || largoMetros == null) return 0.0;
        return anchoMetros * largoMetros;
    }

    public boolean puedeAlojar(String tipoVehiculo) {
        if (this.tipo == null) return true;
        return this.tipo.equalsIgnoreCase(tipoVehiculo) || 
               (esParaAuto() && "CAMIONETA".equalsIgnoreCase(tipoVehiculo));
    }

    public void marcarComoOcupado(OcupanteInfo ocupante) {
        this.ocupadoPor = ocupante;
        this.disponible = false;
        this.ultimaActualizacion = LocalDateTime.now();
    }

    public void marcarComoDisponible() {
        this.ocupadoPor = null;
        this.disponible = true;
        this.reservadoHasta = null;
        this.ultimaActualizacion = LocalDateTime.now();
    }

    public void reservarHasta(LocalDateTime fechaHora) {
        this.reservadoHasta = fechaHora;
        this.ultimaActualizacion = LocalDateTime.now();
    }

    // Clase interna
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OcupanteInfo {
        private Long usuarioId;
        private String nombreUsuario;
        private String placaVehiculo;
        private String tipoVehiculo;
        private LocalDateTime horaEntrada;
    }
}