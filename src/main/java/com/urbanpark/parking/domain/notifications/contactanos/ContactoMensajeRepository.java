package com.urbanpark.parking.domain.notifications.contactanos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactoMensajeRepository extends JpaRepository<ContactoMensaje, Long> {

Optional<ContactoMensaje> findByCodigoSeguimiento(String codigoSeguimiento);
}
