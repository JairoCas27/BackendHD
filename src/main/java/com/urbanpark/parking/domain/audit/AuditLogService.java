// domain/audit/AuditLogService.java
package com.urbanpark.parking.domain.audit;

import com.urbanpark.parking.domain.audit.dto.AuditLogFiltroRequest;
import com.urbanpark.parking.domain.audit.dto.AuditLogResponse;
import com.urbanpark.parking.shared.enums.TipoAccionAudit;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository repository;

    
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(
            Long usuarioSaasId,
            String usuarioEmail,
            String rolUsuario,
            TipoAccionAudit accion,
            String descripcion,
            String entidadAfectada,
            String endpoint,
            String metodoHttp,
            String ipOrigen,
            boolean exitoso,
            String detalleError
    ) {
        AuditLog log = AuditLog.builder()
                .usuarioSaasId(usuarioSaasId)
                .usuarioEmail(usuarioEmail)
                .rolUsuario(rolUsuario)
                .accion(accion)
                .descripcion(descripcion)
                .entidadAfectada(entidadAfectada)
                .endpoint(endpoint)
                .metodoHttp(metodoHttp)
                .ipOrigen(ipOrigen)
                .exitoso(exitoso)
                .detalleError(detalleError)
                .build();
        repository.save(log);
    }

    // ── Consultas (solo SUPERADMIN) ──────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<AuditLogResponse> listarTodos(Pageable pageable) {
        return repository.findAllByOrderByFechaHoraDesc(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<AuditLogResponse> filtrar(AuditLogFiltroRequest filtro, Pageable pageable) {
        Specification<AuditLog> spec = Specification
            .where(AuditLogSpecifications.conUsuarioId(filtro.usuarioId()))
            .and(AuditLogSpecifications.conAccion(filtro.accion()))
            .and(AuditLogSpecifications.conExitoso(filtro.exitoso()))
            .and(AuditLogSpecifications.desdeFecha(filtro.desde()))
            .and(AuditLogSpecifications.hastaFecha(filtro.hasta()));

        return repository.findAll(spec, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public AuditLogResponse buscarPorId(Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Log no encontrado: " + id));
    }

    private AuditLogResponse toResponse(AuditLog a) {
        return new AuditLogResponse(
                a.getId(), a.getUsuarioSaasId(), a.getUsuarioEmail(),
                a.getRolUsuario(), a.getAccion(), a.getDescripcion(),
                a.getEntidadAfectada(), a.getEndpoint(), a.getMetodoHttp(),
                a.getIpOrigen(), a.isExitoso(), a.getDetalleError(), a.getFechaHora()
        );
    }
}