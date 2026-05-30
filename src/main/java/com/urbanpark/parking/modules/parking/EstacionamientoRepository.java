package com.urbanpark.parking.modules.parking;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstacionamientoRepository extends JpaRepository<EstacionamientoEntity, String> {
    
    List<EstacionamientoEntity> findByCondominioId(String condominioId);
    
    Optional<EstacionamientoEntity> findByIdAndCondominioId(String id, String condominioId);

    // Este método contará de forma automática cuántas celdas tiene asignadas un apartamento específico
    long countByApartamentoIdAndCondominioId(String apartamentoId, String condominioId);
}