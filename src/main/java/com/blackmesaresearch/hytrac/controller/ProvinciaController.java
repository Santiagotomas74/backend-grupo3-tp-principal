package com.blackmesaresearch.hytrac.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blackmesaresearch.hytrac.dto.response.ProvinciaResponseDTO;
import com.blackmesaresearch.hytrac.service.ProvinciaService;

@RestController
<<<<<<< HEAD
@RequestMapping("/api/provincias")
=======
@RequestMapping("/provincias")
>>>>>>> 5df270d33d9df5d9be29376f60cebc17b275eae7
@CrossOrigin("*")
public class ProvinciaController {

    private final ProvinciaService provinciaService;

    public ProvinciaController(ProvinciaService provinciaService) {
        this.provinciaService = provinciaService;
    }

    @GetMapping("/get")
    public ResponseEntity<List<ProvinciaResponseDTO>> obtenerProvincias() {
        return ResponseEntity.ok(provinciaService.obtenerTodas());
    }
}

<<<<<<< HEAD
// hacer un join de la localidad a las provincias, y de localidad a
// plantas/estaciones
=======
//hacer un join de la localidad a las provincias, y de localidad a plantas/estaciones
>>>>>>> 5df270d33d9df5d9be29376f60cebc17b275eae7
