package com.blackmesaresearch.hytrac.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.blackmesaresearch.hytrac.dto.response.CombustibleResponseDTO;
import com.blackmesaresearch.hytrac.service.CombustibleService;
import java.util.List;

@RestController
@RequestMapping("/api/combustibles")
@CrossOrigin("*")
public class CombustibleController {

    @Autowired
    private CombustibleService combustibleService;

    @GetMapping
    public ResponseEntity<List<CombustibleResponseDTO>> obtenerCombustibles() {
        return ResponseEntity.ok(combustibleService.obtenerCombustibles());
    }
}
