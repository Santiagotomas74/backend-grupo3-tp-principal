package com.blackmesaresearch.hytrac.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // This enables @PreAuthorize in your controllers
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 1. MUST allow access to the login page and static assets
                        .requestMatchers("/login", "/css/**", "/js/**").permitAll()
                        // 2. Everything else remains locked
                        .anyRequest().authenticated())/* 
                        
                        ESTO CUANDO HAYA PANTALLA DE LOGIN BIEN TIENE QUE VOLVER A HABILITARSE. POR AHORA TIRA UN POP UP DE CHROME

                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/perform_login")
                        .defaultSuccessUrl("/api/ordenes/get", true) // Redirect here after login
                        .permitAll() // 3. Another way to ensure login endpoints are open
                    
                )*/
                .httpBasic(Customizer.withDefaults()); // Keep this for Postman testing

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}