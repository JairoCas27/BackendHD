package com.urbanpark.parking.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    // Una firma base temporal de 256 bits para las pruebas (Luego irá en el .env)
    private final String JWT_SECRET = "SaaS_Parking_Super_Secret_Key_2026_UrbanPark_Security_Key";
    private final long JWT_EXPIRATION_MS = 86400000; // 24 Horas

    private Key getSigningKey() {
        byte[] keyBytes = this.JWT_SECRET.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // RF-03: Generar el JWT interno con la estructura requerida
    public String generarTokenInterno(String tenantId, String userId, String externalId, String rolParking) {
        Date ahora = new Date();
        Date fechaExpiracion = new Date(ahora.getTime() + JWT_EXPIRATION_MS);

        Map<String, Object> claims = new HashMap<>();
        claims.put("tenant_id", tenantId);
        claims.put("external_id", externalId);
        claims.put("rol", rolParking);
        // Aquí puedes mapear una lista de permisos según el rol en el futuro

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(ahora)
                .setExpiration(fechaExpiracion)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Métodos utilitarios para validar el token que nos envíen en los requests
    public boolean validarToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Claims obtenerClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }
}