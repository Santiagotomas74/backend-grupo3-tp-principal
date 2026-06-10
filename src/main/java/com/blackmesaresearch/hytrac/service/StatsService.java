package com.blackmesaresearch.hytrac.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blackmesaresearch.hytrac.dto.response.StatsLugarResponseDTO;
import com.blackmesaresearch.hytrac.dto.response.StatsSistemaResponseDTO;
import com.blackmesaresearch.hytrac.dto.response.StatsTransportistaResponseDTO;
import com.blackmesaresearch.hytrac.model.stats.*;
import com.blackmesaresearch.hytrac.repository.StatsLugarRepository;
import com.blackmesaresearch.hytrac.repository.StatsSistemaRepository;
import com.blackmesaresearch.hytrac.repository.StatsTransportistaRepository;
import com.blackmesaresearch.hytrac.repository.IncidenciaRepository;
import com.blackmesaresearch.hytrac.repository.OrdenCargaRepository;

@Service
public class StatsService {

    @Autowired
    private StatsLugarRepository statsLugarRepository;
    @Autowired
    private StatsTransportistaRepository statsTransportistaRepository;
    @Autowired
    private StatsSistemaRepository statsSistemaRepository;
    @Autowired
    private IncidenciaRepository incidenciaRepository;
    @Autowired
    private OrdenCargaRepository ordenCargaRepository;

    public StatsService(StatsLugarRepository statsLugarRepository,
            StatsTransportistaRepository statsTransportistaRepository, StatsSistemaRepository statsSistemaRepository) {
        this.statsLugarRepository = statsLugarRepository;
        this.statsTransportistaRepository = statsTransportistaRepository;
        this.statsSistemaRepository = statsSistemaRepository;
    }

    public StatsLugarResponseDTO getStatsLugar(Integer lugarId) {

        StatsLugar statsLugar = statsLugarRepository.findByLugarId(lugarId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Lugar operativo destino no encontrado con ID: " + lugarId));

        int idLugar = 0;
        if (statsLugar.getLugar() != null && statsLugar.getLugar().getId() != null) {
            idLugar = statsLugar.getLugar().getId();
        }

        Long totalDespachos = statsLugar.getDespachos() != null ? statsLugar.getDespachos().longValue() : 0L;
        Long totalRecepciones = statsLugar.getRecepciones() != null ? statsLugar.getRecepciones().longValue() : 0L;

        return new StatsLugarResponseDTO(idLugar, totalDespachos, totalRecepciones);

    }

    public StatsTransportistaResponseDTO getStatsTransportista(Integer transportistaId) {

        StatsTransportista statsTransportista = statsTransportistaRepository.findByTransportistaId(transportistaId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Transportista no encontrado con ID: " + transportistaId));

        int idTransportista = 0;
        if (statsTransportista.getTransportista() != null && statsTransportista.getTransportista().getId() != null) {
            idTransportista = statsTransportista.getTransportista().getId();
        }

        int totalOrdenes = statsTransportista.getTotalOrdenes();

        int largasExitosas = statsTransportista.getLargasExitosas();

        int mediasExitosas = statsTransportista.getMediasExitosas();

        int cortasExitosas = statsTransportista.getCortasExitosas();

        int totalIncidencias = 0;
        if (idTransportista != 0) {
            totalIncidencias = (int) incidenciaRepository.countByTransportistaId(idTransportista);
        }

        return new StatsTransportistaResponseDTO(
            idTransportista,
            totalOrdenes, 
            largasExitosas + mediasExitosas + cortasExitosas, 
            totalIncidencias);

    }

    public StatsSistemaResponseDTO getStatsSistema() {

        StatsSistema statsSistema = statsSistemaRepository.findAll().stream().findFirst().orElse(null);

        int totalOrdenes = statsSistema.getTotalOrdenes();

        int totalEntregadas = statsSistema.getLargasExitosas() + statsSistema.getMediasExitosas() + statsSistema.getCortasExitosas();

        int totalCanceladas = totalOrdenes - totalEntregadas;

        int totalEnCurso = (int) (
            ordenCargaRepository.count()
            - ordenCargaRepository.countByEstadoOrdenCarga_Id(3)
            - ordenCargaRepository.countByEstadoOrdenCarga_Id(4)
        );

        return new StatsSistemaResponseDTO(
            totalOrdenes,
            totalEntregadas,
            totalCanceladas,
            totalEnCurso
        );

    }

}
