package com.urbanpark.parking.domain.tenant;

import com.urbanpark.parking.shared.enums.EstadoCondominio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CondominioRepository extends JpaRepository<Condominio, UUID> {
    Optional<Condominio> findByTitularEmail(String email);
    List<Condominio> findAllByEstado(EstadoCondominio estado);
    boolean existsByTitularEmail(String email);
}