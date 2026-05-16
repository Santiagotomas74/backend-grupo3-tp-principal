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

    @Autowired
    private OrdenCargaRepository ordenCargaRepository;
    @Autowired
    private EstadoOrdenCargaRepository estadoOrdenCargaRepository;
    @Autowired
    private VehiculoRepository vehiculoRepository;
    @Autowired
    private TransportistaRepository transportistaRepository;
    @Autowired
    private LugarOperativoRepository lugarOperativoRepository;
    @Autowired
    private CombustibleRepository combustibleRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private AcopladoRepository acopladoRepository;

    public List<OrdenCargaResponseDTO> obtenerTodas() {
        return ordenCargaRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public OrdenCargaResponseDTO guardarNuevaOrdenCarga(OrdenCargaRequestDTO dto) {

        // =========================
        // VALIDACIONES DE UNICIDAD
        // =========================

        if (ordenCargaRepository.findByNumeroRemito(dto.numeroRemito()).isPresent()) {
            throw new IllegalArgumentException(
                    "El número de remito ya existe en el sistema.");
        }

        if (ordenCargaRepository.findByCot(dto.cot()).isPresent()) {
            throw new IllegalArgumentException(
                    "El COT ya existe en el sistema.");
        }

        // =========================
        // VALIDACIONES BÁSICAS
        // =========================

        if (dto.litrosCargados() == null || dto.litrosCargados() <= 0) {
            throw new IllegalArgumentException(
                    "Los litros cargados son obligatorios.");
        }

        if (dto.plantaDespachoId().equals(dto.estacionDestinoId())) {
            throw new IllegalArgumentException(
                    "La planta de despacho y el destino no pueden ser iguales.");
        }

        // =========================
        // OBTENER ENTIDADES
        // =========================

        var camion = vehiculoRepository.findById(dto.camionId())
                .orElseThrow(() -> new IllegalArgumentException("Camión no encontrado."));

        var acoplado = acopladoRepository.findById(dto.acopladoId())
                .orElseThrow(() -> new IllegalArgumentException("Acoplado no encontrado."));

        var transportista = transportistaRepository.findById(dto.transportistaId())
                .orElseThrow(() -> new IllegalArgumentException("Transportista no encontrado."));

        var plantaDespacho = lugarOperativoRepository.findById(dto.plantaDespachoId())
                .orElseThrow(() -> new IllegalArgumentException("Planta de despacho no encontrada."));

        var estacionDestino = lugarOperativoRepository.findById(dto.estacionDestinoId())
                .orElseThrow(() -> new IllegalArgumentException("Estación destino no encontrada."));

        var operador = usuarioRepository.findById(dto.operadorId())
                .orElseThrow(() -> new IllegalArgumentException("Operador no encontrado."));

        var combustible = combustibleRepository.findById(dto.combustibleId())
                .orElseThrow(() -> new IllegalArgumentException("Combustible no encontrado."));

        var estado = estadoOrdenCargaRepository.findById(dto.estadoId())
                .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado."));

        // =========================
        // VALIDACIONES DE NEGOCIO
        // =========================

        // mismo empresa
        if (!camion.getEmpresa().getId().equals(acoplado.getEmpresa().getId())) {

            throw new IllegalArgumentException(
                    "El camión y el acoplado pertenecen a empresas distintas.");
        }

        // capacidad máxima del acoplado
        if (dto.litrosCargados() > acoplado.getCapacidadMaximaLitros()) {

            throw new IllegalArgumentException(
                    "Los litros cargados superan la capacidad máxima del acoplado.");
        }

        // disponibilidad camión
        if (!camion.getEstado().getNombre().equalsIgnoreCase("Disponible")) {

            throw new IllegalArgumentException(
                    "El camión no está disponible.");
        }

        // disponibilidad acoplado
        if (!acoplado.getEstado().getNombre().equalsIgnoreCase("Disponible")) {

            throw new IllegalArgumentException(
                    "El acoplado no está disponible.");
        }

        // =========================
        // CREAR ORDEN
        // =========================

        OrdenCarga orden = new OrdenCarga();

        orden.setNumeroRemito(dto.numeroRemito());
        orden.setCot(dto.cot());

        orden.setCamion(camion);
        orden.setAcoplado(acoplado);

        orden.setTransportista(transportista);

        orden.setPlantaDespacho(plantaDespacho);
        orden.setEstacionDestino(estacionDestino);

        orden.setOperador(operador);

        orden.setCombustible(combustible);

        orden.setEstadoOrdenCarga(estado);

        orden.setLitrosCargados(dto.litrosCargados());
        orden.setLitrosEntregados(dto.litrosEntregados());

        orden.setFechaCreacion(dto.fechaCreacion());
        orden.setFechaSalidaPlanta(dto.fechaSalidaPlanta());
        orden.setFechaEntregaEstimada(dto.fechaEntrega());

        orden.setTemperaturaCarga(dto.temperatura());
        orden.setDensidadCarga(dto.densidad());

        orden.setObservaciones(dto.observaciones());

        orden.setFieAdjunta(dto.fieAdjunta());
        orden.setConfirmado(dto.confirmado());

        // =========================
        // GUARDAR
        // =========================

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
                orden.getFechaEntregaEstimada());
    }

    public OrdenCargaDetalleResponseDTO obtenerDetallePorId(Integer id) {

        OrdenCarga orden = ordenCargaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada."));

        return new OrdenCargaDetalleResponseDTO(

                orden.getId(),
                orden.getNumeroRemito(),
                orden.getCot(),

                orden.getEstadoOrdenCarga().getNombre(),

                orden.getCamion().getPatente(),
                orden.getAcoplado().getPatente(),

                // NUEVOS DATOS
                orden.getCamion().getPeso_maximo_admitido(),
                orden.getAcoplado().getCapacidadMaximaLitros(),

                orden.getTransportista().getUsuario().getNombre()
                        + " "
                        + orden.getTransportista().getUsuario().getApellido(),

                orden.getCombustible().getNombre(),

                orden.getPlantaDespacho().getNombre(),
                orden.getEstacionDestino().getNombre(),

                orden.getLitrosCargados(),
                orden.getLitrosEntregados(),

                orden.getFechaCreacion(),
                orden.getFechaSalidaPlanta(),
                orden.getFechaEntregaEstimada(),
                orden.getFechaEntregaReal(),

                orden.getObservaciones(),
                orden.getFieAdjunta(),
                orden.getConfirmado(),

                // =========================
                // DATOS COMBUSTIBLE
                // =========================

                orden.getCombustible().getNombre(),
                orden.getCombustible().getNumeroOnu(),
                orden.getCombustible().getClaseRiesgo(),
                orden.getCombustible().getDensidad(),
                orden.getCombustible().getTemperaturaReferencia());
    }
}