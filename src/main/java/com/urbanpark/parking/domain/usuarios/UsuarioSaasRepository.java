package com.urbanpark.parking.domain.usuarios;

import com.urbanpark.parking.shared.enums.EstadoUsuarioSaas;
import com.urbanpark.parking.shared.enums.RolSaas;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UsuarioSaasRepository extends JpaRepository<UsuarioSaas, Long> {

    Optional<UsuarioSaas> findByEmail(String email);

    Optional<UsuarioSaas> findByDni(String dni);

    boolean existsByEmail(String email);

    boolean existsByDni(String dni);

    List<UsuarioSaas> findAllByRol(RolSaas rol);

    List<UsuarioSaas> findAllByEstado(EstadoUsuarioSaas estado);

    List<UsuarioSaas> findAllByRolIn(Collection<RolSaas> roles);
}