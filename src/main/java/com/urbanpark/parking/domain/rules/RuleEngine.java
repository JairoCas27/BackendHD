package com.urbanpark.parking.domain.rules;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.urbanpark.parking.domain.rules.dto.ValidacionRequest;
import com.urbanpark.parking.domain.rules.dto.ValidacionResult;
import com.urbanpark.parking.shared.enums.TipoRegla;
import com.urbanpark.parking.shared.exceptions.ValidacionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RuleEngine {

    private final ReglaAccesoRepository reglaAccesoRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ValidacionResult evaluar(Long condominioId, ValidacionRequest request) {
        List<ReglaAcceso> reglas = reglaAccesoRepository
                .findAllByCondominioIdAndActivaTrue(condominioId);

        if (reglas.isEmpty()) {
            return ValidacionResult.builder()
                    .permitido(true)
                    .motivo("Sin reglas activas")
                    .build();
        }

        for (ReglaAcceso regla : reglas) {
            ValidacionResult fallo = evaluarRegla(regla, request);
            if (!fallo.isPermitido()) {
                return fallo;
            }
        }

        return ValidacionResult.builder()
                .permitido(true)
                .motivo("Todas las reglas activas se cumplen")
                .build();
    }

    public void validarConfiguracion(TipoRegla tipo, String configuracion) {
        JsonNode json = parseJson(configuracion);
        switch (tipo) {
            case HORARIO_ACCESO -> {
                require(json, "horaInicio");
                require(json, "horaFin");
                LocalTime.parse(json.get("horaInicio").asText());
                LocalTime.parse(json.get("horaFin").asText());
            }
            case LIMITE_VEHICULOS -> {
                require(json, "maxVehiculos");
                if (json.get("maxVehiculos").asInt() < 1)
                    throw new ValidacionException("maxVehiculos debe ser mayor a 0");
            }
            case TIPO_USUARIO -> {
                require(json, "rolesPermitidos");
                if (!json.get("rolesPermitidos").isArray() || json.get("rolesPermitidos").isEmpty())
                    throw new ValidacionException("rolesPermitidos debe ser un arreglo no vacío");
            }
            case VISITANTE_PERMITIDO -> require(json, "permitido");
        }
    }

    private ValidacionResult evaluarRegla(ReglaAcceso regla, ValidacionRequest request) {
        JsonNode json = parseJson(regla.getConfiguracion());
        LocalTime hora = request.getHoraAcceso() != null
                ? request.getHoraAcceso()
                : LocalTime.now();

        return switch (regla.getTipo()) {
            case HORARIO_ACCESO -> evaluarHorario(regla, json, hora);
            case LIMITE_VEHICULOS -> evaluarLimiteVehiculos(regla, json, request);
            case TIPO_USUARIO -> evaluarTipoUsuario(regla, json, request);
            case VISITANTE_PERMITIDO -> evaluarVisitante(regla, json, request);
        };
    }

    

    private ValidacionResult evaluarLimiteVehiculos(
            ReglaAcceso regla, JsonNode json, ValidacionRequest request) {

        int max = json.get("maxVehiculos").asInt();
        int activos = request.getVehiculosActivos() != null ? request.getVehiculosActivos() : 0;

        if (activos < max) {
            return ok();
        }
        return denegado(regla,
                "Límite de vehículos alcanzado (" + activos + "/" + max + ")");
    }

    private ValidacionResult evaluarTipoUsuario(
            ReglaAcceso regla, JsonNode json, ValidacionRequest request) {

        if (request.getRolUsuario() == null || request.getRolUsuario().isBlank()) {
            return denegado(regla, "Rol de usuario requerido para validar la regla");
        }

        for (JsonNode rol : json.get("rolesPermitidos")) {
            if (rol.asText().equalsIgnoreCase(request.getRolUsuario())) {
                return ok();
            }
        }
        return denegado(regla,
                "Rol no permitido: " + request.getRolUsuario());
    }

    private ValidacionResult evaluarVisitante(
            ReglaAcceso regla, JsonNode json, ValidacionRequest request) {

        boolean permitidoCfg = json.get("permitido").asBoolean();
        boolean esVisitante = Boolean.TRUE.equals(request.getEsVisitante());

        if (!esVisitante || permitidoCfg) {
            return ok();
        }
        return denegado(regla, "Visitantes no permitidos por configuración del condominio");
    }

    private ValidacionResult ok() {
        return ValidacionResult.builder().permitido(true).build();
    }

    private ValidacionResult denegado(ReglaAcceso regla, String motivo) {
        return ValidacionResult.builder()
                .permitido(false)
                .motivo(motivo)
                .reglaId(regla.getId())
                .reglaNombre(regla.getNombre())
                .build();
    }

    private JsonNode parseJson(String configuracion) {
        try {
            return objectMapper.readTree(configuracion);
        } catch (Exception e) {
            throw new ValidacionException("Configuración JSON inválida");
        }
    }

    private void require(JsonNode json, String field) {
        if (!json.has(field) || json.get(field).isNull()) {
            throw new ValidacionException("Falta el campo '" + field + "' en la configuración");
        }
    }
}

