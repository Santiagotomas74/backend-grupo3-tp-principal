package com.blackmesaresearch.hytrac.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blackmesaresearch.hytrac.dto.request.CancelarOrdenRequestDTO;
import com.blackmesaresearch.hytrac.dto.request.OrdenCargaRequestDTO;
import com.blackmesaresearch.hytrac.dto.response.OrdenCargaDetalleResponseDTO;
import com.blackmesaresearch.hytrac.dto.response.OrdenCargaResponseDTO;
import com.blackmesaresearch.hytrac.service.OrdenCargaService;

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
    
@PutMapping("/{id}/cancelar")
public ResponseEntity<?> cancelarOrden(
        @PathVariable Integer id, 
        @RequestBody CancelarOrdenRequestDTO dto
) {
    try {
        OrdenCargaResponseDTO response = ordenCargaService.cancelarOrden(id, dto);
        return ResponseEntity.ok(response);
        
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
                        "message", "Error interno al intentar cancelar la orden de carga"
                )
        );
    }
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