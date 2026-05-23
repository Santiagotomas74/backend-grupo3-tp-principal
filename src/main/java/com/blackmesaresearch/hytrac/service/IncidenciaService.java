package com.blackmesaresearch.hytrac.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.blackmesaresearch.hytrac.dto.response.IncidenciaResponseDTO;
import com.blackmesaresearch.hytrac.repository.IncidenciaRepository;

@Service
public class IncidenciaService {

    private final IncidenciaRepository incidenciaRepository;

    public IncidenciaService(
        IncidenciaRepository incidenciaRepository
    ) {
        this.incidenciaRepository = incidenciaRepository;
    }

    // =========================
    // TODAS LAS INCIDENCIAS
    // =========================

    public List<IncidenciaResponseDTO> obtenerTodas() {

        return incidenciaRepository.findAll()
            .stream()
            .map(incidencia -> new IncidenciaResponseDTO(

                incidencia.getId(),

                incidencia.getOrden()
                    .getNumeroRemito(),

                incidencia.getUsuarioRegistro()
                    .getLegajo(),

                incidencia.getUsuarioGestion() != null
                    ? incidencia.getUsuarioGestion().getLegajo()
                    : null,

                incidencia.getTipoIncidencia()
                    .getNombre(),

                incidencia.getDescripcion(),

                incidencia.getFechaIncidente(),

                incidencia.getLeyAplicada(),

                incidencia.getAccionesTomadas(),

                incidencia.getResuelto()

            ))
            .toList();
    }
}