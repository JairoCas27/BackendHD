package com.urbanpark.parking.domain.condominios;

import com.urbanpark.parking.domain.titulares.Titular;
import com.urbanpark.parking.shared.enums.EstadoCondominio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CondominioRepository extends JpaRepository<Condominio, Long> {

    List<Condominio> findAllByTitular(Titular titular);

    List<Condominio> findAllByEstado(EstadoCondominio estado);

    long countByTitularAndEstadoNot(Titular titular, EstadoCondominio estado);

    boolean existsBySlug(String slug);
}