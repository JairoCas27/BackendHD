package com.urbanpark.parking.visitor.repository;
 
import com.urbanpark.parking.user.domain.model.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
 
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
 
@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {
 
    List<Visitor> findAllByAuthorizedByIdAndTenantId(Long userId, String tenantId);
 
    List<Visitor> findAllByTenantId(String tenantId);
 
    Optional<Visitor> findByIdAndTenantId(Long id, String tenantId);
 
    /**
     * Busca visitantes activos cuya placa coincida y estén dentro del período de validez.
     * Usado por el módulo de control de acceso para validar el ingreso.
     */
    @Query("""
        SELECT v FROM Visitor v
        WHERE v.tenantId = :tenantId
          AND v.vehiclePlate = :plate
          AND v.isActive = true
          AND v.validFrom <= :now
          AND v.validUntil >= :now
        """)
    List<Visitor> findActiveVisitorsByPlate(
        @Param("tenantId") String tenantId,
        @Param("plate") String plate,
        @Param("now") LocalDateTime now
    );
 
    /**
     * Todos los visitantes actualmente vigentes de un usuario.
     */
    @Query("""
        SELECT v FROM Visitor v
        WHERE v.authorizedBy.id = :userId
          AND v.tenantId = :tenantId
          AND v.isActive = true
          AND v.validUntil >= :now
        """)
    List<Visitor> findActiveByUser(
        @Param("userId") Long userId,
        @Param("tenantId") String tenantId,
        @Param("now") LocalDateTime now
    );
}