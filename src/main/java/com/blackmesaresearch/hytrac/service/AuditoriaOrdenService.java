package com.blackmesaresearch.hytrac.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.blackmesaresearch.hytrac.dto.response.AuditoriaOrdenResponseDTO;
import com.blackmesaresearch.hytrac.model.core.AuditoriaEstado;
import com.blackmesaresearch.hytrac.model.core.OrdenCarga;
import com.blackmesaresearch.hytrac.model.core.Usuario;
import com.blackmesaresearch.hytrac.model.lookup.EstadoOrdenCarga;
import com.blackmesaresearch.hytrac.repository.AuditoriaEstadoRepository;
import com.blackmesaresearch.hytrac.repository.OrdenCargaRepository;
import com.blackmesaresearch.hytrac.repository.UsuarioRepository;
import com.blackmesaresearch.hytrac.repository.EstadoOrdenCargaRepository;

@Service
public class AuditoriaOrdenService {

    private final AuditoriaEstadoRepository auditoriaEstadoRepository;
    private final OrdenCargaRepository ordenCargaRepository;
    private final EstadoOrdenCargaRepository estadoOrdenCargaRepository;
    private final UsuarioRepository usuarioRepository;

    public AuditoriaOrdenService(
            AuditoriaEstadoRepository auditoriaEstadoRepository,
            OrdenCargaRepository ordenCargaRepository,
            EstadoOrdenCargaRepository estadoOrdenCargaRepository,
            UsuarioRepository usuarioRepository) {

        this.auditoriaEstadoRepository = auditoriaEstadoRepository;
        this.ordenCargaRepository = ordenCargaRepository;
        this.estadoOrdenCargaRepository = estadoOrdenCargaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // =========================
    // REGISTRAR AUDITORIA
    // =========================

    public void registrarCambioEstado(
            String numeroRemito,
            String estadoAnteriorNombre,
            String estadoNuevoNombre,
            String solicitanteLegajo,
            String confirmadorLegajo,
            String motivo) {

        OrdenCarga orden = ordenCargaRepository
                .findByNumeroRemito(numeroRemito)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Orden no encontrada."));

        EstadoOrdenCarga estadoAnterior = estadoOrdenCargaRepository
                .findByNombre(estadoAnteriorNombre)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Estado anterior no encontrado."));

        EstadoOrdenCarga estadoNuevo = estadoOrdenCargaRepository
                .findByNombre(estadoNuevoNombre)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Estado nuevo no encontrado."));

        Usuario solicitante = null;
        if (solicitanteLegajo != null) {
            solicitante = usuarioRepository
                    .findByLegajo(solicitanteLegajo)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Solicitante no encontrado."));
        }

        Usuario confirmador = null;
        if (confirmadorLegajo != null) {
            confirmador = usuarioRepository
                    .findByLegajo(confirmadorLegajo)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Confirmador no encontrado."));
        }

        AuditoriaEstado auditoria = new AuditoriaEstado();

        auditoria.setOrden(orden);
        auditoria.setEstadoAnterior(estadoAnterior);
        auditoria.setEstadoNuevo(estadoNuevo);
        auditoria.setFechaCambio(LocalDateTime.now());
        auditoria.setSolicitante(solicitante);
        auditoria.setConfirmador(confirmador);
        auditoria.setMotivo(motivo);

        auditoriaEstadoRepository.save(auditoria);
    }

    // =========================
    // OBTENER TODA LA AUDITORIA
    // =========================

    public List<AuditoriaOrdenResponseDTO> obtenerAuditoria() {

        return auditoriaEstadoRepository
                .findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // =========================
    // OBTENER AUDITORIA POR REMITO
    // =========================

    public List<AuditoriaOrdenResponseDTO> obtenerPorNumeroRemito(
            String numeroRemito) {

        List<AuditoriaOrdenResponseDTO> auditorias =

                auditoriaEstadoRepository
                        .findAll()
                        .stream()
                        .filter(a ->
                                a.getOrden() != null &&
                                a.getOrden().getNumeroRemito().equals(numeroRemito))
                        .map(this::toDTO)
                        .toList();

        if (auditorias.isEmpty()) {

            throw new IllegalArgumentException(
                    "No se encontraron auditorías para el remito.");
        }

        return auditorias;
    }

    // =========================
    // MAPPER
    // =========================

    private AuditoriaOrdenResponseDTO toDTO(
            AuditoriaEstado auditoria) {

        return new AuditoriaOrdenResponseDTO(

                auditoria.getOrden().getNumeroRemito(),

                auditoria.getEstadoAnterior().getNombre(),

                auditoria.getEstadoNuevo().getNombre(),

                auditoria.getFechaCambio(),

                auditoria.getSolicitante() != null
                        ? auditoria.getSolicitante().getLegajo()
                        : null,

                auditoria.getConfirmador() != null
                        ? auditoria.getConfirmador().getLegajo()
                        : null,

                auditoria.getMotivo());
    }
}