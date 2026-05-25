package com.blackmesaresearch.hytrac.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.blackmesaresearch.hytrac.model.core.Usuario;
import com.blackmesaresearch.hytrac.model.lookup.Rol;
import com.blackmesaresearch.hytrac.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void loadUserByUsername_DebeLanzarExcepcionCuandoEmailNoExiste() {

        when(usuarioRepository.findByEmail("noexiste@mail.com")).thenReturn(Optional.empty());

        UsernameNotFoundException excepcion = assertThrows(UsernameNotFoundException.class, () -> 
            usuarioService.loadUserByUsername("noexiste@mail.com")
        );

        assertEquals("Usuario no encontrado con email: noexiste@mail.com", excepcion.getMessage());
 
    }   

    @Test
    void loadUserByUserName_DebeRetornarUserDetailsCuandoEmailExiste() {
        var rol = new Rol();
        rol.setNombre("OPERADOR");
        rol.setPermisos(java.util.Set.of());

        var usuario = new Usuario();
        usuario.setEmail("moristeenmadrid@mail.com");
        usuario.setPasswordHash("lamasimportante");
        usuario.setActivo(true);
        usuario.setRol(rol);

        when(usuarioRepository.findByEmail("moristeenmadrid@mail.com")).thenReturn(Optional.of(usuario));

        var userDetails = usuarioService.loadUserByUsername("moristeenmadrid@mail.com");

        assertNotNull(userDetails);
        assertEquals("moristeenmadrid@mail.com", userDetails.getUsername());
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void loadUserByUserName_DebeRetornarUsuarioDeshabilitadoCuandoNoEstaActivo() {

        var rol = new Rol();
        rol.setNombre("OPERADOR");
        rol.setPermisos(java.util.Set.of());

        var usuario = new Usuario();
        usuario.setEmail("inactivo@mail.com");
        usuario.setPasswordHash("contraseñainactiva");
        usuario.setActivo(false);
        usuario.setRol(rol);

        when(usuarioRepository.findByEmail("inactivo@mail.com")).thenReturn(Optional.of(usuario));

        var userDetails = usuarioService.loadUserByUsername("inactivo@mail.com");

        assertFalse(userDetails.isEnabled());
    }

    
    
    }
