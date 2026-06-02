package com.urbanpark.parking.domain.users.usuario;

import com.urbanpark.parking.shared.enums.RolParking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioCondominioRepository extends JpaRepository<UsuarioCondominio, UUID> {

    Optional<UsuarioCondominio> findByExternalIdAndTenantId(String externalId, UUID tenantId);

    List<UsuarioCondominio> findAllByTenantId(UUID tenantId);

    List<UsuarioCondominio> findAllByTenantIdAndRolParking(UUID tenantId, RolParking rol);

    boolean existsByEmailAndTenantId(String email, UUID tenantId);
}