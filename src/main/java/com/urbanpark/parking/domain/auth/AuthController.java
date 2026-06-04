package com.urbanpark.parking.domain.auth;

import com.urbanpark.parking.domain.auth.dto.*;
import com.urbanpark.parking.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Endpoints de autenticación")
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
    @Operation(summary = "Inicio de sesión — retorna token y refreshToken")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(ApiResponse.success("Login exitoso", authService.login(request)));
    }

    @GetMapping("/me/profile")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener perfil del usuario autenticado")
    public ResponseEntity<ApiResponse<ProfileResponse>> profile() {

        return ResponseEntity.ok(ApiResponse.success(authService.getProfile()));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Solicitar OTP para restablecer contraseña")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        authService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Se envió un código OTP a tu correo", null));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Restablecer contraseña con OTP")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Contraseña actualizada exitosamente", null));
    }

    @PutMapping("/me/password")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Cambiar contraseña del usuario autenticado")
    public ResponseEntity<ApiResponse<Void>> cambiarPassword(
            @Valid @RequestBody ChangePasswordRequest request) {

        authService.cambiarPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Contraseña actualizada exitosamente", null));
    }
}