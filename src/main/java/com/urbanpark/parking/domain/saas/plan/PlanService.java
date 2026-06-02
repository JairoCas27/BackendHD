package com.urbanpark.parking.domain.saas.plan;

import com.urbanpark.parking.domain.audit.AuditService;
import com.urbanpark.parking.domain.saas.plan.dto.PlanRequestDTO;
import com.urbanpark.parking.domain.saas.plan.dto.PlanResponseDTO;
import com.urbanpark.parking.shared.enums.EstadoPlan;
import com.urbanpark.parking.shared.enums.TipoAccionAudit;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;
    private final AuditService auditService;

    public List<PlanResponseDTO> listarActivos() {
        return planRepository.findAllByEstado(EstadoPlan.ACTIVO)
                .stream().map(this::toResponse).toList();
    }

    public List<PlanResponseDTO> listarTodos() {
        return planRepository.findAll()
                .stream().map(this::toResponse).toList();
    }

    public PlanResponseDTO crear(PlanRequestDTO request) {
        if (planRepository.existsByNombre(request.getNombre())) {
            throw new IllegalArgumentException(
                    "Ya existe un plan con el nombre: " + request.getNombre());
        }

        Plan plan = planRepository.save(Plan.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .precio(request.getPrecio())
                .maxEspacios(request.getMaxEspacios())
                .maxUsuarios(request.getMaxUsuarios())
                .estado(EstadoPlan.ACTIVO)
                .build());

        auditService.registrar(
                null, null,
                TipoAccionAudit.PLAN_CREADO,
                "Plan", plan.getId().toString(),
                Map.of(
                        "nombre", plan.getNombre(),
                        "precio", plan.getPrecio().toString(),
                        "maxEspacios", plan.getMaxEspacios(),
                        "maxUsuarios", plan.getMaxUsuarios()
                )
        );

        return toResponse(plan);
    }

    public PlanResponseDTO actualizar(UUID id, PlanRequestDTO request) {
        Plan plan = findById(id);
        plan.setNombre(request.getNombre());
        plan.setDescripcion(request.getDescripcion());
        plan.setPrecio(request.getPrecio());
        plan.setMaxEspacios(request.getMaxEspacios());
        plan.setMaxUsuarios(request.getMaxUsuarios());
        Plan actualizado = planRepository.save(plan);

        auditService.registrar(
                null, null,
                TipoAccionAudit.PLAN_ACTUALIZADO,
                "Plan", id.toString(),
                Map.of(
                        "nombre", actualizado.getNombre(),
                        "precio", actualizado.getPrecio().toString(),
                        "maxEspacios", actualizado.getMaxEspacios(),
                        "maxUsuarios", actualizado.getMaxUsuarios()
                )
        );

        return toResponse(actualizado);
    }

    public void activar(UUID id) {
        Plan plan = findById(id);
        plan.setEstado(EstadoPlan.ACTIVO);
        planRepository.save(plan);

        auditService.registrar(
                null, null,
                TipoAccionAudit.PLAN_ACTIVADO,
                "Plan", id.toString(),
                Map.of("nombre", plan.getNombre())
        );
    }

    public void desactivar(UUID id) {
        Plan plan = findById(id);
        plan.setEstado(EstadoPlan.INACTIVO);
        planRepository.save(plan);

        auditService.registrar(
                null, null,
                TipoAccionAudit.PLAN_DESACTIVADO,
                "Plan", id.toString(),
                Map.of("nombre", plan.getNombre())
        );
    }

    public void eliminar(UUID id) {
        Plan plan = findById(id);
        planRepository.delete(plan);

        auditService.registrar(
                null, null,
                TipoAccionAudit.PLAN_ELIMINADO,
                "Plan", id.toString(),
                Map.of("nombre", plan.getNombre())
        );
    }

    public Plan findById(UUID id) {
        return planRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Plan no encontrado"));
    }

    private PlanResponseDTO toResponse(Plan plan) {
        return PlanResponseDTO.builder()
                .id(plan.getId())
                .nombre(plan.getNombre())
                .descripcion(plan.getDescripcion())
                .precio(plan.getPrecio())
                .maxEspacios(plan.getMaxEspacios())
                .maxUsuarios(plan.getMaxUsuarios())
                .estado(plan.getEstado().name())
                .createdAt(plan.getCreatedAt())
                .build();
    }
}