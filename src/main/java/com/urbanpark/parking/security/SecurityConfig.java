package com.urbanpark.parking.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    // ─── Rutas completamente publicas ────────────────────────────────
    private static final String[] PUBLIC_ROUTES = {
            "/api/v1/auth/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**"
    };

    // ─── Solo SUPERADMIN ──────────────────────────────────────────────
    private static final String[] SUPERADMIN_ONLY_ROUTES = {
            "/api/v1/saas/usuarios/**",
            "/api/v1/audit/saas"
    };

    // ─── SUPERADMIN o ADMIN ───────────────────────────────────────────
    private static final String[] ADMIN_ROUTES = {
            "/api/v1/condominios/**",
            "/api/v1/planes/todos",
            "/api/v1/audit/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // Sin JWT
                        .requestMatchers(PUBLIC_ROUTES).permitAll()

                        // GET /api/v1/planes → publico
                        .requestMatchers(HttpMethod.GET, "/api/v1/planes").permitAll()

                        // Escritura en planes → SUPERADMIN o ADMIN
                        .requestMatchers(HttpMethod.POST,   "/api/v1/planes").hasAnyRole("SUPERADMIN", "ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/v1/planes/**").hasAnyRole("SUPERADMIN", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH,  "/api/v1/planes/**").hasAnyRole("SUPERADMIN", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/planes/**").hasAnyRole("SUPERADMIN", "ADMIN")

                        // Solo SUPERADMIN
                        .requestMatchers(SUPERADMIN_ONLY_ROUTES).hasRole("SUPERADMIN")

                        // SUPERADMIN o ADMIN
                        .requestMatchers(ADMIN_ROUTES).hasAnyRole("SUPERADMIN", "ADMIN")

                        // Cualquier otra ruta requiere autenticacion
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}