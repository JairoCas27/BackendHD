package com.urbanpark.parking.domain.saas.plan;

import com.urbanpark.parking.shared.enums.EstadoPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PlanRepository extends JpaRepository<Plan, UUID> {

    List<Plan> findAllByEstado(EstadoPlan estado);

    boolean existsByNombre(String nombre);
}