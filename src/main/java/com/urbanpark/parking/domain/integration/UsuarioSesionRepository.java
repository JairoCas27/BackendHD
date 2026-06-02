package com.urbanpark.parking.domain.integration;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UsuarioSesionRepository extends JpaRepository<UsuarioSesion, UUID> {

    List<UsuarioSesion> findAllByCondominioId(UUID condominioId);

    List<UsuarioSesion> findAllByExternalUserIdAndCondominioId(
            Long externalUserId, UUID condominioId);
}