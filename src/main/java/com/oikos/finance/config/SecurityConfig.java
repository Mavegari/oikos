package com.oikos.finance.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Desactivamos CSRF: es una API REST stateless, no usa sesiones con cookies
            .csrf(csrf -> csrf.disable())

            // Sin sesiones: cada petición se autentica sola (con JWT, más adelante)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Reglas de acceso
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas: registro y login
                .requestMatchers("/api/auth/**").permitAll()
                // Todo lo demás requiere autenticación
                .anyRequest().authenticated()
            );

        return http.build();
    }
}