package com.blackmesaresearch.hytrac.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blackmesaresearch.hytrac.dto.response.LugarOperativoResponseDTO;
import com.blackmesaresearch.hytrac.service.LugarOperativoService;

@RestController
<<<<<<< HEAD
@RequestMapping("/api/lugares-operativos")
=======
@RequestMapping("/lugares-operativos")
>>>>>>> 5df270d33d9df5d9be29376f60cebc17b275eae7
@CrossOrigin("*")
public class LugarOperativoController {

    private final LugarOperativoService lugarOperativoService;

    public LugarOperativoController(LugarOperativoService lugarOperativoService) {
        this.lugarOperativoService = lugarOperativoService;
    }

    @GetMapping("/plantas/get")
    public ResponseEntity<List<LugarOperativoResponseDTO>> obtenerPlantas() {
        return ResponseEntity.ok(lugarOperativoService.obtenerPlantas());
    }

    @GetMapping("/estaciones-servicio/get")
    public ResponseEntity<List<LugarOperativoResponseDTO>> obtenerEstacionesServicio() {
        return ResponseEntity.ok(lugarOperativoService.obtenerEstacionesServicio());
    }
}