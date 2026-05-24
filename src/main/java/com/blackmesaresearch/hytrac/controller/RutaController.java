package com.blackmesaresearch.hytrac.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blackmesaresearch.hytrac.service.RutaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/rutas")
@CrossOrigin("*")
public class RutaController {

    private final RutaService rutaService;

    public RutaController(RutaService rutaService) {
        this.rutaService = rutaService;
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getRuta(@PathVariable Integer id) {
        return ResponseEntity.ok(rutaService.obtenerRutaPorId(id));
    }

    @GetMapping("/calculate/{origenId}/{destinoId}")
    public ResponseEntity<?> calcularRuta(
            @PathVariable Integer origenId,
            @PathVariable Integer destinoId) {
        return ResponseEntity.ok(rutaService.calcularRuta(origenId, destinoId));
    }

}
