package com.blackmesaresearch.hytrac.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blackmesaresearch.hytrac.dto.response.EmpresaTercerizadaResponseDTO;
import com.blackmesaresearch.hytrac.dto.response.TipoDocumentoResponseDTO;
import com.blackmesaresearch.hytrac.dto.response.TipoVinculoResponseDTO;
import com.blackmesaresearch.hytrac.service.EntidadService;


@RestController
@RequestMapping("/api/entidades")
@CrossOrigin("*")
public class EntidadController {
    private final EntidadService entidadService;

    public EntidadController(EntidadService entidadService) {
        this.entidadService = entidadService;
    }

    @GetMapping("/empresas")
    public ResponseEntity<List<EmpresaTercerizadaResponseDTO>> obtenerEmpresas() {
        return ResponseEntity.ok(entidadService.obtenerEmpresas());
    }
    
    @GetMapping("/tipo-vinculo")
    public ResponseEntity<List<TipoVinculoResponseDTO>> obtenerTipoVinculo() {
        return ResponseEntity.ok(entidadService.obtenerTipoVinculo());
    }

    @GetMapping("/tipo-documento")
    public ResponseEntity<List<TipoDocumentoResponseDTO>> obtenerTipoDocumento() {
        return ResponseEntity.ok(entidadService.obtenerTipoDocumento());
    }


    
}
