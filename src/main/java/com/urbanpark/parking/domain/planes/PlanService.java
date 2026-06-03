package com.urbanpark.parking.domain.planes;

import com.urbanpark.parking.domain.planes.dto.PlanRequest;
import com.urbanpark.parking.domain.planes.dto.PlanResponse;
import com.urbanpark.parking.shared.enums.EstadoPlan;
import com.urbanpark.parking.shared.exceptions.ResourceNotFoundException;
import com.urbanpark.parking.shared.exceptions.ValidacionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;

    @Transactional
    public PlanResponse crear(PlanRequest request) {
        if (planRepository.existsByNombre(request.getNombre()))
            throw new ValidacionException("Ya existe un plan con el nombre: " + request.getNombre());

        Plan plan = Plan.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .limiteCondominios(request.getLimiteCondominios())
                .precio(request.getPrecio())
                .moneda(request.getMoneda())
                .estado(request.getEstado())
                .build();

        planRepository.save(plan);
        return toResponse(plan);
    }

    @Transactional
    public PlanResponse actualizar(Long id, PlanRequest request) {
        Plan plan = findById(id);

        if (!plan.getNombre().equals(request.getNombre())
                && planRepository.existsByNombre(request.getNombre()))
            throw new ValidacionException("Ya existe un plan con el nombre: " + request.getNombre());

        plan.setNombre(request.getNombre());
        plan.setDescripcion(request.getDescripcion());
        plan.setLimiteCondominios(request.getLimiteCondominios());
        plan.setPrecio(request.getPrecio());
        plan.setMoneda(request.getMoneda());
        plan.setEstado(request.getEstado());

        planRepository.save(plan);
        return toResponse(plan);
    }

    @Transactional
    public void eliminar(Long id) {
        Plan plan = findById(id);
        planRepository.delete(plan);
    }

    public PlanResponse obtenerPorId(Long id) {
        return toResponse(findById(id));
    }

    // Público: solo planes activos
    public List<PlanResponse> listarActivos() {
        return planRepository.findAllByEstado(EstadoPlan.ACTIVO)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Admin: todos los planes
    public List<PlanResponse> listarTodos() {
        return planRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public Plan findById(Long id) {
        return planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Plan no encontrado con id: " + id));
    }

    public PlanResponse toResponse(Plan p) {
        return PlanResponse.builder()
                .id(p.getId())
                .nombre(p.getNombre())
                .descripcion(p.getDescripcion())
                .limiteCondominios(p.getLimiteCondominios())
                .maxCondominios(p.getLimiteCondominios().getValor())
                .precio(p.getPrecio())
                .moneda(p.getMoneda())
                .estado(p.getEstado())
                .creadoEn(p.getCreadoEn())
                .build();
    }
}