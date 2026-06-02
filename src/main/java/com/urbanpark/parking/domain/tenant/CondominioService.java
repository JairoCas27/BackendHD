package com.urbanpark.parking.domain.tenant;

import com.urbanpark.parking.domain.saas.plan.Plan;
import com.urbanpark.parking.domain.saas.plan.PlanRepository;
import com.urbanpark.parking.domain.tenant.dto.CondominioRequest;
import com.urbanpark.parking.domain.tenant.dto.CondominioResponse;
import com.urbanpark.parking.shared.enums.EstadoCondominio;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CondominioService {

    private final CondominioRepository condominioRepository;
    private final PlanRepository planRepository;

    public CondominioResponse crear(CondominioRequest request) {
        if (condominioRepository.existsByTitularEmail(request.getTitularEmail())) {
            throw new IllegalArgumentException("Ya existe un condominio con ese email");
        }

        Plan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new EntityNotFoundException("Plan no encontrado"));

        Condominio condominio = Condominio.builder()
                .nombre(request.getNombre())
                .apiBaseUrl(request.getApiBaseUrl())
                .titularNombre(request.getTitularNombre())
                .titularEmail(request.getTitularEmail())
                .titularTelefono(request.getTitularTelefono())
                .estado(EstadoCondominio.ACTIVO)
                .plan(plan)
                .build();

        return toResponse(condominioRepository.save(condominio));
    }

    public List<CondominioResponse> listarTodos() {
        return condominioRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CondominioResponse buscarPorId(UUID id) {
        return toResponse(findById(id));
    }

    public CondominioResponse actualizar(UUID id, CondominioRequest request) {
        Condominio condominio = findById(id);
        Plan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new EntityNotFoundException("Plan no encontrado"));

        condominio.setNombre(request.getNombre());
        condominio.setApiBaseUrl(request.getApiBaseUrl());
        condominio.setTitularNombre(request.getTitularNombre());
        condominio.setTitularEmail(request.getTitularEmail());
        condominio.setTitularTelefono(request.getTitularTelefono());
        condominio.setPlan(plan);

        return toResponse(condominioRepository.save(condominio));
    }

    public void cambiarEstado(UUID id, EstadoCondominio estado) {
        Condominio condominio = findById(id);
        condominio.setEstado(estado);
        condominioRepository.save(condominio);
    }

    private Condominio findById(UUID id) {
        return condominioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Condominio no encontrado"));
    }

    private CondominioResponse toResponse(Condominio c) {
        return CondominioResponse.builder()
                .id(c.getId())
                .nombre(c.getNombre())
                .apiBaseUrl(c.getApiBaseUrl())
                .titularNombre(c.getTitularNombre())
                .titularEmail(c.getTitularEmail())
                .titularTelefono(c.getTitularTelefono())
                .estado(c.getEstado())
                .planNombre(c.getPlan().getNombre())
                .fechaRegistro(c.getFechaRegistro())
                .build();
    }
}