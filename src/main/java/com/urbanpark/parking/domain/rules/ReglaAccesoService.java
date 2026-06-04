package com.urbanpark.parking.domain.rules;

import com.urbanpark.parking.domain.condominios.Condominio;
import com.urbanpark.parking.domain.condominios.CondominioRepository;
import com.urbanpark.parking.domain.rules.dto.ReglaRequest;
import com.urbanpark.parking.domain.rules.dto.ReglaResponse;
import com.urbanpark.parking.domain.rules.dto.ValidacionRequest;
import com.urbanpark.parking.domain.rules.dto.ValidacionResult;
import com.urbanpark.parking.domain.titulares.Titular;
import com.urbanpark.parking.domain.titulares.TitularService;
import com.urbanpark.parking.domain.usuarios.UsuarioSaas;
import com.urbanpark.parking.domain.usuarios.UsuarioSaasService;
import com.urbanpark.parking.shared.enums.EstadoCondominio;
import com.urbanpark.parking.shared.exceptions.AccesoDenegadoException;
import com.urbanpark.parking.shared.exceptions.ResourceNotFoundException;
import com.urbanpark.parking.shared.exceptions.ValidacionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReglaAccesoService {

    private final ReglaAccesoRepository reglaAccesoRepository;
    private final CondominioRepository condominioRepository;
    private final TitularService titularService;
    private final UsuarioSaasService usuarioSaasService;
    private final RuleEngine ruleEngine;

    @Transactional
    public ReglaResponse crear(Long condominioId, ReglaRequest request) {
        Condominio condominio = obtenerCondominioDelCliente(condominioId);
        ruleEngine.validarConfiguracion(request.getTipo(), request.getConfiguracion());

        if (reglaAccesoRepository.existsByCondominioIdAndNombreIgnoreCase(
                condominioId, request.getNombre())) {
            throw new ValidacionException(
                    "Ya existe una regla con el nombre: " + request.getNombre());
        }

        ReglaAcceso regla = ReglaAcceso.builder()
                .condominio(condominio)
                .tipo(request.getTipo())
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .configuracion(request.getConfiguracion())
                .activa(request.getActiva() == null || request.getActiva())
                .build();

        reglaAccesoRepository.save(regla);
        return toResponse(regla);
    }

    public List<ReglaResponse> listarPorCondominio(Long condominioId) {
        obtenerCondominioDelCliente(condominioId);
        return reglaAccesoRepository.findAllByCondominioIdOrderByIdDesc(condominioId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ReglaResponse obtener(Long condominioId, Long reglaId) {
        return toResponse(findReglaDelCliente(condominioId, reglaId));
    }

    @Transactional
    public ReglaResponse actualizar(Long condominioId, Long reglaId, ReglaRequest request) {
        ReglaAcceso regla = findReglaDelCliente(condominioId, reglaId);
        ruleEngine.validarConfiguracion(request.getTipo(), request.getConfiguracion());

        if (reglaAccesoRepository.existsByCondominioIdAndNombreIgnoreCaseAndIdNot(
                condominioId, request.getNombre(), reglaId)) {
            throw new ValidacionException(
                    "Ya existe otra regla con el nombre: " + request.getNombre());
        }

        regla.setTipo(request.getTipo());
        regla.setNombre(request.getNombre());
        regla.setDescripcion(request.getDescripcion());
        regla.setConfiguracion(request.getConfiguracion());
        if (request.getActiva() != null) {
            regla.setActiva(request.getActiva());
        }

        reglaAccesoRepository.save(regla);
        return toResponse(regla);
    }

    @Transactional
    public void eliminar(Long condominioId, Long reglaId) {
        ReglaAcceso regla = findReglaDelCliente(condominioId, reglaId);
        reglaAccesoRepository.delete(regla);
    }

    public ValidacionResult validar(Long condominioId, ValidacionRequest request) {
        obtenerCondominioDelCliente(condominioId);
        return ruleEngine.evaluar(condominioId, request);
    }

    private Condominio obtenerCondominioDelCliente(Long condominioId) {
        UsuarioSaas usuario = usuarioSaasService.getUsuarioActual();
        Titular titular = titularService.findByUsuarioId(usuario.getId());

        Condominio condominio = condominioRepository.findById(condominioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Condominio no encontrado con id: " + condominioId));

        if (!condominio.getTitular().getId().equals(titular.getId())) {
            throw new AccesoDenegadoException("No tienes acceso a este condominio");
        }

        if (condominio.getEstado() == EstadoCondominio.RECHAZADO) {
            throw new ValidacionException(
                    "No puedes gestionar reglas en un condominio rechazado");
        }

        return condominio;
    }

    private ReglaAcceso findReglaDelCliente(Long condominioId, Long reglaId) {
        obtenerCondominioDelCliente(condominioId);
        return reglaAccesoRepository.findByIdAndCondominioId(reglaId, condominioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Regla no encontrada con id: " + reglaId));
    }

    private ReglaResponse toResponse(ReglaAcceso r) {
        return ReglaResponse.builder()
                .id(r.getId())
                .condominioId(r.getCondominio().getId())
                .condominioNombre(r.getCondominio().getNombre())
                .tipo(r.getTipo())
                .nombre(r.getNombre())
                .descripcion(r.getDescripcion())
                .configuracion(r.getConfiguracion())
                .activa(r.isActiva())
                .fechaCreacion(r.getFechaCreacion())
                .fechaActualizacion(r.getFechaActualizacion())
                .build();
    }
}
