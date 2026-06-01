package main.java.com.urbanpark.parking.rules;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PermisoUsuarioDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("usuario_id")
    @NotNull(message = "El ID de usuario es obligatorio")
    private Long usuarioId;

    @JsonProperty("nombre_usuario")
    @Size(max = 200)
    private String nombreUsuario;

    @JsonProperty("email_usuario")
    @Size(max = 150)
    private String emailUsuario;

    @JsonProperty("tipo_permiso")
    @NotBlank(message = "El tipo de permiso es obligatorio")
    @Pattern(regexp = "CREAR_RESERVA|VER_REPORTES|ADMINISTRAR|GESTIONAR_USUARIOS|GESTIONAR_VEHICULOS|CONFIGURAR_SISTEMA|VER_HISTORIAL|EXPORTAR_DATOS|APROBAR_ACCESOS|GESTIONAR_TARIFAS|CONFIGURAR_REGLAS|SUPER_ADMIN", 
             message = "Tipo de permiso no valido")
    private String tipoPermiso;

    @JsonProperty("recurso")
    @NotBlank(message = "El recurso es obligatorio")
    @Pattern(regexp = "PARQUEO|VEHICULO|USUARIO|CONDOMINIO|SISTEMA|REPORTE|RESERVA|TARIFA|REGLA|NOTIFICACION", 
             message = "Recurso no valido")
    private String recurso;

    @JsonProperty("nivel_acceso")
    @Pattern(regexp = "LECTURA|ESCRITURA|ELIMINACION|ADMINISTRACION|EJECUCION", 
             message = "Nivel de acceso no valido")
    @Builder.Default
    private String nivelAcceso = "LECTURA";

    @JsonProperty("alcance")
    @Pattern(regexp = "PROPIO|DEPARTAMENTO|TORRE|CONDOMINIO|GLOBAL|MULTI_CONDOMINIO", 
             message = "Alcance no valido")
    @Builder.Default
    private String alcance = "PROPIO";

    @JsonProperty("condominio_id")
    private Long condominioId;

    @JsonProperty("nombre_condominio")
    private String nombreCondominio;

    @JsonProperty("departamento_id")
    private Long departamentoId;

    @JsonProperty("torre_id")
    private Long torreId;

    @JsonProperty("activo")
    @Builder.Default
    private Boolean activo = true;

    @JsonProperty("fecha_inicio")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaInicio;

    @JsonProperty("fecha_expiracion")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaExpiracion;

    @JsonProperty("creado_en")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creadoEn;

    @JsonProperty("actualizado_en")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actualizadoEn;

    @JsonProperty("creado_por")
    @Size(max = 100)
    private String creadoPor;

    @JsonProperty("actualizado_por")
    @Size(max = 100)
    private String actualizadoPor;

    @JsonProperty("motivo")
    @Size(max = 500)
    private String motivo;

    @JsonProperty("ip_creacion")
    @Size(max = 45)
    private String ipCreacion;

    @JsonProperty("ip_ultima_modificacion")
    @Size(max = 45)
    private String ipUltimaModificacion;

    @JsonProperty("condiciones_adicionales")
    @Builder.Default
    private List<CondicionPermiso> condicionesAdicionales = new ArrayList<>();

    @JsonProperty("metadatos")
    @Builder.Default
    private Map<String, Object> metadatos = new HashMap<>();

    @JsonProperty("historial_cambios")
    @Builder.Default
    private List<HistorialCambioPermiso> historialCambios = new ArrayList<>();

    // Métodos de utilidad
    public boolean estaActivo() {
        return this.activo != null && this.activo;
    }

    public boolean estaVigente() {
        if (!estaActivo()) return false;
        LocalDateTime ahora = LocalDateTime.now();
        if (fechaInicio != null && ahora.isBefore(fechaInicio)) return false;
        if (fechaExpiracion != null && ahora.isAfter(fechaExpiracion)) return false;
        return true;
    }

    public boolean estaExpirado() {
        return fechaExpiracion != null && LocalDateTime.now().isAfter(fechaExpiracion);
    }

    public boolean expiraPronto(int dias) {
        if (fechaExpiracion == null) return false;
        return fechaExpiracion.isBefore(LocalDateTime.now().plusDays(dias));
    }

    public boolean expiraPronto() {
        return expiraPronto(7);
    }

    public boolean tienePermiso(String tipoPermisoRequerido, String recursoRequerido) {
        return estaVigente() 
            && this.tipoPermiso.equalsIgnoreCase(tipoPermisoRequerido) 
            && this.recurso.equalsIgnoreCase(recursoRequerido);
    }

    public boolean tienePermisoConNivel(String tipoPermisoRequerido, String recursoRequerido, String nivelRequerido) {
        return tienePermiso(tipoPermisoRequerido, recursoRequerido) 
            && tieneNivelSuficiente(nivelRequerido);
    }

    public boolean tieneNivelSuficiente(String nivelRequerido) {
        Map<String, Integer> jerarquia = Map.of(
            "LECTURA", 1,
            "ESCRITURA", 2,
            "ELIMINACION", 3,
            "EJECUCION", 4,
            "ADMINISTRACION", 5
        );
        Integer nivelActual = jerarquia.getOrDefault(this.nivelAcceso, 0);
        Integer nivelReq = jerarquia.getOrDefault(nivelRequerido, 0);
        return nivelActual >= nivelReq;
    }

    public boolean esPermanente() {
        return this.fechaExpiracion == null;
    }

    public boolean esTemporal() {
        return !esPermanente();
    }

    public boolean tieneAlcanceGlobal() {
        return "GLOBAL".equalsIgnoreCase(this.alcance) || "MULTI_CONDOMINIO".equalsIgnoreCase(this.alcance);
    }

    public boolean tieneAlcancePropio() {
        return "PROPIO".equalsIgnoreCase(this.alcance);
    }

    public boolean tieneCondiciones() {
        return this.condicionesAdicionales != null && !this.condicionesAdicionales.isEmpty();
    }

    public boolean tieneMetadatos() {
        return this.metadatos != null && !this.metadatos.isEmpty();
    }

    public boolean tieneHistorial() {
        return this.historialCambios != null && !this.historialCambios.isEmpty();
    }

    public void desactivar(String motivoDesactivacion, String usuarioDesactiva, String ip) {
        this.activo = false;
        this.motivo = motivoDesactivacion;
        this.actualizadoEn = LocalDateTime.now();
        this.actualizadoPor = usuarioDesactiva;
        this.ipUltimaModificacion = ip;
        agregarHistorial("DESACTIVACION", motivoDesactivacion, usuarioDesactiva);
    }

    public void activar(String motivoActivacion, String usuarioActiva, String ip) {
        this.activo = true;
        this.motivo = motivoActivacion;
        this.actualizadoEn = LocalDateTime.now();
        this.actualizadoPor = usuarioActiva;
        this.ipUltimaModificacion = ip;
        agregarHistorial("ACTIVACION", motivoActivacion, usuarioActiva);
    }

    public void renovar(LocalDateTime nuevaFechaExpiracion, String usuarioRenueva, String ip) {
        this.fechaExpiracion = nuevaFechaExpiracion;
        this.actualizadoEn = LocalDateTime.now();
        this.actualizadoPor = usuarioRenueva;
        this.ipUltimaModificacion = ip;
        agregarHistorial("RENOVACION", "Nueva expiracion: " + nuevaFechaExpiracion, usuarioRenueva);
    }

    public void agregarCondicion(CondicionPermiso condicion) {
        if (this.condicionesAdicionales == null) {
            this.condicionesAdicionales = new ArrayList<>();
        }
        this.condicionesAdicionales.add(condicion);
    }

    public void agregarMetadato(String clave, Object valor) {
        if (this.metadatos == null) {
            this.metadatos = new HashMap<>();
        }
        this.metadatos.put(clave, valor);
    }

    private void agregarHistorial(String tipoCambio, String descripcion, String usuario) {
        if (this.historialCambios == null) {
            this.historialCambios = new ArrayList<>();
        }
        this.historialCambios.add(HistorialCambioPermiso.builder()
                .tipoCambio(tipoCambio)
                .descripcion(descripcion)
                .usuario(usuario)
                .fechaCambio(LocalDateTime.now())
                .build());
    }

    public long diasHastaExpiracion() {
        if (fechaExpiracion == null) return -1;
        return java.time.Duration.between(LocalDateTime.now(), fechaExpiracion).toDays();
    }

    // Clases internas
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CondicionPermiso {
        private String id;
        private String tipo;  // HORARIO, UBICACION, LIMITE_USO, APROBACION_REQUERIDA, IP_RESTRINGIDA, DOBLE_FACTOR
        private String descripcion;
        private String valor;
        private String operador;  // IGUAL, MAYOR_QUE, MENOR_QUE, CONTIENE, ENTRE
        private Boolean obligatoria;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime creadoEn;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HistorialCambioPermiso {
        private Long id;
        private String tipoCambio;  // CREACION, MODIFICACION, DESACTIVACION, ACTIVACION, RENOVACION, ELIMINACION
        private String descripcion;
        private String usuario;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime fechaCambio;
        private String ipAddress;
        private Map<String, Object> cambiosAnteriores;
    }
}