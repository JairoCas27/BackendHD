package com.urbanpark.parking.domain.titulares;

import com.urbanpark.parking.domain.titulares.dto.TitularRequest;
import com.urbanpark.parking.domain.titulares.dto.TitularResponse;
import com.urbanpark.parking.domain.usuarios.UsuarioSaas;
import com.urbanpark.parking.domain.usuarios.UsuarioSaasService;
import com.urbanpark.parking.shared.audit.AuditableAction;
import com.urbanpark.parking.shared.enums.TipoAccionAudit;
import com.urbanpark.parking.shared.exceptions.AccesoDenegadoException;
import com.urbanpark.parking.shared.exceptions.ResourceNotFoundException;
import com.urbanpark.parking.shared.exceptions.ValidacionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TitularService {

    private final TitularRepository  titularRepository;
    private final UsuarioSaasService usuarioSaasService;

    @Transactional
    @AuditableAction(
            accion      = TipoAccionAudit.SAAS_USUARIO_CREADO,
            descripcion = "Cliente completa datos de titular",
            entidad     = "Titular"
    )
    public TitularResponse completarDatos(TitularRequest request) {
        UsuarioSaas usuario = usuarioSaasService.getUsuarioActual();

        if (titularRepository.existsByUsuarioSaasId(usuario.getId()))
            throw new ValidacionException("Ya completaste tus datos de titular");

        if (titularRepository.existsByRuc(request.getRuc()))
            throw new ValidacionException(
                    "Ya existe un titular con el RUC: " + request.getRuc());

        Titular titular = Titular.builder()
                .usuarioSaas(usuario)
                .razonSocial(request.getRazonSocial())
                .ruc(request.getRuc())
                .direccionFiscal(request.getDireccionFiscal())
                .representanteLegal(request.getRepresentanteLegal())
                .build();

        titularRepository.save(titular);
        return toResponse(titular);
    }

    public TitularResponse obtenerMiTitular() {
        UsuarioSaas usuario = usuarioSaasService.getUsuarioActual();
        Titular titular = findByUsuarioId(usuario.getId());
        return toResponse(titular);
    }

    @Transactional
    @AuditableAction(
            accion      = TipoAccionAudit.SAAS_USUARIO_ACTIVADO,
            descripcion = "Cliente actualiza datos de titular",
            entidad     = "Titular"
    )
    public TitularResponse actualizar(TitularRequest request) {
        UsuarioSaas usuario = usuarioSaasService.getUsuarioActual();
        Titular titular = findByUsuarioId(usuario.getId());

        if (!titular.getRuc().equals(request.getRuc())
                && titularRepository.existsByRuc(request.getRuc()))
            throw new ValidacionException(
                    "Ya existe un titular con el RUC: " + request.getRuc());

        titular.setRazonSocial(request.getRazonSocial());
        titular.setRuc(request.getRuc());
        titular.setDireccionFiscal(request.getDireccionFiscal());
        titular.setRepresentanteLegal(request.getRepresentanteLegal());

        titularRepository.save(titular);
        return toResponse(titular);
    }

    public TitularResponse obtenerPorId(Long id) {
        return toResponse(findById(id));
    }

    public List<TitularResponse> listarTodos() {
        return titularRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public Titular findByUsuarioId(Long usuarioId) {
        return titularRepository.findByUsuarioSaasId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Debes completar tus datos de titular primero"));
    }

    public Titular findById(Long id) {
        return titularRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Titular no encontrado con id: " + id));
    }

    public TitularResponse toResponse(Titular t) {
        return TitularResponse.builder()
                .id(t.getId())
                .usuarioSaasId(t.getUsuarioSaas().getId())
                .nombreCompletoUsuario(t.getUsuarioSaas().getNombreCompleto())
                .emailUsuario(t.getUsuarioSaas().getEmail())
                .razonSocial(t.getRazonSocial())
                .ruc(t.getRuc())
                .direccionFiscal(t.getDireccionFiscal())
                .representanteLegal(t.getRepresentanteLegal())
                .planId(t.getPlan() != null ? t.getPlan().getId() : null)
                .planNombre(t.getPlan() != null ? t.getPlan().getNombre() : null)
                .estadoPlan(t.getEstadoPlan())
                .fechaAsignacionPlan(t.getFechaAsignacionPlan())
                .build();
    }

    public Titular guardar(Titular titular) {
        return titularRepository.save(titular);
    }
}