package com.urbanpark.parking.domain.audit;

import com.urbanpark.parking.shared.enums.TipoAccionAudit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    // Registro asíncrono — no bloquea la operación principal
    @Async
    public void registrar(
            UUID tenantId,
            UUID usuarioId,
            TipoAccionAudit accion,
            String entidad,
            String entidadId,
            Map<String, Object> detalle
    ) {
        try {
            AuditLog log = AuditLog.builder()
                    .tenantId(tenantId)
                    .usuarioId(usuarioId)
                    .accion(accion)
                    .entidad(entidad)
                    .entidadId(entidadId)
                    .detalle(detalle)
                    .build();

            auditLogRepository.save(log);
        } catch (Exception e) {
            // Nunca fallar la operación principal por un error de auditoría
            log.error("Error registrando auditoría [{} - {}]: {}",
                    accion, entidad, e.getMessage());
        }
    }

    // Overload simplificado sin detalle
    @Async
    public void registrar(
            UUID tenantId,
            UUID usuarioId,
            TipoAccionAudit accion,
            String entidad,
            String entidadId
    ) {
        registrar(tenantId, usuarioId, accion, entidad, entidadId, null);
    }
}