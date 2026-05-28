package com.urbanpark.parking.repository;

import com.urbanpark.parking.enums.EstadoMulta;
import com.urbanpark.parking.model.Multa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MultaRepository extends JpaRepository<Multa, Long> {

    List<Multa> findByApartamentoId(Integer apartamentoId);

    List<Multa> findByCondominioId(Integer condominioId);

    List<Multa> findByEstado(EstadoMulta estado);

    List<Multa> findByCondominioIdAndEstado(Integer condominioId, EstadoMulta estado);

    List<Multa> findByTimestampBetween(LocalDateTime inicio, LocalDateTime fin);
}