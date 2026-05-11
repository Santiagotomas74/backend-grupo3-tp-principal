package com.blackmesaresearch.hytrac.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.blackmesaresearch.hytrac.model.core.Usuario;
import com.blackmesaresearch.hytrac.service.UsuarioService;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin("*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/get")
    public List<Usuario> obtenerUsuarios() {
        return usuarioService.obtenerTodos();
    }
}