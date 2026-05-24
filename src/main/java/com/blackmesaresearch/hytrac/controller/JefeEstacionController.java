package com.blackmesaresearch.hytrac.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.blackmesaresearch.hytrac.dto.request.ConfirmarEntregaRequestDTO;
import com.blackmesaresearch.hytrac.service.OrdenCargaService;

@RestController
@RequestMapping("/api/jefe-estacion")
@CrossOrigin("*")
public class JefeEstacionController {

    private final OrdenCargaService ordenCargaService;

    public JefeEstacionController(
        OrdenCargaService ordenCargaService
    ) {
        this.ordenCargaService = ordenCargaService;
    }

    // =========================
    // REPORTAR ENTREGA
    // =========================

    @PutMapping("/orden/{id}/reportar-entrega")
    public ResponseEntity<?> reportarEntrega(
        @PathVariable Integer id,
        @RequestBody ConfirmarEntregaRequestDTO dto
    ) {

        try {

            ordenCargaService.reportarEntrega(
                id,
                dto
            );

            return ResponseEntity.ok(
                Map.of(
                    "success", true,
                    "message",
                    "Entrega reportada correctamente."
                )
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