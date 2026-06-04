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

    @Around("@annotation(auditable)")
    public Object interceptar(ProceedingJoinPoint pjp, AuditableAction auditable) throws Throwable {

        // 🛡️ CONTROL ANTIBUCLE INTERNO
        String nombreMetodoActual = pjp.getSignature().getName();
        if ("registrar".equals(nombreMetodoActual) || "filtrar".equals(nombreMetodoActual) || "listarTodos".equals(nombreMetodoActual)) {
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
            }
        } catch (Exception ignored) {}

        // 🛡️ Evitar procesar rutas de auditoría por URL secundaria
        if (endpoint != null && endpoint.contains("/api/v1/audit")) {
            return pjp.proceed();
        }

        // ── Usuario autenticado de forma plana y segura ─────────────────────
        Long   usuarioId = null;
        String email     = "anónimo";
        String rol       = "N/A";

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                email = auth.getName(); // 👈 Extrae el string plano directo del token sin tocar Hibernate
                
                // Solo si el principal es la entidad y no un proxy rebelde extraemos de forma segura
                Object principal = auth.getPrincipal();
                if (principal instanceof UsuarioSaas u) {
                    usuarioId = u.getId();
                    // Usamos un bloque defensivo para el Enum por si fuera un proxy perezoso
                    try {
                        rol = u.getRol() != null ? u.getRol().name() : "N/A";
                    } catch (Exception e) {
                        rol = "AUTENTICADO";
                    }
                }
            }
        } catch (Exception ignored) {}

        // ── Ejecución y registro ───────────────────────────────────────────
        String desc = auditable.descripcion().isBlank()
                ? nombreMetodoActual
                : auditable.descripcion();

        final Long finalUsuarioId = usuarioId;
        final String finalEmail = email;
        final String finalRol = rol;
        final String finalEndpoint = endpoint;
        final String finalMetodo = metodo;
        final String finalIp = ip;

        try {
            Object resultado = pjp.proceed();
            auditLogService.registrar(
                    finalUsuarioId, finalEmail, finalRol,
                    auditable.accion(), desc, auditable.entidad(),
                    finalEndpoint, finalMetodo, finalIp,
                    true, null
            );
            return resultado;
        } catch (Throwable ex) {
            auditLogService.registrar(
                    finalUsuarioId, finalEmail, finalRol,
                    auditable.accion(), desc, auditable.entidad(),
                    finalEndpoint, finalMetodo, finalIp,
                    false, ex.getMessage()
            );
            throw ex;
        }
    }

    private String resolverIp(HttpServletRequest req) {
        String ip = req.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = req.getRemoteAddr();
        return ip.contains(",") ? ip.split(",")[0].trim() : ip;
    }
}