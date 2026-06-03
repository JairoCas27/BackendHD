package com.urbanpark.parking.security.otp;

import com.urbanpark.parking.domain.usuarios.UsuarioSaas;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {

    Optional<OtpToken> findTopByUsuarioAndUsadoFalseOrderByFechaCreacionDesc(UsuarioSaas usuario);
}