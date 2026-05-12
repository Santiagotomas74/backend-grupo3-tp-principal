package com.blackmesaresearch.hytrac.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.blackmesaresearch.hytrac.model.core.OrdenCarga;
import com.blackmesaresearch.hytrac.repository.*;
import com.blackmesaresearch.hytrac.dto.request.OrdenCargaRequestDTO;
import com.blackmesaresearch.hytrac.dto.response.OrdenCargaResponseDTO;
import com.blackmesaresearch.hytrac.dto.response.OrdenCargaDetalleResponseDTO;

@Service
public class OrdenCargaService {

    @Autowired private OrdenCargaRepository ordenCargaRepository;
    @Autowired private EstadoOrdenCargaRepository estadoOrdenCargaRepository;
    @Autowired private VehiculoRepository vehiculoRepository;
    @Autowired private TransportistaRepository transportistaRepository;
    @Autowired private LugarOperativoRepository lugarOperativoRepository;
    @Autowired private CombustibleRepository combustibleRepository;



    public List<OrdenCargaResponseDTO> obtenerTodas() {
        return ordenCargaRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public OrdenCargaResponseDTO guardarNuevaOrdenCarga(OrdenCargaRequestDTO dto) {
        if (ordenCargaRepository.findByNumeroRemito(dto.numeroRemito()).isPresent()) {
            throw new IllegalArgumentException("El número de remito ya existe en el sistema.");
        }

        if (ordenCargaRepository.findByCot(dto.cot()).isPresent()) {
            throw new IllegalArgumentException("El COT ya existe en el sistema.");
        }

        var estadoPendiente = estadoOrdenCargaRepository.findByNombre("Pendiente")
            .orElseThrow(() -> new RuntimeException("El estado 'PENDIENTE' no existe en la base de datos."));

        var camion = vehiculoRepository.findById(dto.camionId())
            .orElseThrow(() -> new IllegalArgumentException("Camión no encontrado."));

        var transportista = transportistaRepository.findById(dto.transportistaId())
            .orElseThrow(() -> new IllegalArgumentException("Transportista no encontrado."));

        var plantaDespacho = lugarOperativoRepository.findById(dto.plantaDespachoId())
            .orElseThrow(() -> new IllegalArgumentException("Planta de despacho no encontrada."));

        var estacionDestino = lugarOperativoRepository.findById(dto.estacionDestinoId())
            .orElseThrow(() -> new IllegalArgumentException("Estación destino no encontrada."));

        var combustible = combustibleRepository.findById(dto.combustibleId())
            .orElseThrow(() -> new IllegalArgumentException("Combustible no encontrado."));

        OrdenCarga orden = new OrdenCarga();
        orden.setNumeroRemito(dto.numeroRemito());
        orden.setCot(dto.cot());
        orden.setCamion(camion);
        orden.setTransportista(transportista);
        orden.setPlantaDespacho(plantaDespacho);
        orden.setEstacionDestino(estacionDestino);
        orden.setCombustible(combustible);
        orden.setLitrosCargados(dto.litrosCargados());
        orden.setFechaEntregaEstimada(dto.fechaEntrega());
        orden.setObservaciones(dto.observaciones());
        orden.setEstadoOrdenCarga(estadoPendiente);

        OrdenCarga guardada = ordenCargaRepository.save(orden);
        return toResponseDTO(guardada);
    }

    private OrdenCargaResponseDTO toResponseDTO(OrdenCarga orden) {
        return new OrdenCargaResponseDTO(
            orden.getId(),
            orden.getNumeroRemito(),
            orden.getCot(),
            orden.getEstadoOrdenCarga().getNombre(),
            orden.getCombustible().getNombre(),
            orden.getTransportista().getUsuario().getNombre(),
            orden.getPlantaDespacho().getNombre(),
            orden.getEstacionDestino().getNombre(),
            orden.getLitrosCargados(),
            orden.getFechaCreacion(),
            orden.getFechaEntregaEstimada()
        );
    }

    public OrdenCargaDetalleResponseDTO obtenerDetallePorId(Integer id) {

    OrdenCarga orden = ordenCargaRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

    return new OrdenCargaDetalleResponseDTO(
        orden.getId(),
        orden.getNumeroRemito(),
        orden.getCot(),

        orden.getEstadoOrdenCarga().getNombre(),

        orden.getCamion() != null ? orden.getCamion().getPatente() : null,
        orden.getAcoplado() != null ? orden.getAcoplado().getPatente() : null,

        orden.getTransportista().getUsuario().getNombre(),
        orden.getCombustible().getNombre(),

        orden.getPlantaDespacho().getNombre(),
        orden.getEstacionDestino().getNombre(),

        orden.getLitrosCargados(),
        orden.getLitrosEntregados(),

        orden.getFechaSalidaPlanta(),
        orden.getFechaEntregaEstimada(),
        orden.getFechaEntregaReal(),

        orden.getObservaciones()
    );
}
}