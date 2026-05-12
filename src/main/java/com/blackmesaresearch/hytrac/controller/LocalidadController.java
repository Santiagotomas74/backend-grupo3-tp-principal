package com.blackmesaresearch.hytrac.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blackmesaresearch.hytrac.dto.response.LocalidadResponseDTO;
import com.blackmesaresearch.hytrac.service.LocalidadService;

@RestController
@RequestMapping("/api/localidades")
@CrossOrigin("*")
public class LocalidadController {

    private final LocalidadService localidadService;

    public LocalidadController(LocalidadService localidadService) {
        this.localidadService = localidadService;
    }

    @GetMapping("/get")
    public ResponseEntity<List<LocalidadResponseDTO>> obtenerLocalidades() {
        return ResponseEntity.ok(localidadService.obtenerTodas());
    }

    @GetMapping("/provincia/{provinciaId}")
    public ResponseEntity<List<LocalidadResponseDTO>> obtenerLocalidadesPorProvincia(@PathVariable Integer provinciaId) {
        return ResponseEntity.ok(localidadService.obtenerPorProvincia(provinciaId));
    }
}