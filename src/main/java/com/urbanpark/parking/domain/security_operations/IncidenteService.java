package com.urbanpark.parking.domain.security_operations;

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

    public IncidenteResponse reportar(IncidenteRequest request) {
        Incidente incidente = Incidente.builder()
                .tenantId(TenantContext.getTenantId())
                .agenteId(request.getAgenteId())
                .accesoId(request.getAccesoId())
                .descripcion(request.getDescripcion())
                .nivel(request.getNivel())
                .estado(EstadoIncidente.ABIERTO)
                .placaInvolucrada(request.getPlacaInvolucrada() != null
                        ? request.getPlacaInvolucrada().toUpperCase()
                        : null)
                .build();

        return toResponse(incidenteRepository.save(incidente));
    }

    public List<IncidenteResponse> listarTodos() {
        return incidenteRepository.findAllByTenantId(TenantContext.getTenantId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<IncidenteResponse> listarPorEstado(EstadoIncidente estado) {
        return incidenteRepository
                .findAllByTenantIdAndEstado(TenantContext.getTenantId(), estado)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<IncidenteResponse> listarPorNivel(NivelIncidente nivel) {
        return incidenteRepository
                .findAllByTenantIdAndNivel(TenantContext.getTenantId(), nivel)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<IncidenteResponse> listarPorAgente(UUID agenteId) {
        return incidenteRepository
                .findAllByAgenteIdAndTenantId(agenteId, TenantContext.getTenantId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public IncidenteResponse buscarPorId(UUID id) {
        return toResponse(findById(id));
    }

    public IncidenteResponse cambiarEstado(UUID id, EstadoIncidente estado) {
        Incidente incidente = findById(id);
        incidente.setEstado(estado);
        return toResponse(incidenteRepository.save(incidente));
    }

    public IncidenteResponse resolver(UUID id, ResolucionRequest request) {
        Incidente incidente = findById(id);

        if (incidente.getEstado() == EstadoIncidente.RESUELTO) {
            throw new IllegalArgumentException("El incidente ya fue resuelto");
        }

        incidente.setResolucion(request.getResolucion());
        incidente.setEstado(EstadoIncidente.RESUELTO);
        incidente.setResueltoAt(LocalDateTime.now());

        return toResponse(incidenteRepository.save(incidente));
    }

    private Incidente findById(UUID id) {
        Incidente incidente = incidenteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Incidente no encontrado"));

        if (!incidente.getTenantId().equals(TenantContext.getTenantId())) {
            throw new EntityNotFoundException("Incidente no encontrado");
        }

        return incidente;
    }

    private IncidenteResponse toResponse(Incidente i) {
        return IncidenteResponse.builder()
                .id(i.getId())
                .agenteId(i.getAgenteId())
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