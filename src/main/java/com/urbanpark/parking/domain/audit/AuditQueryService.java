package com.urbanpark.parking.domain.audit;

import com.urbanpark.parking.domain.audit.dto.AuditLogResponse;
import com.urbanpark.parking.domain.tenant.TenantContext;
import com.urbanpark.parking.shared.enums.TipoAccionAudit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditQueryService {

    private final AuditLogRepository auditLogRepository;

    public Page<AuditLogResponse> listarPorTenant(Pageable pageable) {
        return auditLogRepository
                .findAllByTenantId(TenantContext.getTenantId(), pageable)
                .map(this::toResponse);
    }

    public Page<AuditLogResponse> listarPorAccion(
            TipoAccionAudit accion, Pageable pageable) {
        return auditLogRepository
                .findAllByTenantIdAndAccion(TenantContext.getTenantId(), accion, pageable)
                .map(this::toResponse);
    }

    public Page<AuditLogResponse> listarPorUsuario(
            UUID usuarioId, Pageable pageable) {
        return auditLogRepository
                .findAllByTenantIdAndUsuarioId(TenantContext.getTenantId(), usuarioId, pageable)
                .map(this::toResponse);
    }

    public Page<AuditLogResponse> listarPorRango(
            LocalDateTime inicio, LocalDateTime fin, Pageable pageable) {
        return auditLogRepository
                .findAllByTenantIdAndCreatedAtBetween(
                        TenantContext.getTenantId(), inicio, fin, pageable)
                .map(this::toResponse);
    }

    public List<AuditLogResponse> historialEntidad(String entidad, String entidadId) {
        return auditLogRepository
                .findHistorialEntidad(TenantContext.getTenantId(), entidad, entidadId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Superadmin — todos los tenants
    public Page<AuditLogResponse> listarGlobal(Pageable pageable) {
        return auditLogRepository.findAll(pageable).map(this::toResponse);
    }

    private AuditLogResponse toResponse(AuditLog a) {
        return AuditLogResponse.builder()
                .id(a.getId())
                .tenantId(a.getTenantId())
                .usuarioId(a.getUsuarioId())
                .accion(a.getAccion())
                .entidad(a.getEntidad())
                .entidadId(a.getEntidadId())
                .detalle(a.getDetalle())
                .ipOrigen(a.getIpOrigen())
                .createdAt(a.getCreatedAt())
                .build();
    }
}