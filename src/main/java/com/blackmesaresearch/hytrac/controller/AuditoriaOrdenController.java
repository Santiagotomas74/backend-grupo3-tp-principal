package com.blackmesaresearch.hytrac.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.blackmesaresearch.hytrac.dto.response.AuditoriaOrdenResponseDTO;
import com.blackmesaresearch.hytrac.service.AuditoriaOrdenService;

@RestController
@RequestMapping("/api/auditoria")
@CrossOrigin("*")
public class AuditoriaOrdenController {

    private final AuditoriaOrdenService auditoriaOrdenService;

    public AuditoriaOrdenController(
            AuditoriaOrdenService auditoriaOrdenService) {

        this.auditoriaOrdenService =
                auditoriaOrdenService;
    }

    // =========================
    // TODA LA AUDITORIA
    // =========================

    @GetMapping
    public ResponseEntity<List<AuditoriaOrdenResponseDTO>>
            obtenerTodaLaAuditoria() {

        return ResponseEntity.ok(
                auditoriaOrdenService
                        .obtenerAuditoria());
    }

    // =========================
    // HISTORIAL DE UN REMITO
    // =========================

    @GetMapping("/{numeroRemito}")
    public ResponseEntity<?> obtenerHistorialRemito(
            @PathVariable String numeroRemito) {

        try {

            return ResponseEntity.ok(
                    auditoriaOrdenService
                            .obtenerPorNumeroRemito(
                                    numeroRemito));

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", e.getMessage()));
        }
    }
}