
package com.blackmesaresearch.hytrac.controller;

import org.springframework.web.bind.annotation.*;

import com.blackmesaresearch.hytrac.service.OrdenCargaService;

import com.blackmesaresearch.hytrac.dto.response.OrdenSupervisorDetalleResponseDTO;
import com.blackmesaresearch.hytrac.dto.response.OrdenSupervisorResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/supervisor/ordenes")
@CrossOrigin("*")
public class SupervisorOrdenController {

    private final OrdenCargaService ordenCargaService;

    public SupervisorOrdenController(
        OrdenCargaService ordenCargaService
    ) {
        this.ordenCargaService = ordenCargaService;
    }

    // =========================
    // TODAS LAS ORDENES
    // =========================

    @GetMapping
    public ResponseEntity<List<OrdenSupervisorResponseDTO>>
    obtenerTodas() {

        return ResponseEntity.ok(
            ordenCargaService.obtenerTodasSupervisor()
        );
    }

    // =========================
    // DETALLE POR ID
    // =========================

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(
        @PathVariable Integer id
    ) {

        try {

            return ResponseEntity.ok(
                ordenCargaService.obtenerOrdenSupervisor(id)
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(
                Map.of(
                    "success", false,
                    "message", e.getMessage()
                )
            );
        }
    }
}