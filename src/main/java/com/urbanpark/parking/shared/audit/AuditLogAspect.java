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
        String endpoint   = "";
        String metodo     = "";
        String ip         = "desconocida";

        // Extraer contexto HTTP si está disponible
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest req = attrs.getRequest();
                endpoint = req.getRequestURI();
                metodo   = req.getMethod();
                ip       = obtenerIp(req);
            }
        } catch (Exception ignored) {}

        // Extraer usuario autenticado
        Long   usuarioId    = null;
        String email        = "anónimo";
        String rol          = "N/A";

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof UsuarioSaas usuario) {
                usuarioId = usuario.getId();
                email     = usuario.getEmail();
                rol       = usuario.getRol().name();
            }
        } catch (Exception ignored) {}

        // Ejecutar el método original
        try {
            Object resultado = pjp.proceed();
            auditLogService.registrar(
                    usuarioId, email, rol,
                    auditable.accion(),
                    auditable.descripcion().isBlank() ? pjp.getSignature().getName() : auditable.descripcion(),
                    auditable.entidad(),
                    endpoint, metodo, ip,
                    true, null
            );
            return resultado;
        } catch (Throwable ex) {
            auditLogService.registrar(
                    usuarioId, email, rol,
                    auditable.accion(),
                    auditable.descripcion().isBlank() ? pjp.getSignature().getName() : auditable.descripcion(),
                    auditable.entidad(),
                    endpoint, metodo, ip,
                    false, ex.getMessage()
            );
            throw ex;
        }
    }

    private String obtenerIp(HttpServletRequest req) {
        String ip = req.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = req.getRemoteAddr();
        return ip.contains(",") ? ip.split(",")[0].trim() : ip;
    }
}