package com.blackmesaresearch.hytrac.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.blackmesaresearch.hytrac.dto.response.IncidenciaResponseDTO;
import com.blackmesaresearch.hytrac.service.IncidenciaService;

@RestController
@RequestMapping("/api/supervisor/incidencias")
@CrossOrigin("*")
public class SupervisorIncidenciaController {

    private final IncidenciaService incidenciaService;

    public SupervisorIncidenciaController(
        IncidenciaService incidenciaService
    ) {
        this.incidenciaService = incidenciaService;
    }

    // =========================
    // TODAS LAS INCIDENCIAS
    // =========================

    @GetMapping
    public ResponseEntity<List<IncidenciaResponseDTO>>
    obtenerTodas() {

        return ResponseEntity.ok(
            incidenciaService.obtenerTodas()
        );
    }
}