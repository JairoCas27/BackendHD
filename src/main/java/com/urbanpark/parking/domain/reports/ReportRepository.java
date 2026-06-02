package com.urbanpark.parking.domain.reports;

import com.urbanpark.parking.domain.reports.dto.ReporteAccesosPorDiaDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.urbanpark.parking.domain.parking.AccesoVehicular;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ReportRepository extends Repository<AccesoVehicular, UUID> {

    // Total entradas en rango
    @Query("""
        SELECT COUNT(a) FROM AccesoVehicular a
        WHERE a.tenantId = :tenantId
        AND a.autorizado = true
        AND a.timestampEntrada BETWEEN :inicio AND :fin
    """)
    long countEntradas(UUID tenantId, LocalDateTime inicio, LocalDateTime fin);

    // Total denegados en rango
    @Query("""
        SELECT COUNT(a) FROM AccesoVehicular a
        WHERE a.tenantId = :tenantId
        AND a.autorizado = false
        AND a.timestampEntrada BETWEEN :inicio AND :fin
    """)
    long countDenegados(UUID tenantId, LocalDateTime inicio, LocalDateTime fin);

    // Vehículos actualmente dentro del parking
    @Query("""
        SELECT COUNT(a) FROM AccesoVehicular a
        WHERE a.tenantId = :tenantId
        AND a.autorizado = true
        AND a.timestampSalida IS NULL
    """)
    long countVehiculosActivos(UUID tenantId);

    // Duración promedio de permanencia
    @Query("""
        SELECT AVG(a.duracionMinutos) FROM AccesoVehicular a
        WHERE a.tenantId = :tenantId
        AND a.duracionMinutos IS NOT NULL
        AND a.timestampEntrada BETWEEN :inicio AND :fin
    """)
    Double avgDuracion(UUID tenantId, LocalDateTime inicio, LocalDateTime fin);

    // Accesos por día (para gráfica de tendencia)
    @Query("""
        SELECT new com.urbanpark.parking.domain.reports.dto.ReporteAccesosPorDiaDTO(
            CAST(a.timestampEntrada AS string),
            SUM(CASE WHEN a.autorizado = true THEN 1 ELSE 0 END),
            SUM(CASE WHEN a.autorizado = false THEN 1 ELSE 0 END)
        )
        FROM AccesoVehicular a
        WHERE a.tenantId = :tenantId
        AND a.timestampEntrada BETWEEN :inicio AND :fin
        GROUP BY CAST(a.timestampEntrada AS string)
        ORDER BY CAST(a.timestampEntrada AS string)
    """)
    List<ReporteAccesosPorDiaDTO> accesosPorDia(
            UUID tenantId, LocalDateTime inicio, LocalDateTime fin);

    // Historial de accesos de un vehículo específico
    @Query("""
        SELECT COUNT(a) FROM AccesoVehicular a
        WHERE a.tenantId = :tenantId
        AND a.vehiculoId = :vehiculoId
        AND a.autorizado = true
    """)
    long countAccesosPorVehiculo(UUID tenantId, UUID vehiculoId);

    // Total denegados por vehículo
    @Query("""
        SELECT COUNT(a) FROM AccesoVehicular a
        WHERE a.tenantId = :tenantId
        AND a.vehiculoId = :vehiculoId
        AND a.autorizado = false
    """)
    long countDenegadosPorVehiculo(UUID tenantId, UUID vehiculoId);

    // Último acceso de un vehículo
    @Query("""
        SELECT MAX(a.timestampEntrada) FROM AccesoVehicular a
        WHERE a.tenantId = :tenantId
        AND a.vehiculoId = :vehiculoId
    """)
    LocalDateTime ultimoAcceso(UUID tenantId, UUID vehiculoId);

    // Duración promedio por vehículo
    @Query("""
        SELECT AVG(a.duracionMinutos) FROM AccesoVehicular a
        WHERE a.tenantId = :tenantId
        AND a.vehiculoId = :vehiculoId
        AND a.duracionMinutos IS NOT NULL
    """)
    Double avgDuracionPorVehiculo(UUID tenantId, UUID vehiculoId);

    // Top placas con más accesos denegados
    @Query("""
        SELECT a.placa, COUNT(a) as total
        FROM AccesoVehicular a
        WHERE a.tenantId = :tenantId
        AND a.autorizado = false
        AND a.timestampEntrada BETWEEN :inicio AND :fin
        GROUP BY a.placa
        ORDER BY total DESC
    """)
    List<Object[]> topPlacasDenegadas(
            UUID tenantId, LocalDateTime inicio, LocalDateTime fin);
}