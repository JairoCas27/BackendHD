// shared/audit/AuditLogAspect.java
package com.urbanpark.parking.shared.audit;

import com.urbanpark.parking.domain.audit.AuditLogService;
import com.urbanpark.parking.domain.usuarios.UsuarioSaas;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {

    private final AuditLogService auditLogService;
    private static final ThreadLocal<Boolean> EN_AUDITORIA = ThreadLocal.withInitial(() -> Boolean.FALSE);

    @Around("@annotation(auditable)")
    public Object interceptar(ProceedingJoinPoint pjp, AuditableAction auditable) throws Throwable {
        if (EN_AUDITORIA.get()) {
            return pjp.proceed();
        }

        // ── Contexto HTTP ──────────────────────────────────────────────────
        String endpoint = "";
        String metodo   = "";
        String ip       = "desconocida";

        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest req = attrs.getRequest();
                endpoint = req.getRequestURI();
                metodo   = req.getMethod();
                ip       = resolverIp(req);
                
                if (endpoint.startsWith("/api/v1/audit")) {
                    return pjp.proceed();
                }
            } else {
                log.debug("Sin contexto HTTP activo en la interceptación de auditoría.");
            }
        } catch (Exception e) {
            log.warn("Error al extraer contexto HTTP: {}", e.getMessage());
        }

        // ── Usuario autenticado ────────────────────────────────────────────
        Long   usuarioId = null;
        String email     = "anónimo";
        String rol       = "N/A";

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String)) {
                email = auth.getName();
                
                Object principal = auth.getPrincipal();
                if (principal instanceof UsuarioSaas u) {
                    usuarioId = u.getId();
                    try {
                        rol = u.getRol() != null ? u.getRol().name() : "N/A";
                    } catch (Exception e) {
                        rol = "AUTENTICADO";
                        log.trace("Error al obtener rol del usuario: {}", e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Error al extraer contexto de seguridad: {}", e.getMessage());
        }

        // ── Ejecución y registro ───────────────────────────────────────────
        String desc = auditable.descripcion().isBlank()
                ? pjp.getSignature().getName()
                : auditable.descripcion();

        try {
            EN_AUDITORIA.set(Boolean.TRUE);
            Object resultado = pjp.proceed();
            auditLogService.registrar(
                    usuarioId, email, rol,
                    auditable.accion(), desc, auditable.entidad(),
                    endpoint, metodo, ip,
                    true, null
            );
            return resultado;
        } catch (Throwable ex) {
            auditLogService.registrar(
                    usuarioId, email, rol,
                    auditable.accion(), desc, auditable.entidad(),
                    endpoint, metodo, ip,
                    false, ex.getMessage()
            );
            throw ex;
        } finally {
            EN_AUDITORIA.set(Boolean.FALSE);
        }
    }

    private String resolverIp(HttpServletRequest req) {
        String ip = req.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = req.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            ip = "127.0.0.1";
        }
        return ip != null ? ip : "desconocida";
    }
}