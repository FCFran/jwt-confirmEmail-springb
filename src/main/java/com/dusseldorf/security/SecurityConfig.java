package com.dusseldorf.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity //Habilitar web security
@EnableMethodSecurity(securedEnabled = true) // seguridad desde los metodos
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults()) //personalizar con un valor predeterminado
                .csrf(AbstractHttpConfigurer::disable) //desabilitar
                .authorizeHttpRequests(req ->
                            req.requestMatchers(
                                    "/auth/**",
                                    "/v2/api-docs",
                                    "/v3/api-docs",
                                    "/v3/api-docs/**",
                                    "/swagger-resources",
                                    "/swagger-resources/**",
                                    "/configuration/ui",
                                    "/configuration/security",
                                    "swagger-ui/**",
                                    "webjars/**",
                                    "swagger-ui.html"
                            ).permitAll()
                                    .anyRequest()
                                    .authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // que el contexto no se encargue de la seguridad
                .authenticationProvider(authenticationProvider) // proveedor de authenticacion
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // añadiremos nuestro propios filtros
        return http.build();
    }
}
