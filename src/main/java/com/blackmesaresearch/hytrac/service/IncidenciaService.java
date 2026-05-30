package com.blackmesaresearch.hytrac.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.blackmesaresearch.hytrac.dto.request.ReportarIncidenciaRequestDTO;
import com.blackmesaresearch.hytrac.dto.response.IncidenciaResponseDTO;
import com.blackmesaresearch.hytrac.model.core.Incidencia;
import com.blackmesaresearch.hytrac.repository.IncidenciaRepository;
import com.blackmesaresearch.hytrac.repository.OrdenCargaRepository;
import com.blackmesaresearch.hytrac.repository.TipoIncidenciaRepository;
import com.blackmesaresearch.hytrac.repository.UsuarioRepository;

@Service
public class IncidenciaService {

        private final IncidenciaRepository incidenciaRepository;

        private final OrdenCargaRepository ordenCargaRepository;

        private final UsuarioRepository usuarioRepository;

        private final TipoIncidenciaRepository tipoIncidenciaRepository;

        public IncidenciaService(

                        IncidenciaRepository incidenciaRepository,

                        OrdenCargaRepository ordenCargaRepository,

                        UsuarioRepository usuarioRepository,

                        TipoIncidenciaRepository tipoIncidenciaRepository

        ) {

                this.incidenciaRepository = incidenciaRepository;

                this.ordenCargaRepository = ordenCargaRepository;

                this.usuarioRepository = usuarioRepository;

                this.tipoIncidenciaRepository = tipoIncidenciaRepository;
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

        // =========================
        // REPORTAR INCIDENCIA
        // =========================

        public void reportarIncidencia(
                        ReportarIncidenciaRequestDTO dto) {

                // =========================
                // OBTENER ORDEN
                // =========================

                var orden = ordenCargaRepository
                                .findByNumeroRemito(dto.numeroRemito())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Orden no encontrada."));

                // =========================
                // OBTENER USUARIO
                // =========================

                var usuario = usuarioRepository
                                .findByLegajo(dto.legajoTransportista())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Usuario no encontrado."));

                // =========================
                // OBTENER TIPO INCIDENCIA
                // =========================

                var tipoIncidencia = tipoIncidenciaRepository
                                .findByNombre(dto.tipoIncidencia())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Tipo de incidencia no encontrado."));

                // =========================
                // CREAR INCIDENCIA
                // =========================

                Incidencia incidencia = new Incidencia();

                incidencia.setOrden(orden);

                incidencia.setUsuarioRegistro(usuario);

                incidencia.setTipoIncidencia(tipoIncidencia);

                incidencia.setDescripcion(
                                dto.descripcion());

                incidencia.setFechaIncidente(
                                LocalDateTime.now());

                incidencia.setResuelto(false);

                incidenciaRepository.save(incidencia);
        }
}