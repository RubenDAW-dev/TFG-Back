package com.ruben.tfg.config;

import com.ruben.tfg.utilities.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain security(HttpSecurity http) throws Exception {

        return http
            .csrf(csrf -> csrf.disable())

            .cors(cors -> cors.configurationSource(request -> {
                CorsConfiguration config = new CorsConfiguration();

                // Permitir frontend local
                config.addAllowedOrigin("http://localhost:4200");

                // Permitir frontend/backend en Azure
                config.addAllowedOriginPattern("https://*.azurewebsites.net");

                // Permitir todo lo necesario
                config.addAllowedHeader("*");
                config.addAllowedMethod("*");
                config.setAllowCredentials(false);

                return config;
            }))

            .authorizeHttpRequests(auth -> auth

                // === Swagger público ===
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll()

                // === Auth público ===
                .requestMatchers(
                    "/api/auth/login",
                    "/api/auth/forgot-password",
                    "/api/auth/reset-password",
                    "/api/auth/verify-reset-token"
                ).permitAll()

                // === Registro público ===
                .requestMatchers("/api/usuarios/create").permitAll()

                // === Dashboard público ===
                .requestMatchers(
                    "/api/team-season-stats/table",
                    "/api/player-season-stats/top-scorers",
                    "/api/player-season-stats/top-assists",
                    "/api/matches/last",
                    "/api/matches/next"
                ).permitAll()

                // === Todo lo demás requiere autenticación ===
                .anyRequest().authenticated()
            )

            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

            .build();
    }
}
