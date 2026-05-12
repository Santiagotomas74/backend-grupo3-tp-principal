package com.blackmesaresearch.hytrac.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.blackmesaresearch.hytrac.model.core.OrdenCarga;
import com.blackmesaresearch.hytrac.service.OrdenCargaService;
import com.blackmesaresearch.hytrac.dto.request.OrdenCargaRequestDTO;
import com.blackmesaresearch.hytrac.dto.response.OrdenCargaDetalleResponseDTO;
import com.blackmesaresearch.hytrac.dto.response.OrdenCargaResponseDTO;
import org.springframework.http.ResponseEntity;
import java.util.Map;


@RestController
@RequestMapping("/api/ordenes")
@CrossOrigin("*")
public class OrdenCargaController {

    @Autowired
    private OrdenCargaService ordenCargaService;

   
    @GetMapping("/get")
    public ResponseEntity<List<OrdenCargaResponseDTO>> obtenerOrdenes() {
        return ResponseEntity.ok(ordenCargaService.obtenerTodas());
    }

    
    @PostMapping("/crear")
public ResponseEntity<?> crearOrdenCarga(@RequestBody OrdenCargaRequestDTO dto) {

    try {

        OrdenCargaResponseDTO response =
                ordenCargaService.guardarNuevaOrdenCarga(dto);

        return ResponseEntity.status(201).body(response);

    } catch (IllegalArgumentException e) {

        return ResponseEntity.badRequest().body(
                Map.of(
                        "success", false,
                        "message", e.getMessage()
                )
        );

    } catch (Exception e) {

        return ResponseEntity.status(500).body(
                Map.of(
                        "success", false,
                        "message", "Error interno al crear la orden de carga"
                )
        );
    }
}
    @GetMapping("/{id}")
public OrdenCargaDetalleResponseDTO obtenerPorId(@PathVariable Integer id) {
    return ordenCargaService.obtenerDetallePorId(id);
}
}