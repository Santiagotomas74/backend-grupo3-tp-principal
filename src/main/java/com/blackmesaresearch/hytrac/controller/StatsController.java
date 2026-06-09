package com.blackmesaresearch.hytrac.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blackmesaresearch.hytrac.service.StatsService;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/api/stats")
@CrossOrigin("*")
public class StatsController {
    
    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/sistema")
    public ResponseEntity<?> statsSistema() {
        return ResponseEntity.ok(statsService.getStatsSistema());
    }

    @GetMapping("/transportista/{id}")
    public ResponseEntity<?> statsTransportista(@PathVariable("id") Integer transportistaId) {
        return ResponseEntity.ok(statsService.getStatsTransportista(transportistaId));
    }

    @GetMapping("/lugar/{id}")
    public ResponseEntity<?> statsLugar(@PathVariable("id") Integer lugarId) {
        return ResponseEntity.ok(statsService.getStatsLugar(lugarId));
    }
    
    
    
}
