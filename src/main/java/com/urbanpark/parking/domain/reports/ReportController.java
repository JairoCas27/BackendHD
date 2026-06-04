package com.urbanpark.parking.domain.reports;

import com.urbanpark.parking.domain.reports.dto.AdminClientesStatsDTO;
import com.urbanpark.parking.domain.reports.dto.GlobalStatsDTO;
import com.urbanpark.parking.domain.reports.dto.ReporteDetalladoDTO;
import com.urbanpark.parking.domain.reports.dto.TitularStatsDTO;
import com.urbanpark.parking.domain.reports.dto.TopPlanesStatsDTO;
import com.urbanpark.parking.domain.usuarios.UsuarioSaasRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes y Estadísticas", description = "Endpoints para obtener estadísticas del sistema")
@SecurityRequirement(name = "bearerAuth")
public class ReportController {

    private final ReportService reportService;
    private final UsuarioSaasRepository usuarioSaasRepository;

    /**
     * Obtiene estadísticas globales del sistema.
     * Solo accesible para usuarios con rol SUPERADMIN.
     *
     * @return Estadísticas globales (total usuarios por rol)
     */
    @GetMapping("/estadisticas-globales")
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(
        summary = "Obtener estadísticas globales",
        description = "Retorna el total de usuarios por rol (SUPERADMIN, ADMIN, CLIENTE). " +
                     "Solo accesible para usuarios con rol SUPERADMIN."
    )
    public ResponseEntity<GlobalStatsDTO> obtenerEstadisticasGlobales() {
        GlobalStatsDTO stats = reportService.obtenerEstadisticasGlobales();
        return ResponseEntity.ok(stats);
    }

    /**
     * Obtiene estadísticas personales del titular (cliente).
     * Cada usuario solo puede ver sus propias estadísticas.
     *
     * @param authentication Información del usuario autenticado
     * @return Estadísticas del titular (cantidad de condominios, plan, etc.)
     */
    @GetMapping("/estadisticas-titular")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(
        summary = "Obtener estadísticas del titular",
        description = "Retorna estadísticas personales del titular autenticado: " +
                     "cantidad de condominios, razón social, RUC, plan actual y estado del plan. " +
                     "Solo accesible para usuarios con rol CLIENTE."
    )
    public ResponseEntity<TitularStatsDTO> obtenerEstadisticasTitular(Authentication authentication) {
        // Obtener el email del usuario desde el token JWT (authentication.getName() retorna el email)
        String email = authentication.getName();
        Long usuarioId = usuarioSaasRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + email))
                .getId();
        
        TitularStatsDTO stats = reportService.obtenerEstadisticasTitular(usuarioId);
        return ResponseEntity.ok(stats);
    }

    /**
     * Obtiene estadísticas de clientes.
     * Solo accesible para usuarios con rol ADMIN.
     *
     * @return Estadísticas detalladas de clientes
     */
    @GetMapping("/estadisticas-clientes")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Obtener estadísticas de clientes",
        description = "Retorna estadísticas detalladas de clientes (CLIENTE role): " +
                     "total de clientes, clientes activos, pendientes de plan, suspendidos, " +
                     "y total de condominios registrados. " +
                     "Solo accesible para usuarios con rol ADMIN."
    )
    public ResponseEntity<AdminClientesStatsDTO> obtenerEstadisticasClientes() {
        AdminClientesStatsDTO stats = reportService.obtenerEstadisticasClientes();
        return ResponseEntity.ok(stats);
    }

    /**
     * Obtiene el top de planes más adquiridos.
     * Solo accesible para usuarios con rol ADMIN o superior.
     *
     * @return Estadísticas de los top 3 planes
     */
    @GetMapping("/top-planes")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Obtener TOP de planes más adquiridos",
        description = "Retorna un ranking de los 3 planes más adquiridos con información detallada: " +
                     "nombre, descripción, cantidad de adquisiciones, precio, límite de condominios. " +
                     "Solo accesible para usuarios con rol ADMIN."
    )
    public ResponseEntity<TopPlanesStatsDTO> obtenerTopPlanes() {
        TopPlanesStatsDTO stats = reportService.obtenerTopPlanes(3);
        return ResponseEntity.ok(stats);
    }

    /**
     * Obtiene un reporte consolidado y detallado con todas las estadísticas del sistema.
     * Solo accesible para usuarios con rol SUPERADMIN.
     *
     * @return Reporte detallado completo
     */
    @GetMapping("/reporte-detallado")
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(
        summary = "Obtener reporte detallado consolidado",
        description = "Retorna un reporte completo con: " +
                     "- Estadísticas globales (usuarios por rol), " +
                     "- Estadísticas de clientes (activos, pendientes, suspendidos, condominios), " +
                     "- TOP 3 de planes más adquiridos. " +
                     "Solo accesible para usuarios con rol SUPERADMIN."
    )
    public ResponseEntity<ReporteDetalladoDTO> obtenerReporteDetallado() {
        ReporteDetalladoDTO reporte = reportService.obtenerReporteDetallado();
        return ResponseEntity.ok(reporte);
    }
}