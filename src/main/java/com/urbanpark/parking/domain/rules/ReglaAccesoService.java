package com.urbanpark.parking.domain.rules;

import com.urbanpark.parking.domain.audit.AuditService;
import com.urbanpark.parking.domain.rules.dto.ReglaRequest;
import com.urbanpark.parking.domain.rules.dto.ReglaResponse;
import com.urbanpark.parking.domain.tenant.TenantContext;
import com.urbanpark.parking.shared.enums.TipoAccionAudit;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReglaAccesoService {

    private final ReglaAccesoRepository reglaRepository;
    private final AuditService auditService;

    public ReglaResponse crear(ReglaRequest request) {
        ReglaAcceso regla = reglaRepository.save(ReglaAcceso.builder()
                .tenantId(TenantContext.getTenantId())
                .nombre(request.getNombre())
                .tipo(request.getTipo())
                .configuracion(request.getConfiguracion())
                .activo(true)
                .build());

        auditService.registrar(
                TenantContext.getTenantId(),
                TenantContext.getUsuarioId(),
                TipoAccionAudit.REGLA_CREADA,
                "ReglaAcceso", regla.getId().toString(),
                Map.of(
                        "nombre", regla.getNombre(),
                        "tipo", regla.getTipo().name()
                )
        );

        return toResponse(regla);
    }

    public List<ReglaResponse> listarTodas() {
        return reglaRepository.findAllByTenantId(TenantContext.getTenantId())
                .stream().map(this::toResponse).toList();
    }

    public List<ReglaResponse> listarActivas() {
        return reglaRepository.findAllByTenantIdAndActivoTrue(TenantContext.getTenantId())
                .stream().map(this::toResponse).toList();
    }

    public ReglaResponse buscarPorId(UUID id) {
        return toResponse(findById(id));
    }

    public ReglaResponse actualizar(UUID id, ReglaRequest request) {
        ReglaAcceso regla = findById(id);
        regla.setNombre(request.getNombre());
        regla.setTipo(request.getTipo());
        regla.setConfiguracion(request.getConfiguracion());
        ReglaAcceso actualizada = reglaRepository.save(regla);

        auditService.registrar(
                TenantContext.getTenantId(),
                TenantContext.getUsuarioId(),
                TipoAccionAudit.REGLA_ACTUALIZADA,
                "ReglaAcceso", id.toString(),
                Map.of(
                        "nombre", actualizada.getNombre(),
                        "tipo", actualizada.getTipo().name()
                )
        );

        return toResponse(actualizada);
    }

    public void activar(UUID id) {
        ReglaAcceso regla = findById(id);
        regla.setActivo(true);
        reglaRepository.save(regla);

        auditService.registrar(
                TenantContext.getTenantId(),
                TenantContext.getUsuarioId(),
                TipoAccionAudit.REGLA_ACTIVADA,
                "ReglaAcceso", id.toString(),
                Map.of("nombre", regla.getNombre())
        );
    }

    public void desactivar(UUID id) {
        ReglaAcceso regla = findById(id);
        regla.setActivo(false);
        reglaRepository.save(regla);

        auditService.registrar(
                TenantContext.getTenantId(),
                TenantContext.getUsuarioId(),
                TipoAccionAudit.REGLA_DESACTIVADA,
                "ReglaAcceso", id.toString(),
                Map.of("nombre", regla.getNombre())
        );
    }

    private ReglaAcceso findById(UUID id) {
        ReglaAcceso regla = reglaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Regla no encontrada"));
        if (!regla.getTenantId().equals(TenantContext.getTenantId())) {
            throw new EntityNotFoundException("Regla no encontrada");
        }
        return regla;
    }

    private ReglaResponse toResponse(ReglaAcceso r) {
        return ReglaResponse.builder()
                .id(r.getId())
                .nombre(r.getNombre())
                .tipo(r.getTipo())
                .configuracion(r.getConfiguracion())
                .activo(r.isActivo())
                .createdAt(r.getCreatedAt())
                .build();
    }

    public void eliminar(UUID id) {
        ReglaAcceso regla = findById(id);
        reglaRepository.delete(regla);

        auditService.registrar(
                TenantContext.getTenantId(),
                TenantContext.getUsuarioId(),
                TipoAccionAudit.REGLA_DESACTIVADA,
                "ReglaAcceso", id.toString(),
                Map.of(
                        "nombre", regla.getNombre(),
                        "tipo", regla.getTipo().name(),
                        "accion", "ELIMINADA"
                )
        );
    }
}