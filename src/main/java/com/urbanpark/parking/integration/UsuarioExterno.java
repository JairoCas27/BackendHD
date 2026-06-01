package main.java.com.urbanpark.parking.integration;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioExterno {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("email")
    private String email;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("apellido")
    private String apellido;

    @JsonProperty("nombre_completo")
    public String getNombreCompleto() {
        return String.format("%s %s", nombre != null ? nombre : "", 
                apellido != null ? apellido : "").trim();
    }

    @JsonProperty("telefono")
    private String telefono;

    @JsonProperty("telefono_emergencia")
    private String telefonoEmergencia;

    @JsonProperty("tipo_usuario")
    private String tipoUsuario;

    @JsonProperty("condominio_id")
    private Long condominioId;

    @JsonProperty("departamento")
    private String departamento;

    @JsonProperty("torre")
    private String torre;

    @JsonProperty("activo")
    private Boolean activo;

    @JsonProperty("email_verificado")
    private Boolean emailVerificado;

    @JsonProperty("fecha_nacimiento")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;

    @JsonProperty("fecha_registro")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaRegistro;

    @JsonProperty("ultima_actualizacion")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime ultimaActualizacion;

    @JsonProperty("ultimo_acceso")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime ultimoAcceso;

    @JsonProperty("vehiculos_registrados")
    @Builder.Default
    private List<VehiculoInfo> vehiculosRegistrados = new ArrayList<>();

    @JsonProperty("permisos")
    @Builder.Default
    private List<String> permisos = new ArrayList<>();

    @JsonProperty("estado_cuenta")
    private String estadoCuenta;

    @JsonProperty("saldo_pendiente")
    private Double saldoPendiente;

    // Métodos de utilidad
    public boolean esResidente() {
        return "RESIDENTE".equalsIgnoreCase(this.tipoUsuario);
    }

    public boolean esAdministrador() {
        return "ADMIN".equalsIgnoreCase(this.tipoUsuario) || 
               "ADMINISTRADOR".equalsIgnoreCase(this.tipoUsuario);
    }

    public boolean esGuarda() {
        return "GUARDA".equalsIgnoreCase(this.tipoUsuario) || 
               "SEGURIDAD".equalsIgnoreCase(this.tipoUsuario);
    }

    public boolean esVisitante() {
        return "VISITANTE".equalsIgnoreCase(this.tipoUsuario);
    }

    public boolean estaActivo() {
        return this.activo != null && this.activo;
    }

    public boolean tieneEmailVerificado() {
        return this.emailVerificado != null && this.emailVerificado;
    }

    public boolean tieneSaldoPendiente() {
        return this.saldoPendiente != null && this.saldoPendiente > 0;
    }

    public boolean tieneVehiculosRegistrados() {
        return this.vehiculosRegistrados != null && !this.vehiculosRegistrados.isEmpty();
    }

    public int cantidadVehiculos() {
        return this.vehiculosRegistrados != null ? this.vehiculosRegistrados.size() : 0;
    }

    public boolean tienePermiso(String permiso) {
        return this.permisos != null && this.permisos.stream()
                .anyMatch(p -> p.equalsIgnoreCase(permiso));
    }

    public boolean tieneAccesoReciente() {
        if (this.ultimoAcceso == null) return false;
        return this.ultimoAcceso.isAfter(LocalDateTime.now().minusDays(30));
    }

    // Clase interna
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VehiculoInfo {
        private Long id;
        private String placa;
        private String tipo;
        private String marca;
        private String modelo;
        private String color;
        private Boolean activo;
    }
}