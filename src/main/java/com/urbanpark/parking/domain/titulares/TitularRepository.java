package com.urbanpark.parking.domain.titulares;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TitularRepository extends JpaRepository<Titular, Long> {

    Optional<Titular> findByUsuarioSaasId(Long usuarioSaasId);

    boolean existsByUsuarioSaasId(Long usuarioSaasId);

    boolean existsByRuc(String ruc);
}