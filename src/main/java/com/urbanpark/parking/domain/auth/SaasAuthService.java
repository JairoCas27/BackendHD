package com.urbanpark.parking.domain.auth;

import com.urbanpark.parking.domain.auth.dto.SaasAuthResponse;
import com.urbanpark.parking.domain.auth.dto.SaasLoginRequest;
import com.urbanpark.parking.domain.saas.user.SaasUser;
import com.urbanpark.parking.domain.saas.user.SaasUserRepository;
import com.urbanpark.parking.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SaasAuthService {

    private final SaasUserRepository saasUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public SaasAuthResponse login(SaasLoginRequest request) {
        SaasUser user = saasUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));

        if (!user.isActivo()) {
            throw new IllegalStateException("Usuario desactivado");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        String accessToken = jwtService.generateSaasToken(
                user.getId(),
                user.getRol().name()
        );

        String refreshToken = jwtService.generateRefreshToken(user.getId());

        return SaasAuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .nombre(user.getNombre())
                .rol(user.getRol().name())
                .build();
    }
}