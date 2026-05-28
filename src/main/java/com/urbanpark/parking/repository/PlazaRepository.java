package com.urbanpark.parking.repository;

import com.urbanpark.parking.enums.TipoPlaza;
import com.urbanpark.parking.model.Plaza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlazaRepository extends JpaRepository<Plaza, Long> {

    List<Plaza> findByCondominioId(Integer condominioId);

    List<Plaza> findByCondominioIdAndOcupada(Integer condominioId, boolean ocupada);

    List<Plaza> findByCondominioIdAndActiva(Integer condominioId, boolean activa);

    Optional<Plaza> findFirstByCondominioIdAndOcupadaFalseAndActivaTrueAndTipo(
            Integer condominioId, TipoPlaza tipo
    );
}