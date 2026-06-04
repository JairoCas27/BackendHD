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
    
    // MEJORA : ThreadLocal para control antibucle robusto
    private static final ThreadLocal<Boolean> EN_AUDITORIA = ThreadLocal.withInitial(() -> Boolean.FALSE);

    @Around("@annotation(auditable)")
    public Object interceptar(ProceedingJoinPoint pjp, AuditableAction auditable) throws Throwable {
        // Si ya estamos en contexto de auditoría, bypass inmediato
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
                
                // MEJORA : Verificación precisa con startsWith
                if (endpoint.startsWith("/api/v1/audit")) {
                    log.trace("Excluyendo ruta de auditoría del propio registro: {}", endpoint);
                    return pjp.proceed();
                }
            } else {
                // MEJORA : Logging en lugar de supresión silenciosa
                log.debug("RequestContextHolder sin contexto HTTP activo. Posible ejecución asíncrona o programada.");
            }
        } catch (ClassCastException e) {
            // MEJORA : Logging de advertencia
            log.warn("No se pudo extraer contexto HTTP. Tipo de atributos incompatible: {}", e.getMessage());
        } catch (Exception e) {
            // MEJORA : Logging de error
            log.error("Error inesperado al extraer contexto HTTP", e);
        }

        // ── Usuario autenticado ────────────────────────────────────────────
        Long   usuarioId = null;
        String email     = "anónimo";
        String rol       = "N/A";

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                // Evitar procesar si es anonymousUser
                if (auth.getPrincipal() instanceof String principalStr) {
                    email = principalStr;
                    log.trace("Usuario autenticado como String: {}", email);
                } else {
                    email = auth.getName();
                    
                    Object principal = auth.getPrincipal();
                    if (principal instanceof UsuarioSaas u) {
                        usuarioId = u.getId();
                        // Bloque defensivo para el Enum
                        try {
                            rol = u.getRol() != null ? u.getRol().name() : "N/A";
                        } catch (Exception e) {
                            rol = "AUTENTICADO";
                            log.trace("Error al obtener rol del usuario (posible proxy perezoso): {}", e.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            // MEJORA : Logging en lugar de supresión
            log.warn("Error al extraer contexto de seguridad: {}", e.getMessage());
        }

        // ── Preparación de variables ───────────────────────────────────────
        String desc = auditable.descripcion().isBlank()
                ? pjp.getSignature().getName()
                : auditable.descripcion();

        // ── Ejecución y registro ───────────────────────────────────────────
        try {
            // MEJORA : Marcar que estamos en contexto de auditoría
            EN_AUDITORIA.set(Boolean.TRUE);
            
            Object resultado = pjp.proceed();
            
            // Registro exitoso
            auditLogService.registrar(
                    usuarioId, email, rol,
                    auditable.accion(), desc, auditable.entidad(),
                    endpoint, metodo, ip,
                    true, null
            );
            
            return resultado;
        } catch (Throwable ex) {
            // Registro fallido
            auditLogService.registrar(
                    usuarioId, email, rol,
                    auditable.accion(), desc, auditable.entidad(),
                    endpoint, metodo, ip,
                    false, ex.getMessage()
            );
            throw ex;
        } finally {
            // MEJORA: Liberación garantizada del flag
            EN_AUDITORIA.set(Boolean.FALSE);
        }
    }

    // MEJORA : Normalización completa de IPs
    private String resolverIp(HttpServletRequest req) {
        String ip = req.getHeader("X-Forwarded-For");
        
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = req.getRemoteAddr();
        }
        
        // Manejo de cadenas con múltiples IPs (cadena de proxies)
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        // Normalización de IPv6 localhost a IPv4
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            ip = "127.0.0.1";
        }
        
        return ip != null ? ip : "desconocida";
    }
}