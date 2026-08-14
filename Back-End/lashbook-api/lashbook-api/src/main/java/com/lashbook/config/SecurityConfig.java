package com.lashbook.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;

@Configuration
public class SecurityConfig {

        @Value("${CORS_ALLOWED_ORIGINS:http://localhost:5173}")
           private String corsAllowedOrigins;
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        return http
                .csrf(csrf ->
                        csrf.disable()
                )
                .cors(
                        Customizer.withDefaults()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(
                                        HttpMethod.OPTIONS,
                                        "/**"
                                )
                                .permitAll()

                                .requestMatchers(
                                        "/api/auth/**"
                                )
                                .permitAll()

                                .requestMatchers(
                                     "/api/public/**"
                                 )
                                .permitAll()

                                .requestMatchers(
                                      HttpMethod.GET,
                                    "/api/servicios/**",
                                    "/api/busqueda/servicios"
                                )
                                .permitAll()

                                .requestMatchers(
                                        "/actuator/health"
                                )
                                .permitAll()

                                .anyRequest()
                                .authenticated()
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(
                                Customizer.withDefaults()
                        )
                )
                .build();
    }

    @Bean
    public CorsConfigurationSource
            corsConfigurationSource() {

        CorsConfiguration configuracion =
                new CorsConfiguration();

        configuracion.setAllowedOrigins(
                Arrays.stream(
                corsAllowedOrigins.split(",")
        )
        .map(String::trim)
        .filter(origen -> !origen.isEmpty())
        .toList()
        );
                
       

        configuracion.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuracion.setAllowedHeaders(
                List.of(
                        "Authorization",
                        "Content-Type",
                        "Accept"
                )
        );

        configuracion.setExposedHeaders(
                List.of(
                        "Authorization"
                )
        );

        configuracion.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource fuente =
                new UrlBasedCorsConfigurationSource();

        fuente.registerCorsConfiguration(
                "/**",
                configuracion
        );

        return fuente;
    }
}