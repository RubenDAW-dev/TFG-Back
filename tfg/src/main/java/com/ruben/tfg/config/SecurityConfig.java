package com.ruben.tfg.config;

import com.ruben.tfg.utilities.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain security(HttpSecurity http) throws Exception {

        return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth

                // Swagger público
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll()

                // Auth público
                .requestMatchers(
                    "/api/auth/login",
                    "/api/auth/forgot-password",
                    "/api/auth/reset-password",
                    "/api/auth/verify-reset-token"
                ).permitAll()

                // Registro de usuario → público
                .requestMatchers("/api/usuarios/create").permitAll()

                // Usuarios → privado
                .requestMatchers("/api/usuarios/**").authenticated()

                // Dashboard → público
                .requestMatchers(
                    "/api/team-season-stats/table",
                    "/api/player-season-stats/top-scorers",
                    "/api/player-season-stats/top-assists",
                    "/api/matches/last",
                    "/api/matches/next"
                ).permitAll()

                // Todo lo demás → autenticado
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}