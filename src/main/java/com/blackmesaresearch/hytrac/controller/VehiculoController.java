package com.blackmesaresearch.hytrac.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.blackmesaresearch.hytrac.dto.response.VehiculoResponseDTO;
import com.blackmesaresearch.hytrac.service.VehiculoService;




@RestController
@RequestMapping("/api/vehiculos")
@CrossOrigin("*")
public class VehiculoController {

    @Autowired
    private VehiculoService vehiculoService;

    @GetMapping("/camiones")
    public ResponseEntity<List<VehiculoResponseDTO>> obtenerCamiones() {
        List<VehiculoResponseDTO> camiones = vehiculoService.obtenerCamiones();
        return ResponseEntity.ok(camiones);
    }

    @GetMapping("/acoplados")
    public ResponseEntity<List<VehiculoResponseDTO>> obtenerAcoplados() {
        List<VehiculoResponseDTO> acoplados = vehiculoService.obtenerAcoplados();
        return ResponseEntity.ok(acoplados);
    }
}