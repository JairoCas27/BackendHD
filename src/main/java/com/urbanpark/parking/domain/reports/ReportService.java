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
import org.springframework.transaction.annotation.Transactional;   // <-- importa esto
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
    @Transactional(readOnly = true)
    public GlobalStatsDTO obtenerEstadisticasGlobales() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        UsuarioStatsDTO usuarioStats = UsuarioStatsDTO.builder()
                .totalSuperAdmins(contarPorRol(RolSaas.SUPERADMIN))
                .totalAdmins(contarPorRol(RolSaas.ADMIN))
                .totalClientes(contarPorRol(RolSaas.CLIENTE))
                .totalUsuarios(usuarioSaasRepository.count())
                .timestamp(timestamp)
                .build();

        return GlobalStatsDTO.builder()
                .usuarioStats(usuarioStats)
                .timestamp(timestamp)
                .periodo("Actualizado al día de hoy")
                .build();
    }

    /**
     * Obtiene estadísticas personales del titular (cliente).
     */
    @Transactional(readOnly = true)
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

        // Si hay titular, retornar sus estadísticas (aquí accedes a LAZY condominios)
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
     */
    @Transactional(readOnly = true)
    public AdminClientesStatsDTO obtenerEstadisticasClientes() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        var todosLosClientes = usuarioSaasRepository.findAllByRol(RolSaas.CLIENTE);
        Long totalClientes = (long) todosLosClientes.size();

        Long clientesActivos = todosLosClientes.stream()
                .filter(u -> u.getEstado() == EstadoUsuarioSaas.ACTIVO)
                .count();

        Long clientesPendientes = todosLosClientes.stream()
                .filter(u -> u.getEstado() == EstadoUsuarioSaas.PENDIENTE_PLAN)
                .count();

        Long clientesSuspendidos = todosLosClientes.stream()
                .filter(u -> u.getEstado() == EstadoUsuarioSaas.SUSPENDIDO)
                .count();

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

    @Transactional(readOnly = true)
    protected Long contarPorRol(RolSaas rol) {
        return (long) usuarioSaasRepository.findAllByRol(rol).size();
    }

    @Transactional(readOnly = true)
    public TopPlanesStatsDTO obtenerTopPlanes(int limit) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        List<Plan> planesActivos = planRepository.findAllByEstado(EstadoPlan.ACTIVO);

        Map<Plan, Long> planCount = new HashMap<>();
        for (Plan plan : planesActivos) {
            Long count = titularRepository.findAll().stream()
                    .filter(t -> t.getPlan() != null && t.getPlan().getId().equals(plan.getId()))
                    .count();
            planCount.put(plan, count);
        }

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

    @Transactional(readOnly = true)
    public ReporteDetalladoDTO obtenerReporteDetallado() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

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