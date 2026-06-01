package com.urbanpark.parking.integration.health;

import com.urbanpark.parking.integration.condominio.CondominioApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalServiceHealthCheck implements HealthIndicator {

    private final CondominioApiClient condominioApiClient;

    @Override
    public Health health() {
        Map<String, Object> details = new HashMap<>();
        details.put("timestamp", LocalDateTime.now().toString());
        details.put("service", "condominio-api");

        try {
            // Verificar conectividad con un condominio de prueba
            boolean conectado = condominioApiClient.verificarConectividad("1");

            if (conectado) {
                details.put("status", "UP");
                details.put("latency", "normal");
                return Health.up()
                        .withDetails(details)
                        .build();
            } else {
                details.put("status", "DOWN");
                details.put("reason", "No hay respuesta del servicio");
                return Health.down()
                        .withDetails(details)
                        .build();
            }

        } catch (Exception e) {
            log.error("Health check fallo: {}", e.getMessage());
            details.put("status", "DOWN");
            details.put("error", e.getMessage());
            details.put("errorType", e.getClass().getSimpleName());
            return Health.down()
                    .withDetails(details)
                    .withException(e)
                    .build();
        }
    }
}