package com.blackmesaresearch.hytrac.controller;

import com.blackmesaresearch.hytrac.dto.response.TransportistaResponseDTO;
import com.blackmesaresearch.hytrac.service.TransportistaService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transportistas")
@CrossOrigin("*")
public class TransportistaController {

    private final TransportistaService transportistaService;

    public TransportistaController(
            TransportistaService transportistaService) {
        this.transportistaService = transportistaService;
    }

    @GetMapping
    public List<TransportistaResponseDTO> obtenerTodos() {
        return transportistaService.obtenerTodos();
    }
}