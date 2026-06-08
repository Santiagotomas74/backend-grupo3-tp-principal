package com.blackmesaresearch.hytrac.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.blackmesaresearch.hytrac.dto.request.AltaUsuarioRequestDTO;
import com.blackmesaresearch.hytrac.model.core.Usuario;
import com.blackmesaresearch.hytrac.model.lookup.Rol;
import com.blackmesaresearch.hytrac.repository.RolRepository;
import com.blackmesaresearch.hytrac.repository.UsuarioRepository;


@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private RolRepository rolRepository;
    @Mock private PasswordEncoder passwordEncoder;

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

    @Test
    void registrarNuevoUsuario_DebeGuardarUsuarioExitosamente() {
        var dto = new AltaUsuarioRequestDTO("Juan", "Perez", 111L, "juan@test.com", "pass", "OPERADOR", null);
        
        when(usuarioRepository.existsByEmail("juan@test.com")).thenReturn(false);
        when(usuarioRepository.existsByDni(111L)).thenReturn(false);
        
        Rol rol = new Rol();
        rol.setId(1);
        rol.setNombre("OPERADOR");
        rol.setPermisos(java.util.Collections.emptySet());
        
        when(rolRepository.findByNombre("OPERADOR")).thenReturn(Optional.of(rol));
        when(passwordEncoder.encode("pass")).thenReturn("encodedPass");
        when(usuarioRepository.findTopByLegajoStartingWithOrderByLegajoDesc("LEG")).thenReturn(Optional.empty());

        usuarioService.registrarNuevoUsuario(dto);

        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void registrarNuevoUsuario_DebeLanzarExcepcion_SiEsJefeEstacionSinLugar() {
        var dto = new AltaUsuarioRequestDTO("Juan", "Perez", 111L, "juan@test.com", "pass", "JEFE_ESTACION", null);
        
        when(usuarioRepository.existsByEmail(any())).thenReturn(false);
        
        Rol rol = new Rol();
        rol.setId(1);
        rol.setNombre("JEFE_ESTACION");
        rol.setPermisos(java.util.Collections.emptySet());
        
        when(rolRepository.findByNombre("JEFE_ESTACION")).thenReturn(Optional.of(rol));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, 
            () -> usuarioService.registrarNuevoUsuario(dto));
        
        assertEquals("Debe asignar un lugar Operativo para el rol de Jefe de Estación", ex.getMessage());
    }

    @Test
    void registrarNuevoUsuario_DebeLanzarExcepcion_SiEmailInvalido() {
        var dto = new AltaUsuarioRequestDTO("N", "A", 111L, "sinArroba", "p", "ROL", null);
        when(usuarioRepository.existsByEmail("sinArroba")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, 
            () -> usuarioService.registrarNuevoUsuario(dto));
        
        assertEquals("El formato del email es inválido.", ex.getMessage());
    }

    
    
    }
