package com.blackmesaresearch.hytrac.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.blackmesaresearch.hytrac.dto.request.AltaTransportistaRequestDTO;
import com.blackmesaresearch.hytrac.dto.request.ReportarIncidenciaRequestDTO;
import com.blackmesaresearch.hytrac.dto.response.TransportistaResponseDTO;
import com.blackmesaresearch.hytrac.service.IncidenciaService;
import com.blackmesaresearch.hytrac.service.TransportistaService;

@RestController
@RequestMapping("/api/transportistas")
@CrossOrigin("*")
public class TransportistaController {

    private final TransportistaService transportistaService;

    private final IncidenciaService incidenciaService;

    public TransportistaController(

            TransportistaService transportistaService,

            IncidenciaService incidenciaService

    ) {

        this.transportistaService = transportistaService;

        this.incidenciaService = incidenciaService;
    }

    // =========================
    // OBTENER TODOS
    // =========================

    @GetMapping
    public List<TransportistaResponseDTO> obtenerTodos() {

        return transportistaService.obtenerTodos();
    }

    // =========================
    // REPORTAR INCIDENCIA
    // =========================

    @PostMapping("/incidencia")
    public ResponseEntity<?> reportarIncidencia(
            @RequestBody ReportarIncidenciaRequestDTO dto) {

        try {

            incidenciaService.reportarIncidencia(dto);

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message",
                            "Incidencia reportada correctamente."));

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", e.getMessage()));
        }
    }

    // =========================
    // Alta Transportista
    // =========================

    @PostMapping("/alta")
    public ResponseEntity<?> registraTransportista(@RequestBody AltaTransportistaRequestDTO dto) {
        try {
            transportistaService.registrarNuevoTransportista(dto);

            return ResponseEntity.status(201).body(Map.of(
                "success", true,
                "message", "El transportista y su documentación fueron registrados"
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Error interno al procesar el alta del transportista."
            ));
        }
    }
}