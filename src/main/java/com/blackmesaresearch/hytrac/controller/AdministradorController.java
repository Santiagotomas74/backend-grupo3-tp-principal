package com.blackmesaresearch.hytrac.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.blackmesaresearch.hytrac.dto.response.UsuarioAdminResponseDTO;
import com.blackmesaresearch.hytrac.service.AdministradorService;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin("*")
public class AdministradorController {

    private final AdministradorService administradorService;

    public AdministradorController(
            AdministradorService administradorService) {

        this.administradorService = administradorService;
    }

    @GetMapping("/usuarios")
    public List<UsuarioAdminResponseDTO> obtenerUsuarios() {

        return administradorService.obtenerUsuarios();
    }
}