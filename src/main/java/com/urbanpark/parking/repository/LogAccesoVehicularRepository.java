package com.urbanpark.parking.repository;

import com.urbanpark.parking.model.LogAccesoVehicular;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LogAccesoVehicularRepository extends JpaRepository<LogAccesoVehicular, Long> {

    List<LogAccesoVehicular> findByPlaca(String placa);

    List<LogAccesoVehicular> findByApartamentoId(Integer apartamentoId);

    List<LogAccesoVehicular> findByCondominioId(Integer condominioId);

    List<LogAccesoVehicular> findByTimestampEntradaBetween(
            LocalDateTime inicio, LocalDateTime fin
    );

    Optional<LogAccesoVehicular> findByPlacaAndTimestampSalidaIsNull(String placa);

    List<LogAccesoVehicular> findByCondominioIdAndTimestampSalidaIsNull(Integer condominioId);
}