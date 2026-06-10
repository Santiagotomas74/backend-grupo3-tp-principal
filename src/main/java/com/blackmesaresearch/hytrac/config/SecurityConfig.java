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
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfiguration = new org.springframework.web.cors.CorsConfiguration();
                    corsConfiguration.setAllowedOrigins(java.util.List.of("*")); // Permitir acceso desde cualquier origen
                    corsConfiguration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corsConfiguration.setAllowedHeaders(java.util.List.of("*"));
                    corsConfiguration.setAllowCredentials(false);
                    return corsConfiguration;
                }))
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

                    // Swagger
                    .requestMatchers("/api/swagger-ui/**", "/api/v3/api-docs/**", "/api/api-docs/**","/swagger-ui/**","/v3/api-docs/**").permitAll()

                    //Especificos por ahora
                    .requestMatchers(HttpMethod.GET, "/api/supervisor/**").hasAnyAuthority("SUPERVISOR", "OPERADOR", "ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/transportistas/**").hasAnyAuthority("SUPERVISOR", "OPERADOR", "ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/transportista/**").hasAnyAuthority("SUPERVISOR", "OPERADOR", "ADMIN")

                    // Solo admin
                    .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                    .requestMatchers("/api/transportistas/alta").hasAuthority("ADMIN")     
                    // Jefe de ESTACION
                    .requestMatchers("/api/jefe-estacion/**").hasAuthority("JEFE_ESTACION")

                     // Operador Acciones especificas
                    .requestMatchers(HttpMethod.POST, "/api/ordenes/crear").hasAuthority("OPERADOR")
                    .requestMatchers(HttpMethod.PUT, "/api/ordenes/*/editar").hasAuthority("OPERADOR")
                    .requestMatchers(HttpMethod.POST, "/api/transportistas/seleccionar-optimos").hasAuthority("OPERADOR")
                    .requestMatchers(HttpMethod.POST, "/api/transportistas/incidencia").hasAnyAuthority("OPERADOR", "SUPERVISOR", "ADMIN")

                    // Supervisor
                    .requestMatchers("/api/supervisor/**").hasAuthority("SUPERVISOR")    
                    // Transportista 
                    .requestMatchers("/api/transportista/**").hasAuthority("TRANSPORTISTA")

                    //Autenticados
                    .requestMatchers(HttpMethod.GET, "/api/ordenes/**").authenticated()
                    .requestMatchers("/api/notificaciones/**").authenticated()

                    //por ahora para el uso del sistema facil
                    .requestMatchers("/api/vehiculos/**").authenticated()
                    .requestMatchers("/api/supervisor/incidencias").authenticated()
                    .requestMatchers("/api/stats/**").authenticated()
                    .requestMatchers("/api/rutas/**").authenticated()
                    .requestMatchers("/api/reportes/**").authenticated()
                    .requestMatchers("/api/provincias/get").authenticated()
                    .requestMatchers("/api/ouch").authenticated()
                    .requestMatchers("/api/lugares-operativos/**").authenticated()
                    .requestMatchers("/api/entidades/**").authenticated()
                    .requestMatchers("/api/combustibles").authenticated()
                    .requestMatchers("/api/bcra/**").authenticated()
                    .requestMatchers("/api/auditoria/**").authenticated()

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