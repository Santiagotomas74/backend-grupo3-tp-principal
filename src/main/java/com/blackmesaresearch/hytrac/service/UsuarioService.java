package com.blackmesaresearch.hytrac.service;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.blackmesaresearch.hytrac.dto.request.AltaUsuarioRequestDTO;
import com.blackmesaresearch.hytrac.model.core.LugarOperativo;
import com.blackmesaresearch.hytrac.model.core.Usuario;
import com.blackmesaresearch.hytrac.model.lookup.Rol;
import com.blackmesaresearch.hytrac.repository.LugarOperativoRepository;
import com.blackmesaresearch.hytrac.repository.RolRepository;
import com.blackmesaresearch.hytrac.repository.UsuarioRepository;

@Service
public class UsuarioService implements UserDetailsService {

        private final UsuarioRepository usuarioRepository;
        private final RolRepository rolRepository;
        private final LugarOperativoRepository lugarOperativoRepository;
        private final PasswordEncoder passwordEncoder;

        public UsuarioService(UsuarioRepository usuarioRepository, 
                                RolRepository rolRepository, LugarOperativoRepository lugarOperativoRepository, PasswordEncoder passwordEncoder) {
                this.usuarioRepository = usuarioRepository;
                this.rolRepository = rolRepository;
                this.lugarOperativoRepository = lugarOperativoRepository;
                this.passwordEncoder = passwordEncoder;
        }

        @Override
        public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

                Usuario usuario = usuarioRepository.findByEmail(email)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "Usuario no encontrado con email: " + email));

                Rol rol = usuario.getRol();

                var authorities = Stream.concat(
                                Stream.of(
                                                new SimpleGrantedAuthority("ROLE_" + rol.getNombre())),
                                rol.getPermisos().stream()
                                                .map(p -> new SimpleGrantedAuthority(p.getCodigo())))
                                .collect(Collectors.toSet());

                return User.builder()
                                .username(usuario.getEmail())
                                .password(usuario.getPasswordHash())
                                .disabled(!usuario.isActivo())
                                // .authorities(authorities)
                                .build();
        }

        // Alta de Nuevo Usuario //
        public void registrarNuevoUsuario(AltaUsuarioRequestDTO dto) {

                if (usuarioRepository.existsByEmail(dto.email())) {
                        throw new IllegalArgumentException( "El Email ya se encuentra registrado");
                }

                if (!dto.email().contains("@")) {
                        throw new IllegalArgumentException("El formato del email es inválido.");
                }

                if (usuarioRepository.existsByDni(dto.dni())) {
                        throw new IllegalArgumentException( "El DNI ya se encuentra registrado");
                }

                Rol rol = rolRepository.findByNombre(dto.rolNombre())
                                .orElseThrow(() -> new IllegalArgumentException("El Rol especificado no existe: " + dto.rolNombre()));

                LugarOperativo lugarOperativo = null;
                if (rol.getNombre().equalsIgnoreCase("JEFE_ESTACION")) {
                        if (dto.lugarOperativoId() == null) {
                                throw new IllegalArgumentException("Debe asignar un lugar Operativo para el rol de Jefe de Estación");
                        }
                        lugarOperativo = lugarOperativoRepository.findById(dto.lugarOperativoId())
                                        .orElseThrow(() -> new IllegalArgumentException("El Lugar Operativo especificado no existe: " + dto.lugarOperativoId()));
                }

                Usuario nuevoUsuario = new Usuario();
                nuevoUsuario.setNombre(dto.nombre());
                nuevoUsuario.setApellido(dto.apellido());
                nuevoUsuario.setDni(dto.dni());
                nuevoUsuario.setEmail(dto.email());

                nuevoUsuario.setLegajo(generarNuevoLegajo());

                nuevoUsuario.setRol(rol);
                nuevoUsuario.setLugarOperativo(lugarOperativo);


                nuevoUsuario.setPasswordHash(passwordEncoder.encode(dto.passwordTemporal()));
                nuevoUsuario.setActivo(true);

                usuarioRepository.save(nuevoUsuario);

        }

        // AUX Generador Legajo //
        private String generarNuevoLegajo() {
                return usuarioRepository.findTopByLegajoStartingWithOrderByLegajoDesc("LEG")
                        .map(Usuario::getLegajo)
                        .filter(legajo -> legajo != null && legajo.startsWith("LEG"))
                        .map(legajo -> {
                                // Parte Númerica del legajo
                                String numeroStr = legajo.substring(3);
                                try {
                                        int numero = Integer.parseInt(numeroStr);
                                        return String.format("LEG%03d", numero + 1);
                                } catch (NumberFormatException e) {
                                        return String.format("LEG%03d", usuarioRepository.count() + 1);
                                }
                        })
                        .orElse("LEG001");
        }

}