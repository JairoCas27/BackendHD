package com.urbanpark.parking.domain.users.vehiculo;

import com.urbanpark.parking.domain.audit.AuditService;
import com.urbanpark.parking.domain.integration.UsuarioSesion;
import com.urbanpark.parking.domain.integration.UsuarioSesionRepository;
import com.urbanpark.parking.domain.tenant.Condominio;
import com.urbanpark.parking.domain.tenant.CondominioRepository;
import com.urbanpark.parking.domain.tenant.TenantContext;
import com.urbanpark.parking.domain.users.vehiculo.dto.VehiculoExternalResponse;
import com.urbanpark.parking.domain.users.vehiculo.dto.VehiculoRequest;
import com.urbanpark.parking.domain.users.vehiculo.dto.VehiculoResponse;
import com.urbanpark.parking.shared.enums.TipoAccionAudit;
import com.urbanpark.parking.shared.enums.TipoVehiculo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final VehiculoExternalClient vehiculoExternalClient;
    private final UsuarioSesionRepository usuarioSesionRepository;
    private final CondominioRepository condominioRepository;
    private final AuditService auditService;

    // ─── Lista todos los vehiculos del tenant (admin/seguridad) ──────

    public List<VehiculoResponse> listarTodos() {
        UsuarioSesion sesion = getSesionActual();
        Condominio condominio = getCondominio();

        return vehiculoExternalClient
                .listarTodos(condominio.getApiBaseUrl(), sesion.getAccessToken())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ─── Lista solo los vehiculos del usuario autenticado ────────────

    public List<VehiculoResponse> listarMios() {
        UsuarioSesion sesion = getSesionActual();
        Condominio condominio = getCondominio();

        return vehiculoExternalClient
                .listarTodos(condominio.getApiBaseUrl(), sesion.getAccessToken())
                .stream()
                .filter(v -> esMiVehiculo(v, sesion.getExternalUserId(), sesion.getRol()))
                .map(this::toResponse)
                .toList();
    }

    // ─── Lista vehiculos de un usuario especifico (admin) ────────────

    public List<VehiculoResponse> listarPorUsuarioExterno(Long externalUserId) {
        UsuarioSesion sesion = getSesionActual();
        Condominio condominio = getCondominio();

        return vehiculoExternalClient
                .listarTodos(condominio.getApiBaseUrl(), sesion.getAccessToken())
                .stream()
                .filter(v -> perteneceAUsuario(v, externalUserId))
                .map(this::toResponse)
                .toList();
    }

    // ─── Registro local (placa en BD propia para accesos/reglas) ─────

    public VehiculoResponse crear(VehiculoRequest request) {
        UUID tenantId = TenantContext.getTenantId();

        if (vehiculoRepository.existsByPlacaAndTenantId(request.getPlaca().toUpperCase(), tenantId)) {
            throw new IllegalArgumentException(
                    "Ya existe un vehículo con esa placa en este condominio");
        }

        Vehiculo vehiculo = vehiculoRepository.save(Vehiculo.builder()
                .tenantId(tenantId)
                .usuarioId(request.getUsuarioId())
                .placa(request.getPlaca().toUpperCase())
                .marca(request.getMarca())
                .modelo(request.getModelo())
                .color(request.getColor())
                .tipo(request.getTipo())
                .activo(true)
                .build());

        auditService.registrar(
                tenantId,
                TenantContext.getUsuarioId(),
                TipoAccionAudit.VEHICULO_REGISTRADO,
                "Vehiculo",
                vehiculo.getId().toString(),
                Map.of(
                        "placa", vehiculo.getPlaca(),
                        "tipo", vehiculo.getTipo().name()
                )
        );

        return toLocalResponse(vehiculo);
    }

    public VehiculoResponse buscarPorPlaca(String placa) {
        Vehiculo vehiculo = vehiculoRepository
                .findByPlacaAndTenantId(placa.toUpperCase(), TenantContext.getTenantId())
                .orElseThrow(() -> new EntityNotFoundException("Vehículo no encontrado"));

        return toLocalResponse(vehiculo);
    }

    public VehiculoResponse buscarPorId(UUID id) {
        return toLocalResponse(findById(id));
    }

    public VehiculoResponse actualizar(UUID id, VehiculoRequest request) {
        Vehiculo vehiculo = findById(id);
        vehiculo.setMarca(request.getMarca());
        vehiculo.setModelo(request.getModelo());
        vehiculo.setColor(request.getColor());
        vehiculo.setTipo(request.getTipo());

        Vehiculo actualizado = vehiculoRepository.save(vehiculo);

        auditService.registrar(
                TenantContext.getTenantId(),
                TenantContext.getUsuarioId(),
                TipoAccionAudit.VEHICULO_ACTUALIZADO,
                "Vehiculo",
                id.toString(),
                Map.of("placa", actualizado.getPlaca())
        );

        return toLocalResponse(actualizado);
    }

    public void desactivar(UUID id) {
        Vehiculo vehiculo = findById(id);
        vehiculo.setActivo(false);
        vehiculoRepository.save(vehiculo);

        auditService.registrar(
                TenantContext.getTenantId(),
                TenantContext.getUsuarioId(),
                TipoAccionAudit.VEHICULO_DESACTIVADO,
                "Vehiculo",
                id.toString(),
                Map.of("placa", vehiculo.getPlaca())
        );
    }

    // ─── Helpers ─────────────────────────────────────────────────────

    private boolean esMiVehiculo(VehiculoExternalResponse v, Long externalUserId, String rol) {
        if (externalUserId == null) {
            return false;
        }

        if ("PROPIETARIO".equalsIgnoreCase(rol)) {
            return externalUserId.equals(v.getPropietarioId());
        }

        if ("INQUILINO".equalsIgnoreCase(rol)) {
            return externalUserId.equals(v.getInquilinoId());
        }

        return false;
    }

    private boolean perteneceAUsuario(VehiculoExternalResponse v, Long externalUserId) {
        return externalUserId != null &&
                (externalUserId.equals(v.getPropietarioId()) ||
                        externalUserId.equals(v.getInquilinoId()));
    }

    private UsuarioSesion getSesionActual() {
        UUID usuarioId = TenantContext.getUsuarioId();

        if (usuarioId == null) {
            throw new EntityNotFoundException("Usuario autenticado no encontrado en contexto");
        }

        return usuarioSesionRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Sesion no encontrada"));
    }

    private Condominio getCondominio() {
        UUID tenantId = TenantContext.getTenantId();

        if (tenantId == null) {
            throw new EntityNotFoundException("Tenant no encontrado en contexto");
        }

        return condominioRepository.findById(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Condominio no encontrado"));
    }

    private Vehiculo findById(UUID id) {
        Vehiculo vehiculo = vehiculoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehículo no encontrado"));

        if (!vehiculo.getTenantId().equals(TenantContext.getTenantId())) {
            throw new EntityNotFoundException("Vehículo no encontrado");
        }

        return vehiculo;
    }

    private VehiculoResponse toResponse(VehiculoExternalResponse v) {
        return VehiculoResponse.builder()
                .externalId(v.getId())
                .placa(v.getPlaca())
                .marca(v.getMarca())
                .modelo(v.getModelo())
                .color(v.getColor())
                .tipo(mapTipoVehiculo(v.getTipo()))
                .propietarioId(v.getPropietarioId())
                .inquilinoId(v.getInquilinoId())
                .estacionamientoId(v.getEstacionamientoId())
                .build();
    }

    private VehiculoResponse toLocalResponse(Vehiculo v) {
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

    private TipoVehiculo mapTipoVehiculo(String tipoExterno) {
        if (tipoExterno == null || tipoExterno.isBlank()) {
            return null;
        }

        return switch (tipoExterno.trim().toUpperCase()) {
            case "AUTO" -> TipoVehiculo.RESIDENTE;
            case "MOTO" -> TipoVehiculo.MOTO;
            case "VISITANTE" -> TipoVehiculo.VISITANTE;
            default -> throw new IllegalArgumentException(
                    "Tipo de vehículo no soportado: " + tipoExterno);
        };
    }
}