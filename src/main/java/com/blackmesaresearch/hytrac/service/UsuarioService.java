package com.blackmesaresearch.hytrac.service;

import com.blackmesaresearch.hytrac.model.core.Usuario;
import com.blackmesaresearch.hytrac.model.lookup.Rol;
import com.blackmesaresearch.hytrac.repository.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con email: " + email));

        Rol rol = usuario.getRol();

        var authorities = Stream.concat(
                Stream.of(
                        new SimpleGrantedAuthority("ROLE_" + rol.getNombre())
                ),
                rol.getPermisos().stream()
                        .map(p -> new SimpleGrantedAuthority(p.getCodigo()))
        ).collect(Collectors.toSet());

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPasswordHash())
                .disabled(!usuario.isActivo())
                .authorities(authorities)
                .build();
    }
}