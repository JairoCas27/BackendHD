package com.urbanpark.parking.domain.users.visitante;

import com.urbanpark.parking.domain.tenant.TenantContext;
import com.urbanpark.parking.domain.users.visitante.dto.VisitanteRequest;
import com.urbanpark.parking.domain.users.visitante.dto.VisitanteResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VisitanteService {

    private final VisitanteRepository visitanteRepository;

    public VisitanteResponse crear(VisitanteRequest request) {
        Visitante visitante = Visitante.builder()
                .tenantId(TenantContext.getTenantId())
                .propietarioId(request.getPropietarioId())
                .nombre(request.getNombre())
                .placaVehiculo(request.getPlacaVehiculo() != null
                        ? request.getPlacaVehiculo().toUpperCase()
                        : null)
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .activo(true)
                .build();

        return toResponse(visitanteRepository.save(visitante));
    }

    public List<VisitanteResponse> listarTodos() {
        return visitanteRepository.findAllByTenantId(TenantContext.getTenantId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<VisitanteResponse> listarPorPropietario(UUID propietarioId) {
        return visitanteRepository
                .findAllByPropietarioIdAndTenantId(propietarioId, TenantContext.getTenantId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void revocar(UUID id) {
        Visitante visitante = findById(id);
        visitante.setActivo(false);
        visitanteRepository.save(visitante);
    }

    private Visitante findById(UUID id) {
        Visitante visitante = visitanteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Visitante no encontrado"));

        if (!visitante.getTenantId().equals(TenantContext.getTenantId())) {
            throw new EntityNotFoundException("Visitante no encontrado");
        }

        return visitante;
    }

    private VisitanteResponse toResponse(Visitante v) {
        return VisitanteResponse.builder()
                .id(v.getId())
                .propietarioId(v.getPropietarioId())
                .nombre(v.getNombre())
                .placaVehiculo(v.getPlacaVehiculo())
                .fechaInicio(v.getFechaInicio())
                .fechaFin(v.getFechaFin())
                .activo(v.isActivo())
                .build();
    }
}