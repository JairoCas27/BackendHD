package com.urbanpark.parking.domain.rules;

import com.urbanpark.parking.domain.rules.dto.ValidacionRequest;
import com.urbanpark.parking.domain.rules.dto.ValidacionResult;
import com.urbanpark.parking.shared.enums.TipoRegla;
import com.urbanpark.parking.shared.enums.TipoVehiculo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RuleEngine {

    private final ReglaAccesoRepository reglaRepository;

    public ValidacionResult validar(ValidacionRequest request) {
        List<ReglaAcceso> reglas = reglaRepository
                .findAllByTenantIdAndActivoTrue(request.getTenantId());

        for (ReglaAcceso regla : reglas) {
            ValidacionResult resultado = evaluarRegla(regla, request);
            if (!resultado.isAutorizado()) {
                return resultado;
            }
        }

        return ValidacionResult.autorizado();
    }

    private ValidacionResult evaluarRegla(ReglaAcceso regla, ValidacionRequest request) {
        return switch (regla.getTipo()) {
            case HORARIO_ACCESO      -> validarHorario(regla, request);
            case TIPO_USUARIO        -> validarTipoUsuario(regla, request);
            case VISITANTE_PERMITIDO -> validarVisitante(regla, request);
            case LIMITE_VEHICULOS    -> validarLimite(regla, request);
        };
    }

    // Valida si la hora actual está dentro del horario permitido
    private ValidacionResult validarHorario(ReglaAcceso regla, ValidacionRequest request) {
        Map<String, Object> config = regla.getConfiguracion();

        try {
            LocalTime horaInicio = LocalTime.parse((String) config.get("horaInicio"));
            LocalTime horaFin    = LocalTime.parse((String) config.get("horaFin"));
            LocalTime ahora      = LocalTime.now();

            if (ahora.isBefore(horaInicio) || ahora.isAfter(horaFin)) {
                return ValidacionResult.denegado(
                        "Acceso fuera del horario permitido (" + horaInicio + " - " + horaFin + ")"
                );
            }
        } catch (Exception e) {
            log.warn("Error evaluando regla de horario {}: {}", regla.getId(), e.getMessage());
        }

        return ValidacionResult.autorizado();
    }

    // Valida si el tipo de usuario tiene permitido el acceso
    private ValidacionResult validarTipoUsuario(ReglaAcceso regla, ValidacionRequest request) {
        Map<String, Object> config = regla.getConfiguracion();

        @SuppressWarnings("unchecked")
        List<String> rolesPermitidos = (List<String>) config.get("rolesPermitidos");

        if (rolesPermitidos != null && request.getRolUsuario() != null) {
            if (!rolesPermitidos.contains(request.getRolUsuario())) {
                return ValidacionResult.denegado(
                        "Rol " + request.getRolUsuario() + " no tiene acceso permitido"
                );
            }
        }

        return ValidacionResult.autorizado();
    }

    // Valida si el visitante tiene autorización activa
    private ValidacionResult validarVisitante(ReglaAcceso regla, ValidacionRequest request) {
        if (request.getTipoVehiculo() == TipoVehiculo.VISITANTE) {
            if (!request.isVisitanteAutorizado()) {
                return ValidacionResult.denegado(
                        "Visitante no tiene autorización activa para ingresar"
                );
            }
        }
        return ValidacionResult.autorizado();
    }

    // Valida límite de vehículos activos en el parking
    private ValidacionResult validarLimite(ReglaAcceso regla, ValidacionRequest request) {
        Map<String, Object> config = regla.getConfiguracion();

        try {
            int limiteMaximo   = (int) config.get("limiteMaximo");
            int vehiculosActivos = request.getVehiculosActivosEnParking();

            if (vehiculosActivos >= limiteMaximo) {
                return ValidacionResult.denegado(
                        "Parking lleno. Límite máximo de " + limiteMaximo + " vehículos alcanzado"
                );
            }
        } catch (Exception e) {
            log.warn("Error evaluando regla de límite {}: {}", regla.getId(), e.getMessage());
        }

        return ValidacionResult.autorizado();
    }
}