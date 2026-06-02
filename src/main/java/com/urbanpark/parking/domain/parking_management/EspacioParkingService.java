package com.urbanpark.parking.domain.parking_management;

import com.urbanpark.parking.domain.parking_management.dto.EspacioRequest;
import com.urbanpark.parking.domain.parking_management.dto.EspacioResponse;
import com.urbanpark.parking.domain.parking_management.dto.OcupacionResponse;
import com.urbanpark.parking.domain.tenant.TenantContext;
import com.urbanpark.parking.shared.enums.EstadoEspacio;
import com.urbanpark.parking.shared.enums.TipoEspacio;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EspacioParkingService {

    private final EspacioParkingRepository espacioRepository;

    public EspacioResponse crear(EspacioRequest request) {
        UUID tenantId = TenantContext.getTenantId();

        if (espacioRepository.existsByCodigoAndTenantId(request.getCodigo(), tenantId)) {
            throw new IllegalArgumentException("Ya existe un espacio con ese código");
        }

        EspacioParking espacio = EspacioParking.builder()
                .tenantId(tenantId)
                .codigo(request.getCodigo().toUpperCase())
                .zona(request.getZona().toUpperCase())
                .tipo(request.getTipo())
                .estado(EstadoEspacio.LIBRE)
                .build();

        return toResponse(espacioRepository.save(espacio));
    }

    public List<EspacioResponse> listarTodos() {
        return espacioRepository.findAllByTenantId(TenantContext.getTenantId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<EspacioResponse> listarPorEstado(EstadoEspacio estado) {
        return espacioRepository
                .findAllByTenantIdAndEstado(TenantContext.getTenantId(), estado)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<EspacioResponse> listarPorZona(String zona) {
        return espacioRepository
                .findAllByTenantIdAndZona(TenantContext.getTenantId(), zona.toUpperCase())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public EspacioResponse buscarPorId(UUID id) {
        return toResponse(findById(id));
    }

    public EspacioResponse actualizar(UUID id, EspacioRequest request) {
        EspacioParking espacio = findById(id);
        espacio.setCodigo(request.getCodigo().toUpperCase());
        espacio.setZona(request.getZona().toUpperCase());
        espacio.setTipo(request.getTipo());
        return toResponse(espacioRepository.save(espacio));
    }

    public void cambiarEstado(UUID id, EstadoEspacio estado) {
        EspacioParking espacio = findById(id);
        espacio.setEstado(estado);
        espacioRepository.save(espacio);
    }

    // Llamado internamente por feature/parking al registrar entrada
    public EspacioParking asignarEspacio(TipoEspacio tipo) {
        UUID tenantId = TenantContext.getTenantId();

        EspacioParking espacio = espacioRepository
                .findFirstByTenantIdAndEstadoAndTipo(tenantId, EstadoEspacio.LIBRE, tipo)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No hay espacios disponibles de tipo " + tipo));

        espacio.setEstado(EstadoEspacio.OCUPADO);
        return espacioRepository.save(espacio);
    }

    // Llamado internamente por feature/parking al registrar salida
    public void liberarEspacio(UUID espacioId) {
        EspacioParking espacio = espacioRepository.findById(espacioId)
                .orElseThrow(() -> new EntityNotFoundException("Espacio no encontrado"));
        espacio.setEstado(EstadoEspacio.LIBRE);
        espacio.setVehiculoActualId(null);
        espacioRepository.save(espacio);
    }

    public OcupacionResponse consultarOcupacion() {
        UUID tenantId = TenantContext.getTenantId();
        List<Object[]> resultados = espacioRepository.countByEstado(tenantId);

        Map<String, Long> conteo = new HashMap<>();
        long total = 0;

        for (Object[] row : resultados) {
            String estado = row[0].toString();
            long cantidad = (Long) row[1];
            conteo.put(estado, cantidad);
            total += cantidad;
        }

        long ocupados = conteo.getOrDefault("OCUPADO", 0L);
        long libres = conteo.getOrDefault("LIBRE", 0L);
        long reservados = conteo.getOrDefault("RESERVADO", 0L);
        long fueraServicio = conteo.getOrDefault("FUERA_SERVICIO", 0L);

        return OcupacionResponse.builder()
                .total((int) total)
                .ocupados((int) ocupados)
                .libres((int) libres)
                .reservados((int) reservados)
                .fueraServicio((int) fueraServicio)
                .porcentajeOcupacion(total > 0 ? (double) ocupados / total * 100 : 0)
                .build();
    }

    private EspacioParking findById(UUID id) {
        EspacioParking espacio = espacioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Espacio no encontrado"));

        if (!espacio.getTenantId().equals(TenantContext.getTenantId())) {
            throw new EntityNotFoundException("Espacio no encontrado");
        }

        return espacio;
    }

    private EspacioResponse toResponse(EspacioParking e) {
        return EspacioResponse.builder()
                .id(e.getId())
                .codigo(e.getCodigo())
                .zona(e.getZona())
                .tipo(e.getTipo())
                .estado(e.getEstado())
                .vehiculoActualId(e.getVehiculoActualId())
                .build();
    }
}