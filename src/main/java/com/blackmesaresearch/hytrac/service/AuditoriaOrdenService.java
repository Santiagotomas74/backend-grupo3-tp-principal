package com.blackmesaresearch.hytrac.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.blackmesaresearch.hytrac.dto.response.AuditoriaOrdenResponseDTO;
import com.blackmesaresearch.hytrac.model.core.AuditoriaOrden;
import com.blackmesaresearch.hytrac.repository.AuditoriaOrdenRepository;

@Service
public class AuditoriaOrdenService {

    private final AuditoriaOrdenRepository auditoriaOrdenRepository;

    public AuditoriaOrdenService(
            AuditoriaOrdenRepository auditoriaOrdenRepository) {

        this.auditoriaOrdenRepository =
                auditoriaOrdenRepository;
    }

    // =========================
    // REGISTRAR AUDITORIA
    // =========================

    public void registrarCambioEstado(
            String numeroRemito,
            String estadoAnterior,
            String estadoNuevo,
            String solicitanteLegajo,
            String confirmadorLegajo,
            String motivo) {

        AuditoriaOrden auditoria =
                new AuditoriaOrden();

        auditoria.setOrdenNumeroRemito(
                numeroRemito);

        auditoria.setEstadoAnteriorNombre(
                estadoAnterior);

        auditoria.setEstadoNuevoNombre(
                estadoNuevo);

        auditoria.setFechaCambio(
                LocalDateTime.now());

        auditoria.setSolicitanteLegajo(
                solicitanteLegajo);

        auditoria.setConfirmadorLegajo(
                confirmadorLegajo);

        auditoria.setMotivo(
                motivo);

        auditoriaOrdenRepository.save(
                auditoria);
    }

    // =========================
    // OBTENER TODA LA AUDITORIA
    // =========================

    public List<AuditoriaOrdenResponseDTO>
            obtenerAuditoria() {

        return auditoriaOrdenRepository
                .findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // =========================
    // OBTENER AUDITORIA POR REMITO
    // =========================

    public List<AuditoriaOrdenResponseDTO>
            obtenerPorNumeroRemito(
                    String numeroRemito) {

        List<AuditoriaOrdenResponseDTO> auditorias =

                auditoriaOrdenRepository
                        .findByOrdenNumeroRemito(
                                numeroRemito)
                        .stream()
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
            AuditoriaOrden auditoria) {

        return new AuditoriaOrdenResponseDTO(

                auditoria.getOrdenNumeroRemito(),

                auditoria.getEstadoAnteriorNombre(),

                auditoria.getEstadoNuevoNombre(),

                auditoria.getFechaCambio(),

                auditoria.getSolicitanteLegajo(),

                auditoria.getConfirmadorLegajo(),

                auditoria.getMotivo());
    }
}