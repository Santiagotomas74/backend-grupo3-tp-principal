package com.blackmesaresearch.hytrac.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.blackmesaresearch.hytrac.model.core.OrdenCarga;
import com.blackmesaresearch.hytrac.service.OrdenCargaService;
import com.blackmesaresearch.hytrac.dto.request.OrdenCargaRequestDTO;
import com.blackmesaresearch.hytrac.dto.response.OrdenCargaResponseDTO;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/api/ordenes")
@CrossOrigin("*")
public class OrdenCargaController {

    @Autowired
    private OrdenCargaService ordenCargaService;

    //@PreAuthorize("hasAuthority('ORDEN_VER')")
    @GetMapping("/get")
    public ResponseEntity<List<OrdenCargaResponseDTO>> obtenerOrdenes() {
        return ResponseEntity.ok(ordenCargaService.obtenerTodas());
    }

    
    @PostMapping("/crear")
    public ResponseEntity<OrdenCargaResponseDTO> crearOrdenCarga(@RequestBody OrdenCargaRequestDTO dto) {
        return ResponseEntity.status(201).body(ordenCargaService.guardarNuevaOrdenCarga(dto));
    }
}