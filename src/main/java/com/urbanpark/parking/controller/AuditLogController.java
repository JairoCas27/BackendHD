package com.urbanpark.parking.controller;

import com.urbanpark.parking.domain.AuditLog;
import com.urbanpark.parking.domain.AuditSeverity;
import com.urbanpark.parking.service.AuditLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/audit")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping("/logs")
    public ResponseEntity<List<AuditLog>> getTenantLogs(@RequestParam String tenantId) {
        return ResponseEntity.ok(auditLogService.getLogsByTenant(tenantId));
    }

    @PostMapping("/logs")
    public ResponseEntity<AuditLog> createLog(
            @RequestParam String tenantId,
            @RequestParam String userId,
            @RequestParam String action,
            @RequestParam String details,
            @RequestParam AuditSeverity severity,
            @RequestParam String ipAddress) {
        
        AuditLog createdLog = auditLogService.logEvent(tenantId, userId, action, details, severity, ipAddress);
        return ResponseEntity.ok(createdLog);
    }
}

