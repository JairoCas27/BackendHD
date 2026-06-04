package com.urbanpark.parking.domain.solicitudes;

import com.urbanpark.parking.domain.planes.Plan;
import com.urbanpark.parking.domain.planes.PlanService;
import com.urbanpark.parking.domain.solicitudes.dto.RevisionSolicitudRequest;
import com.urbanpark.parking.domain.solicitudes.dto.SolicitudPlanRequest;
import com.urbanpark.parking.domain.solicitudes.dto.SolicitudPlanResponse;
import com.urbanpark.parking.domain.titulares.Titular;
import com.urbanpark.parking.domain.titulares.TitularService;
import com.urbanpark.parking.domain.usuarios.UsuarioSaas;
import com.urbanpark.parking.domain.usuarios.UsuarioSaasService;
import com.urbanpark.parking.shared.enums.EstadoPlan;
import com.urbanpark.parking.shared.enums.EstadoSolicitud;
import com.urbanpark.parking.shared.enums.EstadoUsuarioSaas;
import com.urbanpark.parking.shared.exceptions.AccesoDenegadoException;
import com.urbanpark.parking.shared.exceptions.ResourceNotFoundException;
import com.urbanpark.parking.shared.exceptions.ValidacionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SolicitudPlanService {

    private final SolicitudPlanRepository solicitudPlanRepository;
    private final TitularService titularService;
    private final PlanService planService;
    private final UsuarioSaasService usuarioSaasService;

    // Cliente solicita un plan
    @Transactional
    public SolicitudPlanResponse solicitar(SolicitudPlanRequest request) {
        UsuarioSaas usuario = usuarioSaasService.getUsuarioActual();
        Titular titular = titularService.findByUsuarioId(usuario.getId());

        // No puede solicitar si ya tiene plan activo
        if (titular.getEstadoPlan() == EstadoPlan.ACTIVO)
            throw new ValidacionException("Ya tienes un plan activo");

        // No puede tener dos solicitudes pendientes a la vez
        if (solicitudPlanRepository.existsByTitularAndEstado(titular, EstadoSolicitud.PENDIENTE))
            throw new ValidacionException("Ya tienes una solicitud de plan pendiente de revisión");

        Plan plan = planService.findById(request.getPlanId());

        SolicitudPlan solicitud = SolicitudPlan.builder()
                .titular(titular)
                .plan(plan)
                .estado(EstadoSolicitud.PENDIENTE)
                .build();

        solicitudPlanRepository.save(solicitud);

        // Actualizar estado del usuario
        usuario.setEstado(EstadoUsuarioSaas.PENDIENTE_APROBACION);
        usuarioSaasService.guardar(usuario);

        return toResponse(solicitud);
    }

    // Cliente ve su última solicitud
    @Transactional(readOnly = true)
    public SolicitudPlanResponse obtenerMiSolicitud() {
        UsuarioSaas usuario = usuarioSaasService.getUsuarioActual();
        Titular titular = titularService.findByUsuarioId(usuario.getId());

        SolicitudPlan solicitud = solicitudPlanRepository
                .findTopByTitularOrderByFechaSolicitudDesc(titular)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No tienes solicitudes de plan registradas"));

        return toResponse(solicitud);
    }

    // Admin lista todas las solicitudes pendientes
    @Transactional(readOnly = true)
    public List<SolicitudPlanResponse> listarPendientes() {
        return solicitudPlanRepository.findAllByEstado(EstadoSolicitud.PENDIENTE)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Admin lista todas las solicitudes
    @Transactional(readOnly = true)
    public List<SolicitudPlanResponse> listarTodas() {
        return solicitudPlanRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Admin aprueba solicitud
    @Transactional
    public SolicitudPlanResponse aprobar(Long solicitudId) {
        UsuarioSaas admin = usuarioSaasService.getUsuarioActual();
        SolicitudPlan solicitud = findById(solicitudId);

        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE)
            throw new ValidacionException("Solo se pueden aprobar solicitudes en estado PENDIENTE");

        // Actualizar solicitud
        solicitud.setEstado(EstadoSolicitud.APROBADA);
        solicitud.setRevisadoPor(admin);
        solicitud.setFechaRevision(LocalDateTime.now());
        solicitudPlanRepository.save(solicitud);

        // Asignar plan al titular
        Titular titular = solicitud.getTitular();
        titular.setPlan(solicitud.getPlan());
        titular.setEstadoPlan(EstadoPlan.ACTIVO);
        titular.setFechaAsignacionPlan(LocalDateTime.now());
        titularService.guardar(titular);

        // Actualizar estado del usuario a ACTIVO
        UsuarioSaas usuarioTitular = titular.getUsuarioSaas();
        usuarioTitular.setEstado(EstadoUsuarioSaas.ACTIVO);
        usuarioSaasService.guardar(usuarioTitular);

        return toResponse(solicitud);
    }

    // Admin rechaza solicitud
    @Transactional
    public SolicitudPlanResponse rechazar(Long solicitudId, RevisionSolicitudRequest request) {
        UsuarioSaas admin = usuarioSaasService.getUsuarioActual();
        SolicitudPlan solicitud = findById(solicitudId);

        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE)
            throw new ValidacionException("Solo se pueden rechazar solicitudes en estado PENDIENTE");

        if (request.getMotivoRechazo() == null || request.getMotivoRechazo().isBlank())
            throw new ValidacionException("El motivo de rechazo es obligatorio");

        solicitud.setEstado(EstadoSolicitud.RECHAZADA);
        solicitud.setMotivoRechazo(request.getMotivoRechazo());
        solicitud.setRevisadoPor(admin);
        solicitud.setFechaRevision(LocalDateTime.now());
        solicitudPlanRepository.save(solicitud);

        // Regresar estado del usuario a PENDIENTE_PLAN para que pueda volver a solicitar
        UsuarioSaas usuarioTitular = solicitud.getTitular().getUsuarioSaas();
        usuarioTitular.setEstado(EstadoUsuarioSaas.PENDIENTE_PLAN);
        usuarioSaasService.guardar(usuarioTitular);

        return toResponse(solicitud);
    }

    // Helpers
    @Transactional(readOnly = true)
    protected SolicitudPlan findById(Long id) {
        return solicitudPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Solicitud no encontrada con id: " + id));
    }

    private SolicitudPlanResponse toResponse(SolicitudPlan s) {
        return SolicitudPlanResponse.builder()
                .id(s.getId())
                .titularId(s.getTitular().getId())
                .razonSocialTitular(s.getTitular().getRazonSocial())
                .planId(s.getPlan().getId())
                .planNombre(s.getPlan().getNombre())
                .estado(s.getEstado())
                .motivoRechazo(s.getMotivoRechazo())
                .revisadoPorId(s.getRevisadoPor() != null ? s.getRevisadoPor().getId() : null)
                .revisadoPorNombre(s.getRevisadoPor() != null
                        ? s.getRevisadoPor().getNombreCompleto() : null)
                .fechaRevision(s.getFechaRevision())
                .fechaSolicitud(s.getFechaSolicitud())
                .build();
    }
}