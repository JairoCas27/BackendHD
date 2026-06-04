package com.urbanpark.parking.security.jwt;

import com.urbanpark.parking.domain.usuarios.UsuarioSaas;
import com.urbanpark.parking.shared.dto.ApiResponse;
import com.urbanpark.parking.shared.enums.RolSaas;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Endpoints públicos de autenticación")
@RequestMapping("/api/v1/auth")
public class JwtAuthController {

    private final JwtService jwtService;

    @PostMapping("/verify")
    @Operation(
            summary = "Verificar validez de token JWT",
            description = "Verifica si un token JWT es válido: firma correcta, no expirado y estructura válida. " +
                    "Devuelve información del usuario si el token es válido."
    )
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Missing or invalid Authorization header"));
        }

        String token = authHeader.substring(7);

        try {
            boolean isValid = jwtService.isTokenValid(token);

            if (isValid) {
                Map<String, Object> data = new HashMap<>();
                data.put("valid", true);
                data.put("email", jwtService.extractEmail(token));
                return ResponseEntity.ok(ApiResponse.success("Token is valid", data));
            } else {
                Map<String, Object> data = new HashMap<>();
                data.put("valid", false);
                return ResponseEntity.ok(ApiResponse.success("Token is invalid or expired", data));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("Invalid token: " + e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Refrescar token JWT expirado",
            description = "Genera un nuevo token JWT a partir de un token expirado. " +
                    "Extrae los claims del token anterior y crea uno nuevo con la misma información del usuario."
    )
    public ResponseEntity<ApiResponse<Map<String, Object>>> refreshToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Missing or invalid Authorization header"));
        }

        String expiredToken = authHeader.substring(7);

        try {
            String email = extractEmailIgnoreExpiration(expiredToken);
            String rol = extractClaimIgnoreExpiration(expiredToken, "rol");
            String nombres = extractClaimIgnoreExpiration(expiredToken, "nombres");
            String apellidos = extractClaimIgnoreExpiration(expiredToken, "apellidos");

            if (email == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Cannot extract user from token"));
            }

            UsuarioSaas usuario = UsuarioSaas.builder()
                    .email(email)
                    .rol(rol != null ? RolSaas.valueOf(rol) : RolSaas.CLIENTE)
                    .nombres(nombres != null ? nombres : "")
                    .apellidos(apellidos != null ? apellidos : "")
                    .build();

            String newToken = jwtService.generateToken(usuario);

            Map<String, Object> data = new HashMap<>();
            data.put("accessToken", newToken);
            data.put("tokenType", "Bearer");

            return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", data));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Failed to refresh token: " + e.getMessage()));
        }
    }

    private String extractEmailIgnoreExpiration(String token) {
        try {
            return jwtService.extractEmail(token);
        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    private String extractClaimIgnoreExpiration(String token, String claimName) {
        try {
            return jwtService.extractClaims(token).get(claimName, String.class);
        } catch (ExpiredJwtException e) {
            return e.getClaims().get(claimName, String.class);
        } catch (Exception e) {
            return null;
        }
    }
}