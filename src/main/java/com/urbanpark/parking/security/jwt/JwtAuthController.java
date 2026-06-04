package com.urbanpark.parking.security.jwt;

import com.urbanpark.parking.domain.usuarios.UsuarioSaas;
import com.urbanpark.parking.domain.usuarios.UsuarioSaasRepository;
import com.urbanpark.parking.shared.dto.ApiResponse;
import com.urbanpark.parking.shared.exceptions.ResourceNotFoundException;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@Tag(name = "Auth", description = "Endpoints de autenticación")
@RequestMapping("/api/v1/auth")
public class JwtAuthController {

    private final JwtService jwtService;
    private final UsuarioSaasRepository usuarioSaasRepository;

    @PostMapping("/verify")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Verificar token JWT",
            description = "Verifica si el token es válido, no está expirado y pertenece a un usuario activo en la BD."
    )
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyToken(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null)
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Token no encontrado en el header Authorization"));

        try {
            String email = jwtService.extractEmail(token);
            UsuarioSaas usuario = usuarioSaasRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

            Map<String, Object> data = new HashMap<>();
            data.put("valid", true);
            data.put("email", usuario.getEmail());
            data.put("rol", usuario.getRol());
            data.put("estado", usuario.getEstado());
            data.put("nombreCompleto", usuario.getNombreCompleto());

            return ResponseEntity.ok(ApiResponse.success("Token válido", data));

        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Token expirado"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Token inválido"));
        }
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Refrescar token JWT",
            description = "Genera un nuevo accessToken a partir del refreshToken. Envía el refreshToken en el header Authorization."
    )
    public ResponseEntity<ApiResponse<Map<String, Object>>> refreshToken(HttpServletRequest request) {
        String refreshToken = extractToken(request);
        if (refreshToken == null)
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Token no encontrado en el header Authorization"));

        try {
            String email = extractEmailIgnoreExpiration(refreshToken);

            if (email == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Token inválido"));

            UsuarioSaas usuario = usuarioSaasRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

            Map<String, Object> data = new HashMap<>();
            data.put("token", jwtService.generateToken(usuario));
            data.put("refreshToken", jwtService.generateRefreshToken(usuario));

            return ResponseEntity.ok(ApiResponse.success("Token renovado exitosamente", data));

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Usuario no encontrado"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Token inválido"));
        }
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        return authHeader.substring(7);
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
}