package com.urbanpark.parking.domain.saas.user;

import com.urbanpark.parking.shared.enums.RolSaas;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SaasUserRepository extends JpaRepository<SaasUser, UUID> {

    Optional<SaasUser> findByEmail(String email);

    boolean existsByEmail(String email);

    List<SaasUser> findAllByRol(RolSaas rol);

    List<SaasUser> findAllByActivo(boolean activo);
}