package com.urbanpark.parking.domain.audit;

import com.urbanpark.parking.shared.enums.TipoAccionAudit;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDateTime;

public class AuditLogSpecifications {

    public static Specification<AuditLog> conUsuarioId(Long usuarioId) {
        return (root, query, cb) -> 
            usuarioId == null ? cb.conjunction() : cb.equal(root.get("usuarioSaasId"), usuarioId);
    }

    public static Specification<AuditLog> conAccion(TipoAccionAudit accion) {
        return (root, query, cb) -> 
            accion == null ? cb.conjunction() : cb.equal(root.get("accion"), accion);
    }

    public static Specification<AuditLog> conExitoso(Boolean exitoso) {
        return (root, query, cb) -> 
            exitoso == null ? cb.conjunction() : cb.equal(root.get("exitoso"), exitoso);
    }

    public static Specification<AuditLog> desdeFecha(LocalDateTime desde) {
        return (root, query, cb) -> 
            desde == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get("fechaHora"), desde);
    }

    public static Specification<AuditLog> hastaFecha(LocalDateTime hasta) {
        return (root, query, cb) -> 
            hasta == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("fechaHora"), hasta);
    }
}