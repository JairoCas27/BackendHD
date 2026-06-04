// shared/audit/AuditableAction.java
package com.urbanpark.parking.shared.audit;

import com.urbanpark.parking.shared.enums.TipoAccionAudit;
import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuditableAction {
    TipoAccionAudit accion();
    String descripcion() default "";
    String entidad()     default "";
}