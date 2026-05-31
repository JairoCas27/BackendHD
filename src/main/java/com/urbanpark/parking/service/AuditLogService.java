package com.urbanpark.parking.service;

import com.urbanpark.parking.domain.AuditLog;
import com.urbanpark.parking.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public AuditLog logEvent(String tenantId, String userId, String action, String details, String ipAddress) {
        AuditLog log = new AuditLog(tenantId, userId, action, details, ipAddress);
        return auditLogRepository.save(log);
    }

    public List<AuditLog> getLogsByTenant(String tenantId) {
        return auditLogRepository.findByTenantIdOrderByTimestampDesc(tenantId);
    }
}

