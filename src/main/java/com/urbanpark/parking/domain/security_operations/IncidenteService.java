package com.urbanpark.parking.domain.security_operations;

import com.urbanpark.parking.domain.audit.AuditService;
import com.urbanpark.parking.domain.integration.UsuarioSesion;
import com.urbanpark.parking.domain.integration.UsuarioSesionRepository;
import com.urbanpark.parking.domain.security_operations.dto.IncidenteRequest;
import com.urbanpark.parking.domain.security_operations.dto.IncidenteResponse;
import com.urbanpark.parking.domain.security_operations.dto.ResolucionRequest;
import com.urbanpark.parking.domain.tenant.TenantContext;
import com.urbanpark.parking.shared.enums.EstadoIncidente;
import com.urbanpark.parking.shared.enums.NivelIncidente;
import com.urbanpark.parking.shared.enums.TipoAccionAudit;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IncidenteService {

    private final IncidenteRepository incidenteRepository;
    private final UsuarioSesionRepository usuarioSesionRepository;
    private final AuditService auditService;

    public IncidenteResponse reportar(IncidenteRequest request) {
        UsuarioSesion sesion = usuarioSesionRepository.findById(request.getSesionId())
                .orElseThrow(() -> new EntityNotFoundException("Sesion no encontrada"));

        if (!sesion.getCondominioId().equals(TenantContext.getTenantId())) {
            throw new IllegalArgumentException("La sesion no pertenece a este condominio");
        }

        Incidente incidente = incidenteRepository.save(Incidente.builder()
                .tenantId(TenantContext.getTenantId())
                .sesionId(sesion.getId())
                .accesoId(request.getAccesoId())
                .descripcion(request.getDescripcion())
                .nivel(request.getNivel())
                .estado(EstadoIncidente.ABIERTO)
                .placaInvolucrada(request.getPlacaInvolucrada() != null
                        ? request.getPlacaInvolucrada().toUpperCase() : null)
                .build());

        auditService.registrar(
                TenantContext.getTenantId(),
                sesion.getId(),
                TipoAccionAudit.INCIDENTE_REPORTADO,
                "Incidente", incidente.getId().toString(),
                Map.of(
                        "nivel", incidente.getNivel().name(),
                        "descripcion", incidente.getDescripcion(),
                        "reportadoPor", sesion.getNombre(),
                        "rol", sesion.getRol(),
                        "placa", incidente.getPlacaInvolucrada() != null
                                ? incidente.getPlacaInvolucrada() : "N/A"
                )
        );

        return toResponse(incidente, sesion);
    }

    public List<IncidenteResponse> listarTodos() {
        return incidenteRepository.findAllByTenantId(TenantContext.getTenantId())
                .stream().map(i -> toResponse(i, getSesion(i.getSesionId()))).toList();
    }

    public List<IncidenteResponse> listarMios(UUID sesionId) {
        validarSesionDelTenant(sesionId);
        return incidenteRepository
                .findAllBySesionIdAndTenantId(sesionId, TenantContext.getTenantId())
                .stream().map(i -> toResponse(i, getSesion(i.getSesionId()))).toList();
    }

    public List<IncidenteResponse> listarPorEstado(EstadoIncidente estado) {
        return incidenteRepository
                .findAllByTenantIdAndEstado(TenantContext.getTenantId(), estado)
                .stream().map(i -> toResponse(i, getSesion(i.getSesionId()))).toList();
    }

    public List<IncidenteResponse> listarPorNivel(NivelIncidente nivel) {
        return incidenteRepository
                .findAllByTenantIdAndNivel(TenantContext.getTenantId(), nivel)
                .stream().map(i -> toResponse(i, getSesion(i.getSesionId()))).toList();
    }

    public IncidenteResponse buscarPorId(UUID id) {
        Incidente incidente = findById(id);
        return toResponse(incidente, getSesion(incidente.getSesionId()));
    }

    public IncidenteResponse cambiarEstado(UUID id, EstadoIncidente nuevoEstado) {
        Incidente incidente = findById(id);
        EstadoIncidente estadoAnterior = incidente.getEstado();
        incidente.setEstado(nuevoEstado);
        incidenteRepository.save(incidente);

        auditService.registrar(
                TenantContext.getTenantId(),
                incidente.getSesionId(),
                TipoAccionAudit.ESPACIO_ESTADO_CAMBIADO,
                "Incidente", id.toString(),
                Map.of(
                        "estadoAnterior", estadoAnterior.name(),
                        "estadoNuevo", nuevoEstado.name()
                )
        );

        return toResponse(incidente, getSesion(incidente.getSesionId()));
    }

    public IncidenteResponse resolver(UUID id, ResolucionRequest request) {
        Incidente incidente = findById(id);

        if (incidente.getEstado() == EstadoIncidente.RESUELTO) {
            throw new IllegalArgumentException("El incidente ya fue resuelto");
        }

        incidente.setResolucion(request.getResolucion());
        incidente.setEstado(EstadoIncidente.RESUELTO);
        incidente.setResueltoAt(LocalDateTime.now());
        incidenteRepository.save(incidente);

        auditService.registrar(
                TenantContext.getTenantId(),
                incidente.getSesionId(),
                TipoAccionAudit.INCIDENTE_RESUELTO,
                "Incidente", id.toString(),
                Map.of("resolucion", request.getResolucion())
        );

        return toResponse(incidente, getSesion(incidente.getSesionId()));
    }

    public void eliminar(UUID id) {
        Incidente incidente = findById(id);
        incidenteRepository.delete(incidente);

        auditService.registrar(
                TenantContext.getTenantId(),
                incidente.getSesionId(),
                TipoAccionAudit.INCIDENTE_REPORTADO,
                "Incidente", id.toString(),
                Map.of(
                        "accion", "ELIMINADO",
                        "descripcion", incidente.getDescripcion()
                )
        );
    }

    // ─── Helpers ─────────────────────────────────────────────────────

    private Incidente findById(UUID id) {
        Incidente incidente = incidenteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Incidente no encontrado"));
        if (!incidente.getTenantId().equals(TenantContext.getTenantId())) {
            throw new EntityNotFoundException("Incidente no encontrado");
        }
        return incidente;
    }

    private UsuarioSesion getSesion(UUID sesionId) {
        return usuarioSesionRepository.findById(sesionId).orElse(null);
    }

    private void validarSesionDelTenant(UUID sesionId) {
        UsuarioSesion sesion = usuarioSesionRepository.findById(sesionId)
                .orElseThrow(() -> new EntityNotFoundException("Sesion no encontrada"));
        if (!sesion.getCondominioId().equals(TenantContext.getTenantId())) {
            throw new IllegalArgumentException("La sesion no pertenece a este condominio");
        }
    }

    private IncidenteResponse toResponse(Incidente i, UsuarioSesion sesion) {
        return IncidenteResponse.builder()
                .id(i.getId())
                .tenantId(i.getTenantId())
                .sesionId(i.getSesionId())
                .reportadoPor(sesion != null ? sesion.getNombre() : "Desconocido")
                .rolReportador(sesion != null ? sesion.getRol() : "-")
                .accesoId(i.getAccesoId())
                .descripcion(i.getDescripcion())
                .nivel(i.getNivel())
                .estado(i.getEstado())
                .placaInvolucrada(i.getPlacaInvolucrada())
                .resolucion(i.getResolucion())
                .resueltoAt(i.getResueltoAt())
                .createdAt(i.getCreatedAt())
                .build();
    }
}