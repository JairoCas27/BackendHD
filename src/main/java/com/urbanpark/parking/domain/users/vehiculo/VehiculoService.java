package com.urbanpark.parking.domain.users.vehiculo;

import com.urbanpark.parking.domain.tenant.TenantContext;
import com.urbanpark.parking.domain.users.vehiculo.dto.VehiculoRequest;
import com.urbanpark.parking.domain.users.vehiculo.dto.VehiculoResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;

    public VehiculoResponse crear(VehiculoRequest request) {
        UUID tenantId = TenantContext.getTenantId();

        if (vehiculoRepository.existsByPlacaAndTenantId(request.getPlaca(), tenantId)) {
            throw new IllegalArgumentException("Ya existe un vehículo con esa placa en este condominio");
        }

        Vehiculo vehiculo = Vehiculo.builder()
                .tenantId(tenantId)
                .usuarioId(request.getUsuarioId())
                .placa(request.getPlaca().toUpperCase())
                .marca(request.getMarca())
                .modelo(request.getModelo())
                .color(request.getColor())
                .tipo(request.getTipo())
                .activo(true)
                .build();

        return toResponse(vehiculoRepository.save(vehiculo));
    }

    public List<VehiculoResponse> listarTodos() {
        return vehiculoRepository.findAllByTenantId(TenantContext.getTenantId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<VehiculoResponse> listarPorUsuario(UUID usuarioId) {
        return vehiculoRepository
                .findAllByUsuarioIdAndTenantId(usuarioId, TenantContext.getTenantId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public VehiculoResponse buscarPorPlaca(String placa) {
        Vehiculo vehiculo = vehiculoRepository
                .findByPlacaAndTenantId(placa.toUpperCase(), TenantContext.getTenantId())
                .orElseThrow(() -> new EntityNotFoundException("Vehículo no encontrado"));
        return toResponse(vehiculo);
    }

    public VehiculoResponse buscarPorId(UUID id) {
        return toResponse(findById(id));
    }

    public VehiculoResponse actualizar(UUID id, VehiculoRequest request) {
        Vehiculo vehiculo = findById(id);
        vehiculo.setMarca(request.getMarca());
        vehiculo.setModelo(request.getModelo());
        vehiculo.setColor(request.getColor());
        vehiculo.setTipo(request.getTipo());
        return toResponse(vehiculoRepository.save(vehiculo));
    }

    public void desactivar(UUID id) {
        Vehiculo vehiculo = findById(id);
        vehiculo.setActivo(false);
        vehiculoRepository.save(vehiculo);
    }

    private Vehiculo findById(UUID id) {
        Vehiculo vehiculo = vehiculoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehículo no encontrado"));

        if (!vehiculo.getTenantId().equals(TenantContext.getTenantId())) {
            throw new EntityNotFoundException("Vehículo no encontrado");
        }

        return vehiculo;
    }

    private VehiculoResponse toResponse(Vehiculo v) {
        return VehiculoResponse.builder()
                .id(v.getId())
                .usuarioId(v.getUsuarioId())
                .placa(v.getPlaca())
                .marca(v.getMarca())
                .modelo(v.getModelo())
                .color(v.getColor())
                .tipo(v.getTipo())
                .activo(v.isActivo())
                .build();
    }
}