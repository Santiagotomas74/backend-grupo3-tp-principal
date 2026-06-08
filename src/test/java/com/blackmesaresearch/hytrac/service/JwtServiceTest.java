package com.blackmesaresearch.hytrac.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;

import com.blackmesaresearch.hytrac.model.core.Usuario;
import com.blackmesaresearch.hytrac.model.lookup.Rol;

public class JwtServiceTest {

    private JwtService jwtService;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        
        Rol rol = new Rol();
        rol.setNombre("OPERADOR");
        
        usuario = new Usuario();
        usuario.setId(1);
        usuario.setEmail("test@test.com");
        usuario.setNombre("Juan");
        usuario.setApellido("Perez");
        usuario.setLegajo("LEG-001");
        usuario.setRol(rol);
    }

    @Test
    void generarToken_DebeCrearTokenValido() {
        String token = jwtService.generarToken(usuario);
        
        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3); 
    }

    @Test
    void extraerEmail_DebeRetornarElEmailCorrecto() {
        String token = jwtService.generarToken(usuario);
        String email = jwtService.extraerEmail(token);
        
        assertEquals("test@test.com", email);
    }

    @Test
    void tokenValido_DebeRetornarVerdaderoParaUsuarioCorrecto() {
        String token = jwtService.generarToken(usuario);
        var userDetails = User.withUsername("test@test.com")
                              .password("pass")
                              .authorities("ROLE_USER")
                              .build();

        assertTrue(jwtService.tokenValido(token, userDetails));
    }

    @Test
    void tokenValido_DebeRetornarFalsoParaUsuarioDistinto() {
        String token = jwtService.generarToken(usuario);
        var otroUsuario = User.withUsername("otro@test.com")
                              .password("pass")
                              .authorities("ROLE_USER")
                              .build();

        assertFalse(jwtService.tokenValido(token, otroUsuario));
    }
}
