package com.urbanpark.parking.domain.planes;

import com.urbanpark.parking.shared.enums.EstadoPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanRepository extends JpaRepository<Plan, Long> {

    List<Plan> findAllByEstado(EstadoPlan estado);

    boolean existsByNombre(String nombre);
}