package com.urbanpark.parking.domain.audit;

import com.urbanpark.parking.shared.enums.TipoAccionAudit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long>, 
                                            JpaSpecificationExecutor<AuditLog> {

    Page<AuditLog> findAllByOrderByFechaHoraDesc(Pageable pageable);


    // MEJORA : Método para eliminar logs antiguos
    @Transactional
    @Modifying
    @Query("DELETE FROM AuditLog a WHERE a.fechaHora < :fechaCorte")
    int deleteByFechaHoraBefore(@Param("fechaCorte") LocalDateTime fechaCorte);
}