package com.blackmesaresearch.hytrac.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.blackmesaresearch.hytrac.dto.response.LugarOperativoResponseDTO;
import com.blackmesaresearch.hytrac.model.core.LugarOperativo;
import com.blackmesaresearch.hytrac.repository.LugarOperativoRepository;

@Service
public class LugarOperativoService {

        private final LugarOperativoRepository lugarOperativoRepository;

        public LugarOperativoService(
                        LugarOperativoRepository lugarOperativoRepository) {
                this.lugarOperativoRepository = lugarOperativoRepository;
        }

        // =========================
        // TODAS LAS PLANTAS
        // =========================

        public List<LugarOperativoResponseDTO> obtenerPlantas() {

                return lugarOperativoRepository
                                .findActivosByPuedeDespachar()
                                .stream()
                                .map(this::toResponseDTO)
                                .toList();
        }

        // =========================
        // TODAS LAS ESTACIONES
        // =========================

        public List<LugarOperativoResponseDTO> obtenerEstacionesServicio() {

                return lugarOperativoRepository
                                .findActivosByPuedeRecibir()
                                .stream()
                                .map(this::toResponseDTO)
                                .toList();
        }

        // =========================
        // PLANTAS POR LOCALIDAD
        // =========================

        public List<LugarOperativoResponseDTO> obtenerPlantasPorLocalidad(Integer localidadId) {

                return lugarOperativoRepository
                                .findActivosByPuedeDespachar()
                                .stream()
                                .filter(lugar -> lugar.getLocalidad() != null
                                                &&
                                                lugar.getLocalidad().getId().equals(localidadId))
                                .map(this::toResponseDTO)
                                .toList();
        }

        // =========================
        // ESTACIONES POR LOCALIDAD
        // =========================

        public List<LugarOperativoResponseDTO> obtenerEstacionesPorLocalidad(Integer localidadId) {

                return lugarOperativoRepository
                                .findActivosByPuedeRecibir()
                                .stream()
                                .filter(lugar -> lugar.getLocalidad() != null
                                                &&
                                                lugar.getLocalidad().getId().equals(localidadId))
                                .map(this::toResponseDTO)
                                .toList();
        }

        // =========================
        // DTO
        // =========================

        private LugarOperativoResponseDTO toResponseDTO(LugarOperativo lugarOperativo) {

                Integer localidadId = lugarOperativo.getLocalidad() != null
                                ? lugarOperativo.getLocalidad().getId()
                                : null;

                String localidadNombre = lugarOperativo.getLocalidad() != null
                                ? lugarOperativo.getLocalidad().getNombre()
                                : null;

                Integer provinciaId = lugarOperativo.getLocalidad() != null
                                &&
                                lugarOperativo.getLocalidad().getProvincia() != null
                                                ? lugarOperativo.getLocalidad()
                                                                .getProvincia()
                                                                .getId()
                                                : null;

                String provinciaNombre = lugarOperativo.getLocalidad() != null
                                &&
                                lugarOperativo.getLocalidad().getProvincia() != null
                                                ? lugarOperativo.getLocalidad()
                                                                .getProvincia()
                                                                .getNombre()
                                                : null;

                return new LugarOperativoResponseDTO(

                                lugarOperativo.getId(),

                                lugarOperativo.getNombre(),

                                lugarOperativo.getDireccion(),

                                localidadId,
                                localidadNombre,

                                provinciaId,
                                provinciaNombre,

                                lugarOperativo.getLatitud(),
                                lugarOperativo.getLongitud(),

                                lugarOperativo.getPuedeRecibir(),
                                lugarOperativo.getPuedeDespachar());
        }
}