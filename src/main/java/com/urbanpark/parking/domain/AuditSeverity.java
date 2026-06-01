package com.urbanpark.parking.domain;

public enum AuditSeverity {
    INFO,      // Consultas, lecturas de datos
    LOW,       // Registros operativos estándar (entradas/salidas)
    MEDIUM,    // Modificaciones de configuraciones o reglas
    HIGH       // Incidentes de seguridad o accesos denegados
}

