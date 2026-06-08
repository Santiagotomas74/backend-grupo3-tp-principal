package com.blackmesaresearch.hytrac.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.blackmesaresearch.hytrac.dto.response.EstadoMetricaDTO;
import com.blackmesaresearch.hytrac.dto.response.MetricasDashboardDTO;
import com.blackmesaresearch.hytrac.repository.IncidenciaRepository;
import com.blackmesaresearch.hytrac.repository.ReporteRepository;

@Service
public class ReporteService {

    private final ReporteRepository reporteRepository;
    private final IncidenciaRepository incidenciaRepository;

    public ReporteService(ReporteRepository reporteRepository, IncidenciaRepository incidenciaRepository) {
        this.reporteRepository = reporteRepository;
        this.incidenciaRepository = incidenciaRepository;
    }

    public MetricasDashboardDTO obtenerMetricasGlobales() {

    Integer totalEnvios = reporteRepository.countTotalEnvios();
    Integer enviosEnViaje = reporteRepository.countEnviosEnViaje();
    Integer enviosEntregados = reporteRepository.countEnviosEntregados();
    Integer totalIncidencias = Math.toIntExact(incidenciaRepository.count());

    Double tasaEfectividad = 0.0;
    if (totalEnvios != null && totalEnvios > 0) {
        tasaEfectividad = (enviosEntregados.doubleValue() / totalEnvios.doubleValue()) * 100.0;
        tasaEfectividad = Math.round(tasaEfectividad * 100.0) / 100.0;
    }
    List<EstadoMetricaDTO> desgloses = reporteRepository.obtenerDesglosePorEstado();

    return new MetricasDashboardDTO(
        totalEnvios,
        enviosEnViaje,
        enviosEntregados,
        totalIncidencias,
        tasaEfectividad,
        desgloses
        );
    }
}