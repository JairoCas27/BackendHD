package com.urbanpark.parking.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilitamos CSRF ya que utilizaremos JWT (Tokens sin estado)
            .csrf(AbstractHttpConfigurer::disable)
            
            // Forzamos a que la sesión no guarde estado en el servidor (Stateless)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Reglas de autorización de rutas
            .authorizeHttpRequests(auth -> auth
                // Permitir libre acceso a Swagger / OpenAPI para documentar y probar
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                // Permitir libre acceso a los endpoints de autenticación que haremos después
                .requestMatchers("/api/auth/**").permitAll()
                // Cualquier otra petición requerirá estar autenticado internamente
                .anyRequest().authenticated()
            );

        return http.build();
    }
}