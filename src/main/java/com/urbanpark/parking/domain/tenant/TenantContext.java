package com.urbanpark.parking.domain.tenant;

import java.util.UUID;

public class TenantContext {

    private static final ThreadLocal<UUID> currentTenant  = new ThreadLocal<>();
    private static final ThreadLocal<UUID> currentUsuario = new ThreadLocal<>();

    private TenantContext() {}

    // ─── Tenant ──────────────────────────────────────────────────────

    public static void setTenantId(UUID tenantId) {
        currentTenant.set(tenantId);
    }

    public static UUID getTenantId() {
        return currentTenant.get();
    }

    // ─── Usuario ─────────────────────────────────────────────────────

    public static void setUsuarioId(UUID usuarioId) {
        currentUsuario.set(usuarioId);
    }

    public static UUID getUsuarioId() {
        return currentUsuario.get();
    }

    // ─── Limpieza obligatoria al final del request ────────────────────

    public static void clear() {
        currentTenant.remove();
        currentUsuario.remove();
    }
}