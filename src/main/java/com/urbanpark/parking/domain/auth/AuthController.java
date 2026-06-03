package com.urbanpark.parking.domain.auth;

import com.urbanpark.parking.domain.auth.dto.*;
import com.urbanpark.parking.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Endpoints públicos de autenticación")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Registro de nuevo cliente")
    public ResponseEntity<ApiResponse<Void>> register(
            @Valid @RequestBody RegisterRequest request) {

        authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Cuenta creada exitosamente", null));
    }

    @PostMapping("/login")
    @Operation(summary = "Inicio de sesión")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login exitoso", response));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Solicitar OTP para restablecer contraseña")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        authService.forgotPassword(request);
        return ResponseEntity.ok(
                ApiResponse.success("Se envió un código OTP a tu correo", null));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Restablecer contraseña con OTP")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        authService.resetPassword(request);
        return ResponseEntity.ok(
                ApiResponse.success("Contraseña actualizada exitosamente", null));
    }
}