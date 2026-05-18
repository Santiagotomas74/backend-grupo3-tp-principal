package com.blackmesaresearch.hytrac.service;

import org.springframework.stereotype.Service;

import com.blackmesaresearch.hytrac.dto.response.OrdenTransportistaResponseDTO;
import com.blackmesaresearch.hytrac.model.core.OrdenCarga;
import com.blackmesaresearch.hytrac.repository.EstadoOrdenCargaRepository;
import com.blackmesaresearch.hytrac.repository.OrdenCargaRepository;

@Service
public class TransportistaOrdenService {

    private final OrdenCargaRepository ordenCargaRepository;
    private final EstadoOrdenCargaRepository estadoRepository;

    public TransportistaOrdenService(
        OrdenCargaRepository ordenCargaRepository,
        EstadoOrdenCargaRepository estadoRepository
    ) {
        this.ordenCargaRepository = ordenCargaRepository;
        this.estadoRepository = estadoRepository;
    }

    // =========================
    // OBTENER ORDEN PENDIENTE
    // =========================

    public OrdenTransportistaResponseDTO
    obtenerOrdenPendiente(String legajo) {

        OrdenCarga orden =
            ordenCargaRepository
                .findByTransportista_Usuario_LegajoAndConfirmadoTrueAndEstadoOrdenCarga_Nombre(
                    legajo,
                    "Pendiente"
                )
                .stream()
                .findFirst()
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "No hay órdenes pendientes."
                    )
                );

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

            orden.getConfirmado()
        );
    }

    // =========================
    // INICIAR VIAJE
    // =========================

    public void iniciarViaje(Integer ordenId) {

        OrdenCarga orden =
            ordenCargaRepository.findById(ordenId)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "Orden no encontrada."
                    )
                );

        if (!orden.getEstadoOrdenCarga()
            .getNombre()
            .equalsIgnoreCase("Pendiente")) {

            throw new IllegalArgumentException(
                "La orden no está en estado pendiente."
            );
        }

        var nuevoEstado =
            estadoRepository
                .findByNombre("Pendiente de inicio de viaje")
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "Estado no encontrado."
                    )
                );

        orden.setEstadoOrdenCarga(nuevoEstado);

        orden.setFechaSalidaPlanta(
            java.time.LocalDateTime.now()
        );

        ordenCargaRepository.save(orden);
    }
}