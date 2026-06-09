package com.blackmesaresearch.hytrac.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blackmesaresearch.hytrac.dto.response.MetricasDashboardDTO;
import com.blackmesaresearch.hytrac.service.ReporteService;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin("*")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    // Endpoint principal consumido por Supervisor y Admin
    @GetMapping("/dashboard")
    public ResponseEntity<MetricasDashboardDTO> obtenerMetricasDashboard() {
        return ResponseEntity.ok(reporteService.obtenerMetricasGlobales());
    }
}