package com.blackmesaresearch.hytrac.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blackmesaresearch.hytrac.dto.request.AltaUsuarioRequestDTO;
import com.blackmesaresearch.hytrac.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin("*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/alta")
    public ResponseEntity<?> registrarNuevoUsuario(@RequestBody AltaUsuarioRequestDTO dto) {
        try {
            usuarioService.registrarNuevoUsuario(dto);
            return ResponseEntity.status(201).body(Map.of(
                    "success", true, 
                    "message", "Usuario registrado exitosamente y listo para iniciar sesión."
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

}


