package main.java.com.urbanpark.parking.rules;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermisoUsuarioDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("usuario_id")
    @NotNull(message = "El ID de usuario es obligatorio")
    private Long usuarioId;

    @JsonProperty("nombre_usuario")
    private String nombreUsuario;

    @JsonProperty("tipo_permiso")
    @NotBlank(message = "El tipo de permiso es obligatorio")
    @Pattern(regexp = "CREAR_RESERVA|VER_REPORTES|ADMINISTRAR|GESTIONAR_USUARIOS|GESTIONAR_VEHICULOS|CONFIGURAR_SISTEMA|VER_HISTORIAL|EXPORTAR_DATOS", 
             message = "Tipo de permiso no valido")
    private String tipoPermiso;

    @JsonProperty("recurso")
    @NotBlank(message = "El recurso es obligatorio")
    @Pattern(regexp = "PARQUEO|VEHICULO|USUARIO|CONDOMINIO|SISTEMA|REPORTE|RESERVA", 
             message = "Recurso no valido")
    private String recurso;

    @JsonProperty("nivel_acceso")
    @Pattern(regexp = "LECTURA|ESCRITURA|ELIMINACION|ADMINISTRACION", 
             message = "Nivel de acceso no valido")
    @Builder.Default
    private String nivelAcceso = "LECTURA";

    @JsonProperty("alcance")
    @Pattern(regexp = "PROPIO|DEPARTAMENTO|TORRE|CONDOMINIO|GLOBAL", 
             message = "Alcance no valido")
    @Builder.Default
    private String alcance = "PROPIO";

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

    @JsonProperty("creado_por")
    private String creadoPor;

    @JsonProperty("motivo")
    private String motivo;

    @JsonProperty("condiciones_adicionales")
    @Builder.Default
    private List<CondicionPermiso> condicionesAdicionales = new ArrayList<>();

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

    public boolean tienePermiso(String tipoPermisoRequerido, String recursoRequerido) {
        return estaVigente() 
            && this.tipoPermiso.equalsIgnoreCase(tipoPermisoRequerido) 
            && this.recurso.equalsIgnoreCase(recursoRequerido);
    }

    public boolean tieneNivelSuficiente(String nivelRequerido) {
        Map<String, Integer> jerarquia = Map.of(
            "LECTURA", 1,
            "ESCRITURA", 2,
            "ELIMINACION", 3,
            "ADMINISTRACION", 4
        );
        Integer nivelActual = jerarquia.getOrDefault(this.nivelAcceso, 0);
        Integer nivelReq = jerarquia.getOrDefault(nivelRequerido, 0);
        return nivelActual >= nivelReq;
    }

    public boolean esPermanente() {
        return this.fechaExpiracion == null;
    }

    public boolean expiraPronto() {
        if (fechaExpiracion == null) return false;
        return fechaExpiracion.isBefore(LocalDateTime.now().plusDays(7));
    }

    public boolean tieneCondiciones() {
        return this.condicionesAdicionales != null && !this.condicionesAdicionales.isEmpty();
    }

    public void desactivar(String motivo) {
        this.activo = false;
        this.motivo = motivo;
    }

    // Clase interna
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CondicionPermiso {
        private String tipo;  // HORARIO, UBICACION, LIMITE_USO, APROBACION_REQUERIDA
        private String descripcion;
        private String valor;
        private Boolean obligatoria;
    }
}