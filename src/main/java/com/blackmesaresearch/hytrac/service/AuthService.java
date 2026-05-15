package com.blackmesaresearch.hytrac.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.blackmesaresearch.hytrac.dto.request.LoginRequestDTO;
import com.blackmesaresearch.hytrac.dto.response.LoginResponseDTO;
import com.blackmesaresearch.hytrac.model.core.Usuario;
import com.blackmesaresearch.hytrac.repository.UsuarioRepository;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
        UsuarioRepository usuarioRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {

        // =========================
        // BUSCAR USUARIO
        // =========================

        Usuario usuario = usuarioRepository
            .findByEmail(dto.email())
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "Email o contraseña incorrectos."
                )
            );

        // =========================
        // VALIDAR PASSWORD
        // =========================

        boolean passwordCorrecta =
            passwordEncoder.matches(
                dto.password(),
                usuario.getPasswordHash()
            );

        if (!passwordCorrecta) {

            throw new IllegalArgumentException(
                "Email o contraseña incorrectos."
            );
        }

        // =========================
        // GENERAR JWT
        // =========================

        String token =
            jwtService.generarToken(usuario);

        // =========================
        // OBTENER ROLES
        // =========================

        List<String> roles =
            usuario.getRoles()
                .stream()
                .map(rol -> rol.getNombre())
                .toList();

        // =========================
        // RESPONSE
        // =========================

        return new LoginResponseDTO(

            true,
            token,

            usuario.getId(),
            usuario.getNombre(),
            usuario.getApellido(),
            usuario.getEmail(),

            roles
        );
    }
}