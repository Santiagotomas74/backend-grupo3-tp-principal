
package com.blackmesaresearch.hytrac.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blackmesaresearch.hytrac.dto.request.AprobarInicioViajeRequestDTO;
import com.blackmesaresearch.hytrac.dto.request.CancelarOrdenRequestDTO;
import com.blackmesaresearch.hytrac.dto.request.ConfirmarEntregaRequestDTO;
import com.blackmesaresearch.hytrac.dto.request.RechazarRequestDTO;
import com.blackmesaresearch.hytrac.dto.response.OrdenCargaResponseDTO;
import com.blackmesaresearch.hytrac.dto.response.OrdenSupervisorResponseDTO;
import com.blackmesaresearch.hytrac.service.OrdenCargaService;

@RestController
@RequestMapping("/api/supervisor/ordenes")
@CrossOrigin("*")
public class SupervisorOrdenController {

    private final OrdenCargaService ordenCargaService;

    public SupervisorOrdenController(
            OrdenCargaService ordenCargaService) {
        this.ordenCargaService = ordenCargaService;
    }

    // =========================
    // TODAS LAS ORDENES
    // =========================

    @GetMapping
    public ResponseEntity<List<OrdenSupervisorResponseDTO>> obtenerTodas() {

        return ResponseEntity.ok(
                ordenCargaService.obtenerTodasSupervisor());
    }

    // =========================
    // DETALLE POR ID
    // =========================

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(
            @PathVariable Integer id) {

        try {

            return ResponseEntity.ok(
                    ordenCargaService.obtenerOrdenSupervisor(id));

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", e.getMessage()));
        }
    }

    // =========================
    // CONFIRMAR ORDEN
    // =========================

    @PutMapping("/{id}/confirmar")
    public ResponseEntity<?> confirmarOrden(
            @PathVariable Integer id) {

        try {

            ordenCargaService.confirmarOrden(id);

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "Orden confirmada correctamente."));

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", e.getMessage()));
        }
    }

    // =========================
    // APROBAR INICIO DE VIAJE
    // =========================

    @PutMapping("/{id}/aprobar-inicio")
public ResponseEntity<?> aprobarInicioViaje(
        @PathVariable Integer id,
        @RequestBody AprobarInicioViajeRequestDTO dto) {

    try {

        ordenCargaService.aprobarInicioViaje(
                id,
                dto.legajoSupervisor());

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message",
                        "La orden pasó a estado En Curso."));

    } catch (IllegalArgumentException e) {

        return ResponseEntity.badRequest().body(
                Map.of(
                        "success", false,
                        "message", e.getMessage()));
    }
}

    @PutMapping("/remito/{numeroRemito}/gestion-incidencia")
    public ResponseEntity<?> cancelarOrden(
            @PathVariable String numeroRemito,
            @RequestBody CancelarOrdenRequestDTO dto) {
        try {
            // Enviamos el String (numeroRemito) al service modificado
            OrdenCargaResponseDTO response = ordenCargaService.cancelarOrden(numeroRemito, dto);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    Map.of(
                            "success", false,
                            "message", "Error interno al intentar procesar la cancelación de la orden"));
        }
    }

      // =========================
    // APROBAR la entrega
    // =========================

@PutMapping("/{id}/confirmar-entrega")
public ResponseEntity<?> confirmarEntrega(
        @PathVariable Integer id,
        @RequestBody ConfirmarEntregaRequestDTO dto) {

    try {

        ordenCargaService.confirmarEntrega(
                id,
                dto.legajoSupervisor());

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message",
                        "Entrega confirmada correctamente."
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

        // Rechazar Orden
        @PutMapping("/{id}/rechazar")
        public ResponseEntity<?> rechazarOrden(@PathVariable Integer id, @RequestBody RechazarRequestDTO dto) {
                try {
                        ordenCargaService.rechazarOrden(id, dto.legajoSupervisor(), dto.motivoRechazo());
                        return ResponseEntity.ok(Map.of(
                                                        "success", true,
                                                        "message", "Orden rechazada correctamente."));
                } catch (IllegalArgumentException e) {
                        return ResponseEntity.badRequest().body(Map.of(
                                                        "success", false,
                                                        "message", e.getMessage()));
                }
        }

        // Rechazar Inicio de Viaje
        @PutMapping("/{id}/rechazar-inicio")
        public ResponseEntity<?> rechazarInicioViaje(@PathVariable Integer id, @RequestBody RechazarRequestDTO dto) {
                try {
                        ordenCargaService.rechazarInicioViaje(id, dto.legajoSupervisor(), dto.motivoRechazo());
                        return ResponseEntity.ok(Map.of(
                                                        "success", true,
                                                        "message", "Inicio de viaje rechazado correctamente."));
                } catch (IllegalArgumentException e) {
                        return ResponseEntity.badRequest().body(Map.of(
                                                        "success", false,
                                                        "message", e.getMessage()));
                }
        }

        // Rechazar Entrega
        @PutMapping("/{id}/rechazar-entrega")
        public ResponseEntity<?> rechazarEntrega(@PathVariable Integer id, @RequestBody RechazarRequestDTO dto) {
                try {
                        ordenCargaService.rechazarEntrega(id, dto.legajoSupervisor(), dto.motivoRechazo());
                        return ResponseEntity.ok(Map.of(
                                                        "success", true,
                                                        "message", "Entrega rechazada correctamente."));
                } catch (IllegalArgumentException e) {
                        return ResponseEntity.badRequest().body(Map.of(
                                                        "success", false,
                                                        "message", e.getMessage()));
                }
}
}