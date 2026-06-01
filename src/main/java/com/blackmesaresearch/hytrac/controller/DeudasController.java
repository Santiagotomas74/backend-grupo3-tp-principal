package com.blackmesaresearch.hytrac.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blackmesaresearch.hytrac.dto.response.DeudasResponseDTO;
import com.blackmesaresearch.hytrac.service.DeudasApiService;


@RestController
@RequestMapping("/api/bcra")
public class DeudasController {

    private final DeudasApiService deudasApiService;

    public DeudasController(DeudasApiService deudasApiService) {
        this.deudasApiService = deudasApiService;
    }

    @GetMapping("/{cuit}")
    public ResponseEntity<?> consultarDeudaActual(@PathVariable String cuit) {
        DeudasResponseDTO resultado = deudasApiService.consultarDeudaActual(cuit);
        if (resultado != null) {
            return ResponseEntity.ok(resultado);
        } else {
            return ResponseEntity.status(503).body("{\"error\": \"Servicio del BCRA no disponible o CUIT sin registros.\"}");
        }
    }


    
}
