package com.urbanpark.parking.domain.auth;

import com.urbanpark.parking.domain.auth.dto.*;
import com.urbanpark.parking.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final SaasAuthService saasAuthService;

    // Login usuarios del condominio (ADMIN_CONDOMINIO, PROPIETARIO, AGENTE_SEGURIDAD)
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @RequestBody @Valid ExternalLoginRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Login exitoso", authService.login(request)));
    }

    // Login usuarios internos del SaaS (SUPERADMIN, ADMIN)
    @PostMapping("/saas/login")
    public ResponseEntity<ApiResponse<SaasAuthResponse>> saasLogin(
            @RequestBody @Valid SaasLoginRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Login exitoso", saasAuthService.login(request)));
    }
}