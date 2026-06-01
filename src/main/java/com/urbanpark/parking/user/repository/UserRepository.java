package com.urbanpark.parking.user.repository;
 
import com.urbanpark.parking.user.domain.enums.UserRole;
import com.urbanpark.parking.user.domain.enums.UserStatus;
import com.urbanpark.parking.user.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import java.util.List;
import java.util.Optional;
 
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
 
    Optional<User> findByExternalIdAndTenantId(String externalId, String tenantId);
 
    Optional<User> findByIdAndTenantId(Long id, String tenantId);
 
    List<User> findAllByTenantId(String tenantId);
 
    List<User> findAllByTenantIdAndRole(String tenantId, UserRole role);
 
    List<User> findAllByTenantIdAndStatus(String tenantId, UserStatus status);
 
    boolean existsByExternalIdAndTenantId(String externalId, String tenantId);
 
    List<User> findAllByTenantIdAndApartmentNumber(String tenantId, String apartmentNumber);
}