package com.blackmesaresearch.hytrac.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final SecurityEntryPoint securityEntryPoint;
    private final AccessDeniedHandlerImpl accessDeniedHandler;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter,
                            SecurityEntryPoint securityEntryPoint,
                            AccessDeniedHandlerImpl accessDeniedHandler
    ) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.securityEntryPoint = securityEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                //Ataja las excepciones
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(securityEntryPoint)   
                        .accessDeniedHandler(accessDeniedHandler)       
                )
                
                .authorizeHttpRequests(auth -> auth
                        
                    // Publico
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/api-docs/**").permitAll()

                    // Solo admin
                    .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                    .requestMatchers("/api/transportistas/alta").hasAuthority("ADMIN")

                    // Supervisor
                    .requestMatchers("/api/supervisor/**").hasAuthority("SUPERVISOR")
                
                    // Jefe de ESTACION
                    .requestMatchers("/api/jefe-estacion/**").hasAuthority("JEFE_ESTACION")

                    // Transportista 
                    .requestMatchers("/api/transportista/**").hasAuthority("TRANSPORTISTA")

                    // Operador 
                    .requestMatchers(HttpMethod.POST, "/api/ordenes/crear").hasAuthority("OPERADOR")
                    .requestMatchers(HttpMethod.PUT, "/api/ordenes/*/editar").hasAuthority("OPERADOR")


                    .requestMatchers(HttpMethod.GET, "/api/ordenes/**").authenticated()

                    .anyRequest().authenticated()
                )

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration config) throws Exception {
            return config.getAuthenticationManager();
        }
}