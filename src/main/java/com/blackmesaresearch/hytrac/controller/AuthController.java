package com.blackmesaresearch.hytrac.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blackmesaresearch.hytrac.dto.request.AceptarTerminosRequestDTO;
import com.blackmesaresearch.hytrac.dto.request.LoginRequestDTO;
import com.blackmesaresearch.hytrac.dto.response.LoginResponseDTO;
import com.blackmesaresearch.hytrac.service.AuthService;
import com.blackmesaresearch.hytrac.service.UsuarioService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;

    public AuthController(AuthService authService, UsuarioService usuarioService) {
        this.authService = authService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequestDTO dto) {

        try {

            LoginResponseDTO response = authService.login(dto);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", e.getMessage()));

        } catch (Exception e) {

            return ResponseEntity.internalServerError().body(
                    Map.of(
                            "success", false,
                            "message", "Error interno del servidor"));
        }
    }

    @PostMapping("/aceptar-terminos")
    public ResponseEntity<?> aceptarTerminos(@RequestBody AceptarTerminosRequestDTO dto) {
        try {
            usuarioService.aceptarTerminos(dto.legajo());
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Términos aceptados correctamente."
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
}
}