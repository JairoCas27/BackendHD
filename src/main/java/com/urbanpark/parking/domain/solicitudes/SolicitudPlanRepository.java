package com.urbanpark.parking.domain.solicitudes;

import com.urbanpark.parking.domain.titulares.Titular;
import com.urbanpark.parking.shared.enums.EstadoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SolicitudPlanRepository extends JpaRepository<SolicitudPlan, Long> {

    List<SolicitudPlan> findAllByEstado(EstadoSolicitud estado);

    Optional<SolicitudPlan> findTopByTitularOrderByFechaSolicitudDesc(Titular titular);

    boolean existsByTitularAndEstado(Titular titular, EstadoSolicitud estado);
}