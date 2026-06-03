package com.urbanpark.parking.domain.titulares;

import com.urbanpark.parking.domain.titulares.dto.TitularRequest;
import com.urbanpark.parking.domain.titulares.dto.TitularResponse;
import com.urbanpark.parking.domain.usuarios.UsuarioSaas;
import com.urbanpark.parking.domain.usuarios.UsuarioSaasService;
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

    private final TitularRepository titularRepository;
    private final UsuarioSaasService usuarioSaasService;

    // ─── Completar datos del titular (el propio cliente) ─────
    @Transactional
    public TitularResponse completarDatos(TitularRequest request) {
        UsuarioSaas usuario = usuarioSaasService.getUsuarioActual();

        if (titularRepository.existsByUsuarioSaasId(usuario.getId()))
            throw new ValidacionException("Ya completaste tus datos de titular");

        if (titularRepository.existsByRuc(request.getRuc()))
            throw new ValidacionException("Ya existe un titular con el RUC: " + request.getRuc());

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

    // ─── Ver mis datos de titular ─────────────────────────────
    public TitularResponse obtenerMiTitular() {
        UsuarioSaas usuario = usuarioSaasService.getUsuarioActual();
        Titular titular = findByUsuarioId(usuario.getId());
        return toResponse(titular);
    }

    // ─── Actualizar datos del titular ────────────────────────
    @Transactional
    public TitularResponse actualizar(TitularRequest request) {
        UsuarioSaas usuario = usuarioSaasService.getUsuarioActual();
        Titular titular = findByUsuarioId(usuario.getId());

        // Si cambia el RUC, verificar que no esté en uso por otro
        if (!titular.getRuc().equals(request.getRuc())
                && titularRepository.existsByRuc(request.getRuc()))
            throw new ValidacionException("Ya existe un titular con el RUC: " + request.getRuc());

        titular.setRazonSocial(request.getRazonSocial());
        titular.setRuc(request.getRuc());
        titular.setDireccionFiscal(request.getDireccionFiscal());
        titular.setRepresentanteLegal(request.getRepresentanteLegal());

        titularRepository.save(titular);
        return toResponse(titular);
    }

    // ─── Ver titular por ID (admin) ───────────────────────────
    public TitularResponse obtenerPorId(Long id) {
        return toResponse(findById(id));
    }

    // ─── Listar todos los titulares (admin) ───────────────────
    public List<TitularResponse> listarTodos() {
        return titularRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ─── Helpers ──────────────────────────────────────────────
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
}