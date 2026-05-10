package com.blackmesaresearch.hytrac.service;

import org.springframework.stereotype.Service;

import com.blackmesaresearch.hytrac.model.core.Usuario;
import com.blackmesaresearch.hytrac.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
            .orElseThrow();
    }
}
