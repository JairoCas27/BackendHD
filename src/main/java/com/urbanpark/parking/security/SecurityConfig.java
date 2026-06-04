package com.urbanpark.parking.security;

import com.urbanpark.parking.security.jwt.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomSecurityExceptionHandler securityExceptionHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s ->
                        s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(securityExceptionHandler)
                        .accessDeniedHandler(securityExceptionHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        // Públicos
                        .requestMatchers(
                                "/api/v1/auth/register",
                                "/api/v1/auth/login",
                                "/api/v1/auth/forgot-password",
                                "/api/v1/auth/reset-password",
                                "/api/v1/auth/refresh",
                                "/api/v1/planes",
                                "/api/v1/contacto/seguimiento/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/api-docs/**"
                        ).permitAll()

                        // Auth protegido (requiere estar logueado)
                        .requestMatchers(
                                "/api/v1/auth/me/profile",
                                "/api/v1/auth/me/password",
                                "/api/v1/auth/verify"
                        ).authenticated()

                        // Planes (ADMIN / SUPERADMIN por jerarquía)
                        .requestMatchers(HttpMethod.GET,    "/api/v1/admin/planes").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,    "/api/v1/admin/planes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,   "/api/v1/admin/planes").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/v1/admin/planes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/admin/planes/**").hasRole("ADMIN")

                        // Condominios cliente (CLIENTE / superior)
                        .requestMatchers(HttpMethod.POST, "/api/v1/me/condominios").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.GET,  "/api/v1/me/condominios").hasRole("CLIENTE")

                        // Condominios admin (ADMIN / SUPERADMIN)
                        .requestMatchers("/api/v1/admin/condominios").hasRole("ADMIN")
                        .requestMatchers("/api/v1/admin/condominios/pendientes").hasRole("ADMIN")
                        .requestMatchers("/api/v1/admin/condominios/*/aprobar").hasRole("ADMIN")
                        .requestMatchers("/api/v1/admin/condominios/*/rechazar").hasRole("ADMIN")

                        // Reglas de acceso (CLIENTE / superior)
                        .requestMatchers("/api/v1/me/condominios/*/reglas").hasRole("CLIENTE")
                        .requestMatchers("/api/v1/me/condominios/*/reglas/**").hasRole("CLIENTE")

                        // Contacto admin (ADMIN_CONDOMINIO / SUPERADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/v1/contacto").permitAll()
                        .requestMatchers(HttpMethod.GET,   "/api/v1/contacto").hasAnyRole("ADMIN", "SUPERADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/contacto/*/responder").hasAnyRole("ADMIN", "SUPERADMIN")
                        .requestMatchers(HttpMethod.GET,   "/api/v1/contacto/respondidos").hasAnyRole("ADMIN", "SUPERADMIN")
                        .requestMatchers(HttpMethod.GET,   "/api/v1/contacto/pendientes").hasAnyRole("ADMIN", "SUPERADMIN")

                        // Solicitudes de plan
                        // Cliente
                        .requestMatchers(HttpMethod.POST, "/api/v1/me/solicitud-plan").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.GET,  "/api/v1/me/solicitud-plan").hasRole("CLIENTE")
                        // Admin
                        .requestMatchers(HttpMethod.GET,  "/api/v1/admin/solicitudes-plan").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,  "/api/v1/admin/solicitudes-plan/pendientes").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,  "/api/v1/admin/solicitudes-plan/*/aprobar").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,  "/api/v1/admin/solicitudes-plan/*/rechazar").hasRole("ADMIN")

                        // Titular (CLIENTE / ADMIN)
                        // Cliente (sus propios datos)
                        .requestMatchers(HttpMethod.POST, "/api/v1/me/titular").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.GET,  "/api/v1/me/titular").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.PUT,  "/api/v1/me/titular").hasRole("CLIENTE")
                        // Admin (todos los titulares)
                        .requestMatchers(HttpMethod.GET,  "/api/v1/admin/titulares").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,  "/api/v1/admin/titulares/*").hasRole("ADMIN")

                        // Usuarios internos (ADMIN / SUPERADMIN)
                        .requestMatchers(HttpMethod.POST,   "/api/v1/admin/usuarios").hasRole("SUPERADMIN")
                        .requestMatchers(HttpMethod.GET,    "/api/v1/admin/usuarios").hasRole("SUPERADMIN")
                        .requestMatchers(HttpMethod.GET,    "/api/v1/admin/usuarios/clientes").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,    "/api/v1/admin/usuarios/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/v1/admin/usuarios/*/estado").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/admin/usuarios/*").hasRole("SUPERADMIN")

                        // Reportes
                        .requestMatchers("/api/v1/reportes/estadisticas-globales").hasRole("SUPERADMIN")
                        .requestMatchers("/api/v1/reportes/reporte-detallado").hasRole("SUPERADMIN")
                        .requestMatchers("/api/v1/reportes/estadisticas-titular").hasRole("CLIENTE")
                        .requestMatchers("/api/v1/reportes/estadisticas-clientes").hasRole("ADMIN")
                        .requestMatchers("/api/v1/reportes/top-planes").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withDefaultRolePrefix()
                .role("SUPERADMIN").implies("ADMIN")
                .role("ADMIN").implies("CLIENTE")
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }
}