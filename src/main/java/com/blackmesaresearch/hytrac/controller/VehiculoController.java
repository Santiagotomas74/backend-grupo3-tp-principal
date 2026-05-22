package com.blackmesaresearch.hytrac.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
<<<<<<< HEAD

import com.blackmesaresearch.hytrac.dto.response.AcopladoResponseDTO;
import com.blackmesaresearch.hytrac.dto.response.VehiculoResponseDTO;
import com.blackmesaresearch.hytrac.service.VehiculoService;

=======
import com.blackmesaresearch.hytrac.dto.response.VehiculoResponseDTO;
import com.blackmesaresearch.hytrac.service.VehiculoService;




>>>>>>> 5df270d33d9df5d9be29376f60cebc17b275eae7
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
<<<<<<< HEAD
    public ResponseEntity<List<AcopladoResponseDTO>> obtenerAcoplados() {

        List<AcopladoResponseDTO> acoplados = vehiculoService.obtenerAcoplados();

=======
    public ResponseEntity<List<VehiculoResponseDTO>> obtenerAcoplados() {
        List<VehiculoResponseDTO> acoplados = vehiculoService.obtenerAcoplados();
>>>>>>> 5df270d33d9df5d9be29376f60cebc17b275eae7
        return ResponseEntity.ok(acoplados);
    }
}