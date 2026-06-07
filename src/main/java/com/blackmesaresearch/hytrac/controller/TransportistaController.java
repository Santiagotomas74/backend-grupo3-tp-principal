package com.blackmesaresearch.hytrac.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.blackmesaresearch.hytrac.dto.request.AltaTransportistaRequestDTO;
import com.blackmesaresearch.hytrac.dto.request.ReportarIncidenciaRequestDTO;
import com.blackmesaresearch.hytrac.dto.request.TransportistaOptimoRequestDTO;
import com.blackmesaresearch.hytrac.dto.response.TransportistaResponseDTO;
import com.blackmesaresearch.hytrac.service.IncidenciaService;
import com.blackmesaresearch.hytrac.service.TransportistaService;
import com.blackmesaresearch.hytrac.service.SeleccionTransportistasService;

@RestController
@RequestMapping("/api/transportistas")
@CrossOrigin("*")
public class TransportistaController {

    private final TransportistaService transportistaService;
    private final IncidenciaService incidenciaService;
    private final SeleccionTransportistasService seleccionTransportistasService;

    public TransportistaController(

            TransportistaService transportistaService,

            IncidenciaService incidenciaService,
            SeleccionTransportistasService seleccionTransportistasService

    ) {

        this.transportistaService = transportistaService;

        this.incidenciaService = incidenciaService;

        this.seleccionTransportistasService = seleccionTransportistasService;
    }

    // =========================
    // OBTENER TODOS
    // =========================

    @GetMapping
    public List<TransportistaResponseDTO> obtenerTodos() {

        return transportistaService.obtenerTodos();
    }

    @GetMapping("/disponibles")
    public List<TransportistaResponseDTO> obtenerDisponibles() {

        return transportistaService.obtenerDisponibles();
    }

    @PostMapping("/seleccionar-optimos")
    public ResponseEntity<?> seleccionarTransportistas(
            @RequestBody TransportistaOptimoRequestDTO dto) {

        // Implement the logic to select transportistas based on volume and distance
        return ResponseEntity.ok(seleccionTransportistasService.seleccionarTransportistasOptimos(dto));
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