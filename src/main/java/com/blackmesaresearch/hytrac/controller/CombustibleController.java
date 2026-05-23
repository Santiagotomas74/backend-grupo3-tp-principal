package com.blackmesaresearch.hytrac.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blackmesaresearch.hytrac.dto.response.CombustibleResponseDTO;
import com.blackmesaresearch.hytrac.service.CombustibleService;

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
