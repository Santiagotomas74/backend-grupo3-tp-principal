package com.blackmesaresearch.hytrac.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.blackmesaresearch.hytrac.dto.response.OrdenTransportistaResponseDTO;
import com.blackmesaresearch.hytrac.service.TransportistaOrdenService;

@RestController
@RequestMapping("/api/transportista")
@CrossOrigin("*")
public class TransportistaOrdenController {

    private final TransportistaOrdenService service;

    public TransportistaOrdenController(
        TransportistaOrdenService service
    ) {
        this.service = service;
    }

    // =========================
    // OBTENER ORDEN ACTIVA
    // =========================

    @GetMapping("/{legajo}/orden")
    public ResponseEntity<?> obtenerOrdenActiva(
        @PathVariable String legajo
    ) {

        try {

            OrdenTransportistaResponseDTO response =
                service.obtenerOrdenPendiente(legajo);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(
                Map.of(
                    "success", false,
                    "message", e.getMessage()
                )
            );
        }
    }

    // =========================
    // INICIAR VIAJE
    // =========================

    @PutMapping("/orden/{ordenId}/iniciar-viaje")
    public ResponseEntity<?> iniciarViaje(
        @PathVariable Integer ordenId
    ) {

        try {

            service.iniciarViaje(ordenId);

            return ResponseEntity.ok(
                Map.of(
                    "success", true,
                    "message",
                    "Viaje iniciado correctamente."
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