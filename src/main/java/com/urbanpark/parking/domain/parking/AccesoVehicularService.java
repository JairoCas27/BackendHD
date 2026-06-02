package com.urbanpark.parking.domain.parking;

import com.urbanpark.parking.domain.parking.dto.AccesoResponse;
import com.urbanpark.parking.domain.parking.dto.RegistroEntradaRequest;
import com.urbanpark.parking.domain.parking.dto.RegistroSalidaRequest;
import com.urbanpark.parking.domain.parking_management.EspacioParking;
import com.urbanpark.parking.domain.parking_management.EspacioParkingService;
import com.urbanpark.parking.domain.rules.RuleEngine;
import com.urbanpark.parking.domain.rules.dto.ValidacionRequest;
import com.urbanpark.parking.domain.rules.dto.ValidacionResult;
import com.urbanpark.parking.domain.tenant.TenantContext;
import com.urbanpark.parking.domain.users.vehiculo.Vehiculo;
import com.urbanpark.parking.domain.users.vehiculo.VehiculoRepository;
import com.urbanpark.parking.domain.users.visitante.VisitanteRepository;
import com.urbanpark.parking.shared.enums.MetodoAcceso;
import com.urbanpark.parking.shared.enums.TipoEspacio;
import com.urbanpark.parking.shared.enums.TipoEvento;
import com.urbanpark.parking.shared.enums.TipoVehiculo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccesoVehicularService {

    private final AccesoVehicularRepository accesoRepository;
    private final VehiculoRepository vehiculoRepository;
    private final VisitanteRepository visitanteRepository;
    private final EspacioParkingService espacioService;
    private final RuleEngine ruleEngine;

    @Transactional
    public AccesoResponse registrarEntrada(RegistroEntradaRequest request) {
        UUID tenantId = TenantContext.getTenantId();

        // 1. Buscar el vehículo por placa
        Vehiculo vehiculo = vehiculoRepository
                .findByPlacaAndTenantId(request.getPlaca().toUpperCase(), tenantId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Vehículo con placa " + request.getPlaca() + " no encontrado"));

        // 2. Verificar que no tiene ya un acceso abierto
        accesoRepository.findAccesoAbiertoPorPlaca(tenantId, request.getPlaca().toUpperCase())
                .ifPresent(a -> {
                    throw new IllegalArgumentException(
                            "El vehículo ya se encuentra dentro del parking");
                });

        // 3. Verificar si es visitante autorizado
        boolean visitanteAutorizado = visitanteRepository
                .findVisitanteActivoPorPlaca(
                        tenantId,
                        request.getPlaca().toUpperCase(),
                        LocalDateTime.now()
                )
                .isPresent();

        // 4. Contar vehículos activos para regla de límite
        int vehiculosActivos = accesoRepository.countVehiculosActivos(tenantId);

        // 5. Validar reglas del condominio
        ValidacionRequest validacion = ValidacionRequest.builder()
                .tenantId(tenantId)
                .vehiculoId(vehiculo.getId())
                .placa(request.getPlaca().toUpperCase())
                .tipoVehiculo(vehiculo.getTipo())
                .rolUsuario(request.getRolUsuario())
                .visitanteAutorizado(visitanteAutorizado)
                .vehiculosActivosEnParking(vehiculosActivos)
                .build();

        ValidacionResult resultado = ruleEngine.validar(validacion);

        // 6. Si no está autorizado → registrar intento denegado y retornar
        if (!resultado.isAutorizado()) {
            AccesoVehicular accesoDenegado = AccesoVehicular.builder()
                    .tenantId(tenantId)
                    .vehiculoId(vehiculo.getId())
                    .placa(request.getPlaca().toUpperCase())
                    .tipoEvento(TipoEvento.ENTRADA)
                    .metodo(request.getMetodo())
                    .agenteId(request.getAgenteId())
                    .timestampEntrada(LocalDateTime.now())
                    .autorizado(false)
                    .motivoRechazo(resultado.getMotivo())
                    .build();

            return toResponse(accesoRepository.save(accesoDenegado));
        }

        // 7. Asignar espacio disponible — mapeamos TipoVehiculo a TipoEspacio
        EspacioParking espacio = espacioService.asignarEspacio(
                mapearTipoEspacio(vehiculo.getTipo())
        );

        // 8. Registrar entrada autorizada
        AccesoVehicular acceso = AccesoVehicular.builder()
                .tenantId(tenantId)
                .vehiculoId(vehiculo.getId())
                .placa(request.getPlaca().toUpperCase())
                .espacioId(espacio.getId())
                .tipoEvento(TipoEvento.ENTRADA)
                .metodo(request.getMetodo())
                .agenteId(request.getAgenteId())
                .timestampEntrada(LocalDateTime.now())
                .autorizado(true)
                .build();

        return toResponse(accesoRepository.save(acceso));
    }

    @Transactional
    public AccesoResponse registrarSalida(RegistroSalidaRequest request) {
        UUID tenantId = TenantContext.getTenantId();

        // 1. Buscar acceso abierto por placa
        AccesoVehicular acceso = accesoRepository
                .findAccesoAbiertoPorPlaca(tenantId, request.getPlaca().toUpperCase())
                .orElseThrow(() -> new EntityNotFoundException(
                        "No se encontró entrada activa para la placa " + request.getPlaca()));

        // 2. Registrar salida y calcular duración
        LocalDateTime ahora = LocalDateTime.now();
        long minutos = ChronoUnit.MINUTES.between(acceso.getTimestampEntrada(), ahora);

        acceso.setTimestampSalida(ahora);
        acceso.setDuracionMinutos((int) minutos);
        acceso.setTipoEvento(TipoEvento.SALIDA);

        // 3. Liberar el espacio
        if (acceso.getEspacioId() != null) {
            espacioService.liberarEspacio(acceso.getEspacioId());
        }

        return toResponse(accesoRepository.save(acceso));
    }

    public List<AccesoResponse> listarTodos() {
        return accesoRepository.findAllByTenantId(TenantContext.getTenantId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AccesoResponse> listarPorVehiculo(UUID vehiculoId) {
        return accesoRepository
                .findAllByTenantIdAndVehiculoId(TenantContext.getTenantId(), vehiculoId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AccesoResponse> listarPorRango(LocalDateTime inicio, LocalDateTime fin) {
        return accesoRepository
                .findAllByTenantIdAndTimestampEntradaBetween(
                        TenantContext.getTenantId(), inicio, fin)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Mapper TipoVehiculo → TipoEspacio
    private TipoEspacio mapearTipoEspacio(TipoVehiculo tipoVehiculo) {
        return switch (tipoVehiculo) {
            case MOTO      -> TipoEspacio.MOTO;
            case RESIDENTE -> TipoEspacio.ESTANDAR;
            case VISITANTE -> TipoEspacio.ESTANDAR;
        };
    }

    private AccesoResponse toResponse(AccesoVehicular a) {
        return AccesoResponse.builder()
                .id(a.getId())
                .vehiculoId(a.getVehiculoId())
                .placa(a.getPlaca())
                .espacioId(a.getEspacioId())
                .tipoEvento(a.getTipoEvento())
                .metodo(a.getMetodo())
                .agenteId(a.getAgenteId())
                .timestampEntrada(a.getTimestampEntrada())
                .timestampSalida(a.getTimestampSalida())
                .duracionMinutos(a.getDuracionMinutos())
                .autorizado(a.isAutorizado())
                .motivoRechazo(a.getMotivoRechazo())
                .build();
    }
}