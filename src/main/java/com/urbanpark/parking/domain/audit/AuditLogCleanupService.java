package com.urbanpark.parking.domain.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogCleanupService {

    private final AuditLogRepository repository;

    /**
     * Ejecuta mensualmente (día 1 a las 2 AM)
     * Elimina logs mayores a 6 meses
     * 
     * Cron: "0 0 2 1 * ?"
     * - Segundo 0
     * - Minuto 0
     * - Hora 2 (2:00 AM)
     * - Día 1 del mes
     * - Todos los meses
     * - Sin especificar día de semana
     */
    @Scheduled(cron = "0 0 2 1 * ?")
    @Transactional
    public void limpiarLogsAntiguos() {
        LocalDateTime fechaCorte = LocalDateTime.now().minusMonths(6);
        
        log.info(" Iniciando limpieza de logs de auditoría anteriores a: {}", fechaCorte);
        
        try {
            int cantidadEliminada = repository.deleteByFechaHoraBefore(fechaCorte);
            
            log.info("✅ Limpieza completada. Registros eliminados: {}", cantidadEliminada);
        } catch (Exception e) {
            log.error("❌ Error durante la limpieza de logs: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Método manual para pruebas o ejecución on-demand
     * @param mesesRetencion Número de meses a retener
     * @return Cantidad de registros eliminados
     */
    @Transactional
    public int limpiarLogsManual(int mesesRetencion) {
        LocalDateTime fechaCorte = LocalDateTime.now().minusMonths(mesesRetencion);
        
        log.info("🧹 Limpieza manual iniciada. Reteniendo logs de {} meses. Fecha de corte: {}", 
                mesesRetencion, fechaCorte);
        
        int cantidadEliminada = repository.deleteByFechaHoraBefore(fechaCorte);
        
        log.info("Limpieza manual completada. Registros eliminados: {}", cantidadEliminada);
        
        return cantidadEliminada;
    }
}