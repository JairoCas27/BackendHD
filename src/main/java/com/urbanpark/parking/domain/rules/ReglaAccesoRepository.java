package com.urbanpark.parking.domain.rules;

import com.urbanpark.parking.domain.condominios.Condominio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReglaAccesoRepository extends JpaRepository<ReglaAcceso, Long> {

    List<ReglaAcceso> findAllByCondominioIdOrderByIdDesc(Long condominioId);

    List<ReglaAcceso> findAllByCondominioIdAndActivaTrue(Long condominioId);

    Optional<ReglaAcceso> findByIdAndCondominioId(Long id, Long condominioId);

    boolean existsByCondominioIdAndNombreIgnoreCase(Long condominioId, String nombre);

    boolean existsByCondominioIdAndNombreIgnoreCaseAndIdNot(
            Long condominioId, String nombre, Long id);
}
