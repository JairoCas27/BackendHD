package com.urbanpark.parking.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private String tenantId;

    @Column(nullable = false, updatable = false)
    private String userId;

    @Column(nullable = false, updatable = false)
    private String action;

    @Column(nullable = false, length = 1000, updatable = false)
    private String details;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private AuditSeverity severity;

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false, updatable = false)
    private String ipAddress;

    public AuditLog() {
    }

    public AuditLog(String tenantId, String userId, String action, String details, AuditSeverity severity,
        String ipAddress) {
        this.tenantId = tenantId;
        this.userId = userId;
        this.action = action;
        this.details = details;
        this.severity = severity;
        this.timestamp = LocalDateTime.now();
        this.ipAddress = ipAddress;
    }

    public Long getId() {
        return id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getUserId() {
        return userId;
    }

    public String getAction() {
        return action;
    }

    public String getDetails() {
        return details;
    }

    public AuditSeverity getSeverity() { 
        return severity; }


    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getIpAddress() {
        return ipAddress;
    }
}
