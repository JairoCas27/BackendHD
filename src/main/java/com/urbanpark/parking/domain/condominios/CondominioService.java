package com.urbanpark.parking.domain.condominios;

import com.urbanpark.parking.domain.condominios.dto.CondominioRequest;
import com.urbanpark.parking.domain.condominios.dto.CondominioResponse;
import com.urbanpark.parking.domain.condominios.dto.VerificacionRequest;
import com.urbanpark.parking.domain.titulares.Titular;
import com.urbanpark.parking.domain.titulares.TitularService;
import com.urbanpark.parking.domain.usuarios.UsuarioSaas;
import com.urbanpark.parking.domain.usuarios.UsuarioSaasService;
import com.urbanpark.parking.shared.audit.AuditableAction;
import com.urbanpark.parking.shared.enums.EstadoCondominio;
import com.urbanpark.parking.shared.enums.EstadoPlan;
import com.urbanpark.parking.shared.enums.TipoAccionAudit;
import com.urbanpark.parking.shared.exceptions.AccesoDenegadoException;
import com.urbanpark.parking.shared.exceptions.LimitePlanExcedidoException;
import com.urbanpark.parking.shared.exceptions.ResourceNotFoundException;
import com.urbanpark.parking.shared.exceptions.ValidacionException;
import com.urbanpark.parking.shared.utils.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CondominioService {

    private final CondominioRepository condominioRepository;
    private final TitularService       titularService;
    private final UsuarioSaasService   usuarioSaasService;

    @Transactional
    @AuditableAction(
            accion      = TipoAccionAudit.CONDOMINIO_CREADO,
            descripcion = "Cliente registra nuevo condominio",
            entidad     = "Condominio"
    )
    public CondominioResponse registrar(CondominioRequest request) {
        UsuarioSaas usuario = usuarioSaasService.getUsuarioActual();
        Titular titular = titularService.findByUsuarioId(usuario.getId());

        if (titular.getEstadoPlan() != EstadoPlan.ACTIVO)
            throw new AccesoDenegadoException(
                    "Debes tener un plan activo para registrar condominios");

        long condominiosActivos = condominioRepository
                .countByTitularAndEstadoNot(titular, EstadoCondominio.RECHAZADO);

        if (!titular.getPlan().getLimiteCondominios().permite(condominiosActivos))
            throw new LimitePlanExcedidoException(
                    "Tu plan '" + titular.getPlan().getNombre() +
                            "' permite máximo " +
                            titular.getPlan().getLimiteCondominios().getValor() +
                            " condominio(s)");

        String slug = SlugUtils.generate(request.getNombre());
        if (condominioRepository.existsBySlug(slug))
            slug = slug + "-" + System.currentTimeMillis();

        Condominio condominio = Condominio.builder()
                .titular(titular)
                .nombre(request.getNombre())
                .slug(slug)
                .razonSocial(request.getRazonSocial())
                .ruc(request.getRuc())
                .direccion(request.getDireccion())
                .emailCondominio(request.getEmailCondominio())
                .telefonoCondominio(request.getTelefonoCondominio())
                .apiBaseUrl(request.getApiBaseUrl())
                .estado(EstadoCondominio.PENDIENTE_VERIFICACION)
                .build();

        condominioRepository.save(condominio);
        return toResponse(condominio);
    }

    public List<CondominioResponse> listarMisCondominios() {
        UsuarioSaas usuario = usuarioSaasService.getUsuarioActual();
        Titular titular = titularService.findByUsuarioId(usuario.getId());

        return condominioRepository.findAllByTitular(titular)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<CondominioResponse> listarPendientes() {
        return condominioRepository
                .findAllByEstado(EstadoCondominio.PENDIENTE_VERIFICACION)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<CondominioResponse> listarTodos() {
        return condominioRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    @AuditableAction(
            accion      = TipoAccionAudit.CONDOMINIO_ACTIVADO,
            descripcion = "Admin aprueba condominio",
            entidad     = "Condominio"
    )
    public CondominioResponse aprobar(Long id) {
        UsuarioSaas admin = usuarioSaasService.getUsuarioActual();
        Condominio condominio = findById(id);

        if (condominio.getEstado() != EstadoCondominio.PENDIENTE_VERIFICACION)
            throw new ValidacionException(
                    "Solo se pueden aprobar condominios en estado PENDIENTE_VERIFICACION");

        condominio.setEstado(EstadoCondominio.ACTIVO);
        condominio.setVerificadoPor(admin);
        condominio.setFechaVerificacion(LocalDateTime.now());
        condominioRepository.save(condominio);

        return toResponse(condominio);
    }

    @Transactional
    @AuditableAction(
            accion      = TipoAccionAudit.CONDOMINIO_DESACTIVADO,
            descripcion = "Admin rechaza condominio",
            entidad     = "Condominio"
    )
    public CondominioResponse rechazar(Long id, VerificacionRequest request) {
        UsuarioSaas admin = usuarioSaasService.getUsuarioActual();
        Condominio condominio = findById(id);

        if (condominio.getEstado() != EstadoCondominio.PENDIENTE_VERIFICACION)
            throw new ValidacionException(
                    "Solo se pueden rechazar condominios en estado PENDIENTE_VERIFICACION");

        if (request.getMotivoRechazo() == null || request.getMotivoRechazo().isBlank())
            throw new ValidacionException("El motivo de rechazo es obligatorio");

        condominio.setEstado(EstadoCondominio.RECHAZADO);
        condominio.setMotivoRechazo(request.getMotivoRechazo());
        condominio.setVerificadoPor(admin);
        condominio.setFechaVerificacion(LocalDateTime.now());
        condominioRepository.save(condominio);

        return toResponse(condominio);
    }

    private Condominio findById(Long id) {
        return condominioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Condominio no encontrado con id: " + id));
    }

    private CondominioResponse toResponse(Condominio c) {
        return CondominioResponse.builder()
                .id(c.getId())
                .titularId(c.getTitular().getId())
                .razonSocialTitular(c.getTitular().getRazonSocial())
                .nombre(c.getNombre())
                .slug(c.getSlug())
                .razonSocial(c.getRazonSocial())
                .ruc(c.getRuc())
                .direccion(c.getDireccion())
                .emailCondominio(c.getEmailCondominio())
                .telefonoCondominio(c.getTelefonoCondominio())
                .apiBaseUrl(c.getApiBaseUrl())
                .estado(c.getEstado())
                .verificadoPorId(c.getVerificadoPor() != null
                        ? c.getVerificadoPor().getId() : null)
                .verificadoPorNombre(c.getVerificadoPor() != null
                        ? c.getVerificadoPor().getNombreCompleto() : null)
                .motivoRechazo(c.getMotivoRechazo())
                .fechaVerificacion(c.getFechaVerificacion())
                .fechaRegistro(c.getFechaRegistro())
                .build();
    }
}