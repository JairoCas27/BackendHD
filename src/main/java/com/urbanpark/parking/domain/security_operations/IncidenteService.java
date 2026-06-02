package com.urbanpark.parking.domain.security_operations;

import com.urbanpark.parking.domain.integration.UsuarioSesion;
import com.urbanpark.parking.domain.integration.UsuarioSesionRepository;
import com.urbanpark.parking.domain.security_operations.dto.IncidenteRequest;
import com.urbanpark.parking.domain.security_operations.dto.IncidenteResponse;
import com.urbanpark.parking.domain.security_operations.dto.ResolucionRequest;
import com.urbanpark.parking.domain.tenant.TenantContext;
import com.urbanpark.parking.shared.enums.EstadoIncidente;
import com.urbanpark.parking.shared.enums.NivelIncidente;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IncidenteService {

    private final IncidenteRepository incidenteRepository;
    private final UsuarioSesionRepository usuarioSesionRepository;

    public IncidenteResponse reportar(IncidenteRequest request) {
        // Validar que la sesion existe y pertenece a este tenant
        UsuarioSesion sesion = usuarioSesionRepository.findById(request.getSesionId())
                .orElseThrow(() -> new EntityNotFoundException("Sesion no encontrada"));

        if (!sesion.getCondominioId().equals(TenantContext.getTenantId())) {
            throw new IllegalArgumentException("La sesion no pertenece a este condominio");
        }

        Incidente incidente = Incidente.builder()
                .tenantId(TenantContext.getTenantId())
                .sesionId(sesion.getId())
                .accesoId(request.getAccesoId())
                .descripcion(request.getDescripcion())
                .nivel(request.getNivel())
                .estado(EstadoIncidente.ABIERTO)
                .placaInvolucrada(request.getPlacaInvolucrada() != null
                        ? request.getPlacaInvolucrada().toUpperCase()
                        : null)
                .build();

        return toResponse(incidenteRepository.save(incidente), sesion);
    }

    // ADMIN_CONDOMINIO → lista todos del tenant
    public List<IncidenteResponse> listarTodos() {
        return incidenteRepository.findAllByTenantId(TenantContext.getTenantId())
                .stream()
                .map(i -> toResponse(i, getSesion(i.getSesionId())))
                .toList();
    }

    // AGENTE / PROPIETARIO → solo los suyos por sesionId
    public List<IncidenteResponse> listarMios(UUID sesionId) {
        validarSesionDelTenant(sesionId);
        return incidenteRepository
                .findAllBySesionIdAndTenantId(sesionId, TenantContext.getTenantId())
                .stream()
                .map(i -> toResponse(i, getSesion(i.getSesionId())))
                .toList();
    }

    public List<IncidenteResponse> listarPorEstado(EstadoIncidente estado) {
        return incidenteRepository
                .findAllByTenantIdAndEstado(TenantContext.getTenantId(), estado)
                .stream()
                .map(i -> toResponse(i, getSesion(i.getSesionId())))
                .toList();
    }

    public List<IncidenteResponse> listarPorNivel(NivelIncidente nivel) {
        return incidenteRepository
                .findAllByTenantIdAndNivel(TenantContext.getTenantId(), nivel)
                .stream()
                .map(i -> toResponse(i, getSesion(i.getSesionId())))
                .toList();
    }

    public IncidenteResponse buscarPorId(UUID id) {
        Incidente incidente = findById(id);
        return toResponse(incidente, getSesion(incidente.getSesionId()));
    }

    // Solo ADMIN_CONDOMINIO
    public IncidenteResponse cambiarEstado(UUID id, EstadoIncidente estado) {
        Incidente incidente = findById(id);
        incidente.setEstado(estado);
        return toResponse(incidenteRepository.save(incidente), getSesion(incidente.getSesionId()));
    }

    // Solo ADMIN_CONDOMINIO
    public IncidenteResponse resolver(UUID id, ResolucionRequest request) {
        Incidente incidente = findById(id);

        if (incidente.getEstado() == EstadoIncidente.RESUELTO) {
            throw new IllegalArgumentException("El incidente ya fue resuelto");
        }

        incidente.setResolucion(request.getResolucion());
        incidente.setEstado(EstadoIncidente.RESUELTO);
        incidente.setResueltoAt(LocalDateTime.now());

        return toResponse(incidenteRepository.save(incidente), getSesion(incidente.getSesionId()));
    }

    // Solo ADMIN_CONDOMINIO
    public void eliminar(UUID id) {
        Incidente incidente = findById(id);
        incidenteRepository.delete(incidente);
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
        return usuarioSesionRepository.findById(sesionId)
                .orElse(null);
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