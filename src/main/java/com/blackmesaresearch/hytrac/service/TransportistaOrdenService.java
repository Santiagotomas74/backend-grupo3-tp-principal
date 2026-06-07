package com.blackmesaresearch.hytrac.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.blackmesaresearch.hytrac.dto.response.OrdenTransportistaResponseDTO;
import com.blackmesaresearch.hytrac.model.core.OrdenCarga;
import com.blackmesaresearch.hytrac.model.lookup.EstadoOrdenCarga;
import com.blackmesaresearch.hytrac.repository.EstadoOrdenCargaRepository;
import com.blackmesaresearch.hytrac.repository.OrdenCargaRepository;


@Service
public class TransportistaOrdenService {

    private final OrdenCargaRepository ordenCargaRepository;

    private final EstadoOrdenCargaRepository estadoRepository;
    private final AuditoriaOrdenService auditoriaOrdenService;

    public TransportistaOrdenService(
            OrdenCargaRepository ordenCargaRepository,
            EstadoOrdenCargaRepository estadoRepository,
            AuditoriaOrdenService auditoriaOrdenService) {
        this.ordenCargaRepository = ordenCargaRepository;
        this.estadoRepository = estadoRepository;
        this.auditoriaOrdenService = auditoriaOrdenService;
    }

    // =========================
    // OBTENER ORDEN PENDIENTE
    // =========================

    public OrdenTransportistaResponseDTO obtenerOrdenPendiente(String legajo) {

        OrdenCarga orden = ordenCargaRepository
                .findByTransportista_Usuario_LegajoAndConfirmadoTrueAndEstadoOrdenCarga_Nombre(
                        legajo,
                        "Pendiente")
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No hay órdenes pendientes."));

        return new OrdenTransportistaResponseDTO(

                orden.getId(),

                orden.getNumeroRemito(),
                orden.getCot(),

                orden.getEstadoOrdenCarga().getNombre(),

                orden.getCamion().getPatente(),
                orden.getAcoplado().getPatente(),

                orden.getCombustible().getNombre(),

                orden.getLitrosCargados(),

                orden.getPlantaDespacho().getNombre(),
                orden.getEstacionDestino().getNombre(),

                orden.getFechaEntregaEstimada(),

                orden.getConfirmado(),
                orden.getRuta() != null ? orden.getRuta().getId() : null
                
                
        );
                
    }

    // =========================
    // INICIAR VIAJE
    // =========================

    public void iniciarViaje(
        Integer ordenId,
        String legajoTransportista) {

    OrdenCarga orden = ordenCargaRepository.findById(ordenId)
            .orElseThrow(() -> new IllegalArgumentException(
                    "Orden no encontrada."));

    if (!orden.getEstadoOrdenCarga()
            .getNombre()
            .equalsIgnoreCase("Pendiente")) {

        throw new IllegalArgumentException(
                "La orden no está en estado pendiente.");
    }

    // =========================
    // ESTADO ANTERIOR
    // =========================

    String estadoAnterior =
            orden.getEstadoOrdenCarga().getNombre();

    // =========================
    // NUEVO ESTADO
    // =========================

    EstadoOrdenCarga nuevoEstado =
            estadoRepository
                    .findByNombre("Pendiente de inicio de viaje")
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Estado no encontrado."));

    // =========================
    // ACTUALIZAR ORDEN
    // =========================

    orden.setEstadoOrdenCarga(nuevoEstado);

    orden.setFechaSalidaPlanta(
            LocalDateTime.now());

    ordenCargaRepository.save(orden);

    // =========================
    // AUDITORIA
    // =========================

    auditoriaOrdenService.registrarCambioEstado(
            orden.getNumeroRemito(),
            estadoAnterior,
            nuevoEstado.getNombre(),
            legajoTransportista,
            null,
            "Transportista inició el viaje");
}

    // =========================
    // OBTENER ORDEN EN CURSO
    // =========================

    public OrdenTransportistaResponseDTO obtenerOrdenEnCurso(String legajo) {

        OrdenCarga orden = ordenCargaRepository
                .findByTransportista_Usuario_LegajoAndConfirmadoTrue(
                        legajo)
                .stream()
                .filter(o -> o.getEstadoOrdenCarga()
                        .getNombre()
                        .equalsIgnoreCase("En Curso"))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No hay órdenes en curso."));

        return new OrdenTransportistaResponseDTO(

                orden.getId(),

                orden.getNumeroRemito(),
                orden.getCot(),

                orden.getEstadoOrdenCarga().getNombre(),

                orden.getCamion().getPatente(),
                orden.getAcoplado().getPatente(),

                orden.getCombustible().getNombre(),

                orden.getLitrosCargados(),

                orden.getPlantaDespacho().getNombre(),
                orden.getEstacionDestino().getNombre(),

                orden.getFechaEntregaEstimada(),

                orden.getConfirmado(),
                orden.getRuta() != null ? orden.getRuta().getId() : null
        );
    }
    // =========================
    // NOTIFICAR ENTREGA
    // =========================
public void notificarEntrega(
        Integer ordenId,
        String legajoTransportista,
        String codigoConfirmacion) {

    OrdenCarga orden = ordenCargaRepository.findById(ordenId)
            .orElseThrow(() -> new IllegalArgumentException(
                    "Orden no encontrada."));

    // =========================
    // VALIDAR ESTADO ACTUAL
    // =========================

    if (!orden.getEstadoOrdenCarga()
            .getNombre()
            .equalsIgnoreCase("En Curso")) {

        throw new IllegalArgumentException(
                "La orden no está en curso.");
    }

    // =========================
    // VALIDAR CODIGO
    // =========================

    if (orden.getCodigoConfirmacion() == null) {

        throw new IllegalArgumentException(
                "La orden no posee un código de confirmación.");
    }

    if (!orden.getCodigoConfirmacion()
            .equals(codigoConfirmacion)) {

        throw new IllegalArgumentException(
                "El código de confirmación es incorrecto.");
    }

    // =========================
    // GUARDAR ESTADO ANTERIOR
    // =========================

    String estadoAnterior =
            orden.getEstadoOrdenCarga().getNombre();

    // =========================
    // NUEVO ESTADO
    // =========================

    EstadoOrdenCarga nuevoEstado =
            estadoRepository
                    .findByNombre(
                            "Pendiente de confirmacion de entrega")
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Estado no encontrado."));

    // =========================
    // ACTUALIZAR
    // =========================

    orden.setEstadoOrdenCarga(nuevoEstado);

    ordenCargaRepository.save(orden);

    // =========================
    // AUDITORIA
    // =========================

    auditoriaOrdenService.registrarCambioEstado(
            orden.getNumeroRemito(),
            estadoAnterior,
            nuevoEstado.getNombre(),
            legajoTransportista,
            null,
            "Transportista notificó la entrega validando el código de confirmación.");
}

}