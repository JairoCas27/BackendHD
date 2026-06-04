package com.urbanpark.parking.domain.reports;

public class ReportService {
    
}
package com.urbanpark.parking.domain.reports;

import com.urbanpark.parking.domain.planes.Plan;
import com.urbanpark.parking.domain.planes.PlanRepository;
import com.urbanpark.parking.domain.reports.dto.*;
import com.urbanpark.parking.domain.titulares.Titular;
import com.urbanpark.parking.domain.titulares.TitularRepository;
import com.urbanpark.parking.domain.usuarios.UsuarioSaas;
import com.urbanpark.parking.domain.usuarios.UsuarioSaasRepository;
import com.urbanpark.parking.shared.enums.EstadoPlan;
import com.urbanpark.parking.shared.enums.EstadoUsuarioSaas;
import com.urbanpark.parking.shared.enums.RolSaas;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final UsuarioSaasRepository usuarioSaasRepository;
    private final TitularRepository titularRepository;
    private final PlanRepository planRepository;

    /**
     * Obtiene estadísticas globales de usuarios.
     * Solo SUPERADMIN puede acceder a este método.
     *
     * @return GlobalStatsDTO con conteos de usuarios por rol
     */
    public GlobalStatsDTO obtenerEstadisticasGlobales() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        
        UsuarioStatsDTO usuarioStats = UsuarioStatsDTO.builder()
                .totalSuperAdmins(contarPorRol(RolSaas.SUPERADMIN))
                .totalAdmins(contarPorRol(RolSaas.ADMIN))
                .totalClientes(contarPorRol(RolSaas.CLIENTE))
                .totalUsuarios(usuarioSaasRepository.count())
                .timestamp(timestamp)
                .build();

        GlobalStatsDTO globalStats = GlobalStatsDTO.builder()
                .usuarioStats(usuarioStats)
                .timestamp(timestamp)
                .periodo("Actualizado al día de hoy")
                .build();

        return globalStats;
    }

    /**
     * Obtiene estadísticas personales del titular (cliente).
     * El usuario solo puede ver sus propias estadísticas.
     * Si el usuario no tiene TITULAR, devuelve estadísticas vacías.
     *
     * @param usuarioId ID del usuario autenticado
     * @return TitularStatsDTO con información del titular o datos vacíos
     */
    public TitularStatsDTO obtenerEstadisticasTitular(Long usuarioId) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        
        // Buscar el usuario primero para validar que existe
        UsuarioSaas usuario = usuarioSaasRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));

        // Buscar el titular asociado
        Titular titular = titularRepository.findByUsuarioSaasId(usuarioId).orElse(null);

        // Si no hay titular, retornar estadísticas vacías
        if (titular == null) {
            return TitularStatsDTO.builder()
                    .totalCondominios(0L)
                    .razonSocial("No asignado")
                    .ruc("No asignado")
                    .planActual("Sin plan")
                    .estadoPlan("N/A")
                    .timestamp(timestamp)
                    .build();
        }

        // Si hay titular, retornar sus estadísticas
        return TitularStatsDTO.builder()
                .totalCondominios((long) titular.getCondominios().size())
                .razonSocial(titular.getRazonSocial())
                .ruc(titular.getRuc())
                .planActual(titular.getPlan() != null ? titular.getPlan().getNombre() : "Sin plan")
                .estadoPlan(titular.getEstadoPlan() != null ? titular.getEstadoPlan().toString() : "N/A")
                .timestamp(timestamp)
                .build();
    }

    /**
     * Obtiene estadísticas de clientes (CLIENTE role).
     * Solo ADMIN puede acceder a este método.
     *
     * @return AdminClientesStatsDTO con estadísticas detalladas de clientes
     */
    public AdminClientesStatsDTO obtenerEstadisticasClientes() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        
        // Obtener todos los clientes
        var todosLosClientes = usuarioSaasRepository.findAllByRol(RolSaas.CLIENTE);
        Long totalClientes = (long) todosLosClientes.size();
        
        // Contar por estado
        Long clientesActivos = todosLosClientes.stream()
                .filter(u -> u.getEstado() == EstadoUsuarioSaas.ACTIVO)
                .count();
        
        Long clientesPendientes = todosLosClientes.stream()
                .filter(u -> u.getEstado() == EstadoUsuarioSaas.PENDIENTE_PLAN)
                .count();
        
        Long clientesSuspendidos = todosLosClientes.stream()
                .filter(u -> u.getEstado() == EstadoUsuarioSaas.SUSPENDIDO)
                .count();
        
        // Contar total de condominios de todos los clientes
        Long totalCondominios = titularRepository.findAll().stream()
                .mapToLong(t -> (long) t.getCondominios().size())
                .sum();
        
        return AdminClientesStatsDTO.builder()
                .totalClientes(totalClientes)
                .clientesActivos(clientesActivos)
                .clientesPendientesPlan(clientesPendientes)
                .clientesSuspendidos(clientesSuspendidos)
                .totalCondominiosRegistrados(totalCondominios)
                .timestamp(timestamp)
                .build();
    }

    /**
     * Cuenta el total de usuarios por rol específico.
     *
     * @param rol El rol a contar
     * @return Long con el total de usuarios en ese rol
     */
    private Long contarPorRol(RolSaas rol) {
        return (long) usuarioSaasRepository.findAllByRol(rol).size();
    }

    /**
     * Obtiene el top de planes más adquiridos.
     *
     * @param limit Número de planes a retornar
     * @return TopPlanesStatsDTO con el top de planes
     */
    public TopPlanesStatsDTO obtenerTopPlanes(int limit) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        
        // Obtener todos los planes activos
        List<Plan> planesActivos = planRepository.findAllByEstado(EstadoPlan.ACTIVO);
        
        // Contar cuántos titulares tienen cada plan
        Map<Plan, Long> planCount = new HashMap<>();
        for (Plan plan : planesActivos) {
            Long count = titularRepository.findAll().stream()
                    .filter(t -> t.getPlan() != null && t.getPlan().getId().equals(plan.getId()))
                    .count();
            planCount.put(plan, count);
        }
        
        // Ordenar por cantidad de adquisiciones (descendente) y asignar posiciones
        List<TopPlanDTO> topPlanes = new ArrayList<>();
        int posicion = 1;
        
        for (Map.Entry<Plan, Long> entry : planCount.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(limit)
                .collect(Collectors.toList())) {
            
            TopPlanDTO topPlan = TopPlanDTO.builder()
                    .posicion(posicion)
                    .nombrePlan(entry.getKey().getNombre())
                    .descripcion(entry.getKey().getDescripcion())
                    .totalAdquisiciones(entry.getValue())
                    .precio(entry.getKey().getPrecio())
                    .moneda(entry.getKey().getMoneda())
                    .limiteCondominios(entry.getKey().getLimiteCondominios().toString())
                    .estado(entry.getKey().getEstado().toString())
                    .build();
            
            topPlanes.add(topPlan);
            posicion++;
        }
        
        Long totalPlanesActivos = (long) planesActivos.size();
        
        return TopPlanesStatsDTO.builder()
                .topPlanes(topPlanes)
                .totalPlanesActivos(totalPlanesActivos)
                .timestamp(timestamp)
                .build();
    }

    /**
     * Obtiene un reporte detallado completo con todas las estadísticas.
     * Solo SUPERADMIN puede acceder a este método.
     *
     * @return ReporteDetalladoDTO con todas las estadísticas del sistema
     */
    public ReporteDetalladoDTO obtenerReporteDetallado() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        
        // Obtener todas las estadísticas
        GlobalStatsDTO estadisticasGlobales = obtenerEstadisticasGlobales();
        AdminClientesStatsDTO estadisticasClientes = obtenerEstadisticasClientes();
        TopPlanesStatsDTO topPlanesStats = obtenerTopPlanes(3);
        
        return ReporteDetalladoDTO.builder()
                .estadisticasGlobales(estadisticasGlobales)
                .estadisticasClientes(estadisticasClientes)
                .topPlanesStats(topPlanesStats)
                .timestamp(timestamp)
                .build();
    }
}