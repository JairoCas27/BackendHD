package com.urbanpark.parking.domain.reports;

import com.urbanpark.parking.domain.parking_management.EspacioParkingRepository;
import com.urbanpark.parking.domain.reports.dto.*;
import com.urbanpark.parking.domain.tenant.TenantContext;
import com.urbanpark.parking.domain.users.vehiculo.VehiculoRepository;
import com.urbanpark.parking.shared.enums.EstadoEspacio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final EspacioParkingRepository espacioRepository;
    private final VehiculoRepository vehiculoRepository;

    public ReporteAccesosDTO reporteAccesos(LocalDateTime inicio, LocalDateTime fin) {
        UUID tenantId = TenantContext.getTenantId();

        long entradas   = reportRepository.countEntradas(tenantId, inicio, fin);
        long denegados  = reportRepository.countDenegados(tenantId, inicio, fin);
        long activos    = reportRepository.countVehiculosActivos(tenantId);
        Double promedio = reportRepository.avgDuracion(tenantId, inicio, fin);

        return ReporteAccesosDTO.builder()
                .totalEntradas(entradas)
                .totalSalidas(entradas - activos)
                .totalDenegados(denegados)
                .vehiculosActivos(activos)
                .duracionPromedioMinutos(promedio != null ? promedio : 0)
                .desde(inicio)
                .hasta(fin)
                .build();
    }

    public ReporteOcupacionDTO reporteOcupacion() {
        UUID tenantId = TenantContext.getTenantId();

        List<Object[]> conteos = espacioRepository.countByEstado(tenantId);

        int total = 0, ocupados = 0, libres = 0, reservados = 0, fueraServicio = 0;

        for (Object[] row : conteos) {
            EstadoEspacio estado = (EstadoEspacio) row[0];
            int cantidad = ((Long) row[1]).intValue();
            total += cantidad;

            switch (estado) {
                case OCUPADO        -> ocupados       = cantidad;
                case LIBRE          -> libres         = cantidad;
                case RESERVADO      -> reservados     = cantidad;
                case FUERA_SERVICIO -> fueraServicio  = cantidad;
            }
        }

        // Zona con más ocupación
        String zonaTop = espacioRepository
                .findAllByTenantIdAndEstado(tenantId, EstadoEspacio.OCUPADO)
                .stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        e -> e.getZona(),
                        java.util.stream.Collectors.counting()
                ))
                .entrySet().stream()
                .max(java.util.Map.Entry.comparingByValue())
                .map(java.util.Map.Entry::getKey)
                .orElse("N/A");

        return ReporteOcupacionDTO.builder()
                .totalEspacios(total)
                .ocupados(ocupados)
                .libres(libres)
                .reservados(reservados)
                .fueraServicio(fueraServicio)
                .porcentajeOcupacion(total > 0 ? (double) ocupados / total * 100 : 0)
                .zonaConMasOcupacion(zonaTop)
                .build();
    }

    public ReporteVehiculoDTO reporteVehiculo(UUID vehiculoId) {
        UUID tenantId = TenantContext.getTenantId();

        long accesos   = reportRepository.countAccesosPorVehiculo(tenantId, vehiculoId);
        long denegados = reportRepository.countDenegadosPorVehiculo(tenantId, vehiculoId);
        Double promedio = reportRepository.avgDuracionPorVehiculo(tenantId, vehiculoId);
        LocalDateTime ultimo = reportRepository.ultimoAcceso(tenantId, vehiculoId);

        String placa = vehiculoRepository.findById(vehiculoId)
                .map(v -> v.getPlaca())
                .orElse("DESCONOCIDA");

        return ReporteVehiculoDTO.builder()
                .vehiculoId(vehiculoId)
                .placa(placa)
                .totalAccesos(accesos)
                .totalDenegados(denegados)
                .duracionPromedioMinutos(promedio != null ? promedio : 0)
                .ultimoAcceso(ultimo)
                .build();
    }

    public List<ReporteAccesosPorDiaDTO> tendenciaDiaria(
            LocalDateTime inicio, LocalDateTime fin) {
        return reportRepository.accesosPorDia(TenantContext.getTenantId(), inicio, fin);
    }

    public List<TopPlacaDTO> topPlacasDenegadas(LocalDateTime inicio, LocalDateTime fin) {
        List<Object[]> raw = reportRepository.topPlacasDenegadas(
                TenantContext.getTenantId(), inicio, fin);

        List<TopPlacaDTO> resultado = new ArrayList<>();
        for (Object[] row : raw) {
            resultado.add(new TopPlacaDTO((String) row[0], (Long) row[1]));
        }

        return resultado;
    }
}