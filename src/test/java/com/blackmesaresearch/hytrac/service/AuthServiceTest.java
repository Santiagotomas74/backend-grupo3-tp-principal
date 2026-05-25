package com.blackmesaresearch.hytrac.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.blackmesaresearch.hytrac.dto.request.LoginRequestDTO;
import com.blackmesaresearch.hytrac.dto.response.LoginResponseDTO;
import com.blackmesaresearch.hytrac.model.core.Usuario;
import com.blackmesaresearch.hytrac.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_DebeLanzarExcepcionCuandoEmailNoExiste() {

        when(usuarioRepository.findByEmail("noexiste@mail.com")).thenReturn(Optional.empty());

        LoginRequestDTO dto = new LoginRequestDTO("noexiste@mail.com", "1234");

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> authService.login(dto)
        );

        assertEquals("Email o contraseña incorrectos.", exception.getMessage());

        verify(jwtService, never()).generarToken(any());
    }

    @Test
    void login_DebeLanzarExcepcionCuandoContraseñaEsIncorrecta() {

        var usuario = new Usuario();
        usuario.setPasswordHash("hash1234");

        when(usuarioRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("1234", "hash1234")).thenReturn(false);

        LoginRequestDTO dto = new LoginRequestDTO("test@mail.com", "1234");

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> authService.login(dto)
        );

        assertEquals("Email o contraseña incorrectos.", exception.getMessage());
        verify(jwtService, never()).generarToken(any());

    }


    @Test
    void Login_DebeRetornarTokenCuandoCredencialesSonCorrectas() {

        var rol = new com.blackmesaresearch.hytrac.model.lookup.Rol();
        rol.setNombre("OPERADOR");

        var usuario = new Usuario();
        usuario.setId(1);
        usuario.setNombre("Ricky");
        usuario.setApellido("Maravilla");
        usuario.setEmail("sosdelaB@mail.com");
        usuario.setPasswordHash("quemasteelestadio");
        usuario.setRol(rol);

        when(usuarioRepository.findByEmail("sosdelaB@mail.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("1234", "quemasteelestadio")).thenReturn(true);
        when(jwtService.generarToken(usuario)).thenReturn("token_jwt_falso");

        LoginRequestDTO dto = new LoginRequestDTO("sosdelaB@mail.com", "1234");

        LoginResponseDTO response = authService.login(dto);

        assertTrue(response.success());
        assertEquals("token_jwt_falso", response.token());
        assertEquals("OPERADOR", response.rol());
        verify(jwtService).generarToken(usuario);
    }


    
}