package com.urbanpark.parking.modules.usuario;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.urbanpark.parking.modules.usuario.dto.LoginResponseDto;
import com.urbanpark.parking.security.jwt.JwtTokenProvider;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtTokenProvider tokenProvider;

    public AuthController(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/mock-login")
    public ResponseEntity<LoginResponseDto> mockLogin(@RequestBody Map<String, String> request) {
        String tenantId = request.getOrDefault("tenantId", "condo-torres-altair");
        String externalId = request.getOrDefault("externalId", "ext-user-999");
        String nombre = request.getOrDefault("nombre", "Nick Dev");
        String rolSolicitado = request.getOrDefault("rol", "PROPIETARIO"); 
        
        // ID interno simulado del SaaS
        String idInternoMapeado = "saas-user-uuid-55555";

        // Generamos el token firmado usando el proveedor que está en security
        String jwtInterno = tokenProvider.generarTokenInterno(tenantId, idInternoMapeado, externalId, rolSolicitado);

        LoginResponseDto response = new LoginResponseDto(
                jwtInterno,
                "Bearer",
                nombre,
                rolSolicitado,
                tenantId
        );

        return ResponseEntity.ok(response);
    }
}