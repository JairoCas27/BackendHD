package com.urbanpark.parking.domain.tenant;

import java.util.UUID;

public class TenantContext {

    private static final ThreadLocal<UUID> currentTenant = new ThreadLocal<>();

    private TenantContext() {}

    public static void setTenantId(java.util.UUID tenantId) {
        currentTenant.set(tenantId);
    }

    public static java.util.UUID getTenantId() {
        return currentTenant.get();
    }

    public static void clear() {
        currentTenant.remove();
    }
}