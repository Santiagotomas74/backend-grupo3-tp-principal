package com.blackmesaresearch.hytrac.service;

import com.blackmesaresearch.hytrac.model.core.Usuario;
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
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        // Combine Roles (ROLE_XXXX) and Permissions (XXXX) into one list
        var authorities = usuario.getRoles().stream()
                .flatMap(rol -> Stream.concat(
                        Stream.of(new SimpleGrantedAuthority("ROLE_" + rol.getNombre())), // Adds ROLE_ADMIN
                        rol.getPermisos().stream().map(p -> new SimpleGrantedAuthority(p.getCodigo())) // Adds CAN_READ
                ))
                .collect(Collectors.toSet());

        return User.builder()
                .username(usuario.getEmail()) // por ahora se esta usando el mail como username. sujeto a cambios
                .password(usuario.getPasswordHash()) // This must be the BCrypt hash from DB
                .disabled(!usuario.isActivo())
                //.authorities(authorities)
                .build();
    }
}