package com.blackmesaresearch.hytrac.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blackmesaresearch.hytrac.dto.request.AltaUsuarioRequestDTO;
import com.blackmesaresearch.hytrac.dto.request.EditarUsuarioRequestDTO;
import com.blackmesaresearch.hytrac.dto.response.UsuarioAdminResponseDTO;
import com.blackmesaresearch.hytrac.service.AdministradorService;
import com.blackmesaresearch.hytrac.service.UsuarioService;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin("*")
public class AdministradorController {

    private final AdministradorService administradorService;
    private final UsuarioService usuarioService;

    public AdministradorController(
            AdministradorService administradorService,
            UsuarioService usuarioService) {

        this.administradorService = administradorService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/usuarios")
    public List<UsuarioAdminResponseDTO> obtenerUsuarios() {

        return administradorService.obtenerUsuarios();
    }

    @PostMapping("/usuarios/alta")
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

    @PutMapping("/usuarios/editar/{id}")
    public ResponseEntity<?> editarUsuario(
        @PathVariable Integer id,
        @RequestBody EditarUsuarioRequestDTO dto) {

            try {
                UsuarioAdminResponseDTO response = administradorService.editarUsuario(id, dto);
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Usuario Actualizado Correctamente.",
                    "data", response
                ));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
                ));
            } catch (Exception e) {
                return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Error interno al editar el usuario."
                ));
            }

        }

        @DeleteMapping("/usuarios/baja/{id}")
        public ResponseEntity<?> darDeBajaUsuario(@PathVariable Integer id) {
            try {
                administradorService.darBajaUsuario(id);
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Usuario dado de baja correctamente."
                ));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
                ));
            } catch (Exception e) {
                return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Error interno al intentar dar de baja al usuario. Reintente más tarde"
                ));
            }
        }
    
}