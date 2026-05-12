package com.blackmesaresearch.hytrac.controller;

import org.springframework.web.bind.annotation.*;

import com.blackmesaresearch.hytrac.service.UsuarioService;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin("*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

}