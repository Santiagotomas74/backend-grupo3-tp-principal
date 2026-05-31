package com.blackmesaresearch.hytrac.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blackmesaresearch.hytrac.dto.request.CancelarOrdenRequestDTO;
import com.blackmesaresearch.hytrac.dto.request.ConfirmarEntregaRequestDTO;
import com.blackmesaresearch.hytrac.dto.request.OrdenCargaRequestDTO;
import com.blackmesaresearch.hytrac.dto.response.OrdenCargaDetalleResponseDTO;
import com.blackmesaresearch.hytrac.dto.response.OrdenCargaResponseDTO;
import com.blackmesaresearch.hytrac.dto.response.OrdenSupervisorDetalleResponseDTO;
import com.blackmesaresearch.hytrac.dto.response.OrdenSupervisorResponseDTO;
import com.blackmesaresearch.hytrac.model.core.AuditoriaEstado;
import com.blackmesaresearch.hytrac.model.core.OrdenCarga;
import com.blackmesaresearch.hytrac.model.core.Usuario;
import com.blackmesaresearch.hytrac.model.lookup.EstadoAcoplado;
import com.blackmesaresearch.hytrac.model.lookup.EstadoOrdenCarga;
import com.blackmesaresearch.hytrac.model.lookup.EstadoVehiculo;
import com.blackmesaresearch.hytrac.repository.AcopladoRepository;
import com.blackmesaresearch.hytrac.repository.AuditoriaEstadoRepository;
import com.blackmesaresearch.hytrac.repository.CombustibleRepository;
import com.blackmesaresearch.hytrac.repository.EstadoAcopladoRepository;
import com.blackmesaresearch.hytrac.repository.EstadoOrdenCargaRepository;
import com.blackmesaresearch.hytrac.repository.EstadoVehiculoRepository;
import com.blackmesaresearch.hytrac.repository.LugarOperativoRepository;
import com.blackmesaresearch.hytrac.repository.OrdenCargaRepository;
import com.blackmesaresearch.hytrac.repository.RutaRepository;
import com.blackmesaresearch.hytrac.repository.TransportistaRepository;
import com.blackmesaresearch.hytrac.repository.UsuarioRepository;
import com.blackmesaresearch.hytrac.repository.VehiculoRepository;

@Service
@Transactional(readOnly = true)
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
        @Autowired
        private AuditoriaEstadoRepository auditoriaEstadoRepository;
        @Autowired
        private com.blackmesaresearch.hytrac.repository.IncidenciaRepository incidenciaRepository;
        @Autowired
        private com.blackmesaresearch.hytrac.repository.TipoIncidenciaRepository tipoIncidenciaRepository;
        private static final List<String> MOTIVOS_CANCELACION = List.of("DEMORA", "ACCIDENTE", "DOCUMENTACION");
        @Autowired
        private RutaRepository rutaRepository;
        @Autowired
        private EstadoVehiculoRepository estadoVehiculoRepository;
        @Autowired
        private EstadoAcopladoRepository estadoAcopladoRepository;

        public List<OrdenCargaResponseDTO> obtenerTodas() {
                return ordenCargaRepository.findAll()
                                .stream()
                                .map(this::toResponseDTO)
                                .toList();
        }

        @Transactional
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

                var ruta = rutaRepository.findById(dto.rutaId())
                                .orElse(null); // La ruta es opcional, si no se encuentra se deja null

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

                // disponibilidad transportista
                if (!transportista.isDisponible()) {

                        throw new IllegalArgumentException(
                                        "El transportista no está disponible.");
                }

                // This targets your exact lookups table for vehicle states ("Ocupado", "En
                // Viaje", etc.)
                var estadoVehiculoNoDisponible = estadoVehiculoRepository.findByNombre("No Disponible") // Adjust string
                                                                                                        // to your
                                                                                                        // actual DB
                                                                                                        // record name
                                .orElseThrow(() -> new IllegalStateException(
                                                "Estado de vehículo 'No Disponible' no existe en la base de datos."));

                var estadoAcopladoNoDisponible = estadoAcopladoRepository.findByNombre("No Disponible") // Adjust string
                                                                                                        // to your
                                                                                                        // actual DB
                                                                                                        // record name
                                .orElseThrow(() -> new IllegalStateException(
                                                "Estado de acoplado 'No Disponible' no existe en la base de datos."));

                camion.setEstado(estadoVehiculoNoDisponible);
                acoplado.setEstado(estadoAcopladoNoDisponible);
                transportista.setDisponible(false);

                // =========================
                // CREAR ORDEN
                // =========================
                OrdenCarga orden = new OrdenCarga();

                orden.setTrackingId("HT-" + System.currentTimeMillis()); // Generación simple de tracking_id

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

                orden.setObservaciones(dto.observaciones());

                orden.setFieAdjunta(dto.fieAdjunta());
                orden.setConfirmado(dto.confirmado());

                orden.setRuta(ruta);

                // =========================
                // GUARDAR
                // =========================
                OrdenCarga guardada = ordenCargaRepository.save(orden);

                return toResponseDTO(guardada);
        }

        private OrdenCargaResponseDTO toResponseDTO(
                        OrdenCarga orden) {

                return new OrdenCargaResponseDTO(

                                orden.getId(),
                                orden.getTrackingId(),
                                orden.getNumeroRemito(),
                                orden.getCot(),

                                orden.getEstadoOrdenCarga().getNombre(),
                                orden.getCombustible().getNombre(),

                                orden.getPlantaDespacho().getNombre(),
                                orden.getEstacionDestino().getNombre(),

                                orden.getLitrosCargados(),
                                orden.getLitrosEntregados(),

                                orden.getRuta() != null
                                                ? orden.getRuta().getId()
                                                : null,

                                orden.getFechaCreacion(),
                                orden.getFechaEntregaEstimada(),

                                orden.getCamion().getPatente(),
                                orden.getAcoplado().getPatente(),

                                orden.getTransportista().getUsuario().getNombre(),
                                orden.getTransportista().getUsuario().getApellido(),
                                orden.getTransportista().getUsuario().getLegajo(),

                                orden.getOperador().getLegajo(),

                                orden.getConfirmado());
        }

        public OrdenCargaDetalleResponseDTO obtenerDetallePorId(Integer id) {

                OrdenCarga orden = ordenCargaRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada."));

                return new OrdenCargaDetalleResponseDTO(
                                orden.getId(),
                                orden.getTrackingId(),
                                orden.getNumeroRemito(),
                                orden.getCot(),
                                orden.getEstadoOrdenCarga().getNombre(),
                                orden.getCamion().getPatente(),
                                orden.getAcoplado().getPatente(),
                                // NUEVOS DATOS
                                orden.getCamion().getPeso_maximo_admitido(),
                                orden.getAcoplado().getCapacidadMaximaLitros(),
                                orden.getRuta() != null
                                                ? orden.getRuta().getId()
                                                : null,
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
                                orden.getCombustible().getClaseRiesgo());
        }

        public List<OrdenSupervisorResponseDTO> obtenerTodasSupervisor() {

                return ordenCargaRepository.findAll()
                                .stream()
                                .map(orden -> new OrdenSupervisorResponseDTO(
                                                orden.getId(),
                                                orden.getTrackingId(),
                                                orden.getNumeroRemito(),
                                                orden.getCot(),
                                                orden.getEstadoOrdenCarga().getNombre(),
                                                orden.getCamion().getPatente(),
                                                orden.getAcoplado().getPatente(),
                                                orden.getTransportista()
                                                                .getUsuario()
                                                                .getNombre()
                                                                + " "
                                                                + orden.getTransportista()
                                                                                .getUsuario()
                                                                                .getApellido(),
                                                orden.getCombustible().getNombre(),
                                                orden.getLitrosCargados(),
                                                orden.getLitrosEntregados(),
                                                orden.getPlantaDespacho().getNombre(),
                                                orden.getEstacionDestino().getNombre(),
                                                orden.getObservaciones(),
                                                orden.getFechaCreacion(),
                                                orden.getFechaEntregaEstimada(),
                                                orden.getConfirmado()))
                                .toList();
        }

        public OrdenSupervisorDetalleResponseDTO obtenerOrdenSupervisor(
                        Integer id) {

                OrdenCarga orden = ordenCargaRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Orden no encontrada."));

                return new OrdenSupervisorDetalleResponseDTO(
                                // =========================
                                // ORDEN
                                // =========================

                                orden.getId(),
                                orden.getNumeroRemito(),
                                orden.getCot(),
                                orden.getEstadoOrdenCarga().getNombre(),
                                orden.getConfirmado(),
                                orden.getFieAdjunta(),
                                orden.getObservaciones(),
                                // =========================
                                // FECHAS
                                // =========================

                                orden.getFechaCreacion(),
                                orden.getFechaSalidaPlanta(),
                                orden.getFechaEntregaEstimada(),
                                orden.getFechaEntregaReal(),
                                // =========================
                                // CARGA
                                // =========================

                                orden.getLitrosCargados(),
                                orden.getLitrosEntregados(),
                                // =========================
                                // CAMION
                                // =========================

                                orden.getCamion().getId(),
                                orden.getCamion().getPatente(),
                                orden.getCamion().getMarca(),
                                orden.getCamion().getModelo(),
                                orden.getCamion().getPeso_maximo_admitido(),
                                // =========================
                                // ACOPLADO
                                // =========================

                                orden.getAcoplado().getId(),
                                orden.getAcoplado().getPatente(),
                                orden.getAcoplado().getCapacidadMaximaLitros(),
                                // =========================
                                // TRANSPORTISTA
                                // =========================

                                orden.getTransportista().getId(),
                                orden.getTransportista()
                                                .getUsuario()
                                                .getNombre(),
                                orden.getTransportista()
                                                .getUsuario()
                                                .getApellido(),
                                orden.getTransportista().getCuit(),
                                orden.getTransportista()
                                                .getTipoVinculo()
                                                .getNombre(),
                                // =========================
                                // COMBUSTIBLE
                                // =========================

                                orden.getCombustible().getId(),
                                orden.getCombustible().getNombre(),
                                orden.getCombustible().getNumeroOnu(),
                                orden.getCombustible().getClaseRiesgo(),
                                // =========================
                                // LUGARES
                                // =========================

                                orden.getPlantaDespacho().getNombre(),
                                orden.getEstacionDestino().getNombre(),
                                // =========================
                                // OPERADOR
                                // =========================

                                orden.getOperador().getNombre()
                                                + " "
                                                + orden.getOperador().getApellido());
        }

        @Transactional
        public void confirmarOrden(Integer id) {

                OrdenCarga orden = ordenCargaRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Orden no encontrada."));

                orden.setConfirmado(true);

                ordenCargaRepository.save(orden);
        }

        @Transactional
        public void aprobarInicioViaje(Integer id) {

                OrdenCarga orden = ordenCargaRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Orden no encontrada."));

                // =========================
                // VALIDAR ESTADO ACTUAL
                // =========================
                if (!orden.getEstadoOrdenCarga()
                                .getNombre()
                                .equalsIgnoreCase("Pendiente de inicio de viaje")) {

                        throw new IllegalArgumentException(
                                        "La orden no está pendiente de inicio de viaje.");
                }
                // ==========================================================
                // VALIDACION DE SEGURIDAD ANTES DE SALIR
                // ==========================================================
                if (orden.getRuta() == null) {
                        throw new IllegalArgumentException(
                                        "No se puede iniciar el viaje porque la orden no tiene una ruta asignada. Por favor, edite la orden y confirme el recorrido.");
                }

                // =========================
                // OBTENER NUEVO ESTADO
                // =========================
                EstadoOrdenCarga nuevoEstado = estadoOrdenCargaRepository
                                .findByNombre("En Curso")
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Estado 'En Curso' no encontrado."));

                // =========================
                // ACTUALIZAR
                // =========================
                orden.setEstadoOrdenCarga(nuevoEstado);

                ordenCargaRepository.save(orden);
        }

        public OrdenCargaResponseDTO obtenerPorRemito(
                        String numeroRemito) {

                OrdenCarga orden = ordenCargaRepository
                                .findByNumeroRemito(numeroRemito)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Orden no encontrada."));

                return new OrdenCargaResponseDTO(
                                orden.getId(),
                                orden.getTrackingId(),
                                orden.getNumeroRemito(),
                                orden.getCot(),
                                orden.getEstadoOrdenCarga().getNombre(),
                                orden.getCombustible().getNombre(),
                                orden.getPlantaDespacho().getNombre(),
                                orden.getEstacionDestino().getNombre(),
                                orden.getLitrosCargados(),
                                orden.getLitrosEntregados(),
                                orden.getRuta() != null
                                                ? orden.getRuta().getId()
                                                : null,
                                orden.getFechaCreacion(),
                                orden.getFechaEntregaEstimada(),
                                orden.getCamion().getPatente(),
                                orden.getAcoplado().getPatente(),
                                orden.getTransportista()
                                                .getUsuario()
                                                .getNombre(),
                                orden.getTransportista()
                                                .getUsuario()
                                                .getApellido(),
                                orden.getTransportista()
                                                .getUsuario()
                                                .getLegajo(),
                                orden.getOperador().getLegajo(),
                                orden.getConfirmado());
        }

        @Transactional
        public OrdenCargaResponseDTO editarOrdenCarga(Integer id, OrdenCargaRequestDTO dto) {

                OrdenCarga orden = ordenCargaRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada."));

                // Validar que no este en un estado final e inamovible
                String estadoActual = orden.getEstadoOrdenCarga().getNombre();
                if (estadoActual.equalsIgnoreCase("Entregada") || estadoActual.equalsIgnoreCase("Cancelada")) {
                        throw new IllegalArgumentException(
                                        "No se puede editar una orden que ya se encuentra en estado '" + estadoActual
                                                        + "'.");
                }

                // Validar unico Remito y COT excluyendo la orden actual
                ordenCargaRepository.findByNumeroRemito(dto.numeroRemito()).ifPresent(o -> {
                        if (!o.getId().equals(id)) {
                                throw new IllegalArgumentException(
                                                "El número de remito ya existe en otra orden del sistema.");
                        }
                });

                ordenCargaRepository.findByCot(dto.cot()).ifPresent(o -> {
                        if (!o.getId().equals(id)) {
                                throw new IllegalArgumentException("El COT ya existe en otra orden del sistema.");
                        }
                });

                // Validaciones de formatos y posibles valores contradicctorios //
                if (dto.litrosCargados() == null || dto.litrosCargados() <= 0) {
                        throw new IllegalArgumentException(
                                        "Los litros cargados son obligatorios y deben ser mayores a cero.");
                }

                if (dto.plantaDespachoId().equals(dto.estacionDestinoId())) {
                        throw new IllegalArgumentException("La planta de despacho y el destino no pueden ser iguales.");
                }

                // Obtener Entidades asociadas
                var camion = vehiculoRepository.findById(dto.camionId())
                                .orElseThrow(() -> new IllegalArgumentException("Camión no encontrado."));

                var acoplado = acopladoRepository.findById(dto.acopladoId())
                                .orElseThrow(() -> new IllegalArgumentException("Acoplado no encontrado."));

                // Validar disponibilidad de vehículos SOLO si se cambiaron por unos nuevos
                if (!camion.getId().equals(orden.getCamion().getId())
                                && !camion.getEstado().getNombre().equalsIgnoreCase("Disponible")) {
                        throw new IllegalArgumentException("El nuevo camión seleccionado no está disponible.");
                }

                if (!acoplado.getId().equals(orden.getAcoplado().getId())
                                && !acoplado.getEstado().getNombre().equalsIgnoreCase("Disponible")) {
                        throw new IllegalArgumentException("El nuevo acoplado seleccionado no está disponible.");
                }

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

                var ruta = rutaRepository.findById(dto.rutaId())
                                .orElse(null);
                // Reglas de Negocio Cruzadas
                if (!camion.getEmpresa().getId().equals(acoplado.getEmpresa().getId())) {
                        throw new IllegalArgumentException("El camión y el acoplado pertenecen a empresas distintas.");
                }

                if (dto.litrosCargados() > acoplado.getCapacidadMaximaLitros()) {
                        throw new IllegalArgumentException(
                                        "Los litros cargados superan la capacidad máxima del acoplado.");
                }

                // Mapear y actualizar los campos de la orden existente
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
                orden.setFechaSalidaPlanta(dto.fechaSalidaPlanta());
                orden.setFechaEntregaEstimada(dto.fechaEntrega());
                orden.setObservaciones(dto.observaciones());
                orden.setFieAdjunta(dto.fieAdjunta());
                orden.setConfirmado(dto.confirmado());

                orden.setRuta(ruta);
                // Guardar la orden modificada
                OrdenCarga modificada = ordenCargaRepository.save(orden);

                return toResponseDTO(modificada);
        }

        @Transactional
        public OrdenCargaResponseDTO cancelarOrden(String numeroRemito, CancelarOrdenRequestDTO dto) {

                // ==========================================================
                // 1. FETCH THE TARGET ORDER
                // ==========================================================
                OrdenCarga orden = ordenCargaRepository.findByNumeroRemito(numeroRemito)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Orden no encontrada con el remito: " + numeroRemito));

                // ==========================================================
                // 2. VALIDATE CURRENT STATE
                // ==========================================================
                String estadoActual = orden.getEstadoOrdenCarga().getNombre();
                if (estadoActual.equalsIgnoreCase("Entregada") || estadoActual.equalsIgnoreCase("Cancelada")) {
                        throw new IllegalArgumentException(
                                        "No se puede cancelar una orden que ya se encuentra en estado '" + estadoActual
                                                        + "'.");
                }

                // ==========================================================
                // 3. FETCH THE "DISPONIBLE" LOOKUP STATES
                // ==========================================================
                var estadoVehiculoDisponible = estadoVehiculoRepository.findByNombre("Disponible")
                                .orElseThrow(() -> new IllegalStateException(
                                                "Estado de vehículo 'Disponible' no existe en la base de datos."));

                var estadoAcopladoDisponible = estadoAcopladoRepository.findByNombre("Disponible")
                                .orElseThrow(() -> new IllegalStateException(
                                                "Estado de acoplado 'Disponible' no existe en la base de datos."));

                // ==========================================================
                // 4. EXTRACT ASSETS FROM THE ORDER AND MAKE THEM AVAILABLE
                // ==========================================================
                var camion = orden.getCamion();
                var acoplado = orden.getAcoplado();
                var transportista = orden.getTransportista();

                if (camion != null) {
                        camion.setEstado(estadoVehiculoDisponible);
                        vehiculoRepository.save(camion);
                }

                if (acoplado != null) {
                        acoplado.setEstado(estadoAcopladoDisponible);
                        acopladoRepository.save(acoplado);
                }

                if (transportista != null) {
                        transportista.setDisponible(true);
                        transportistaRepository.save(transportista);
                }

                // ==========================================================
                // 5. UPDATE THE ORDER STATE TO CANCELLED
                // ==========================================================
                EstadoOrdenCarga estadoCancelado = estadoOrdenCargaRepository.findByNombre("Cancelada")
                                .orElseThrow(() -> new IllegalArgumentException("Estado 'Cancelada' no encontrado."));

                orden.setEstadoOrdenCarga(estadoCancelado);

                // Optionally map audit details or reasons from the CancelarOrdenRequestDTO here
                if (dto.motivo() != null) {
                        orden.setObservaciones(orden.getObservaciones() + " | Motivo Cancelación: " + dto.motivo());
                }

                OrdenCarga cancelada = ordenCargaRepository.save(orden);

                return toResponseDTO(cancelada);
        }

        @Transactional
        public OrdenCargaResponseDTO reportarEntrega(Integer id, ConfirmarEntregaRequestDTO dto) {

                // 1. Fetch Order
                OrdenCarga orden = ordenCargaRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Orden no encontrada con el ID: " + id));

                // 2. Validate State (Must be actively traveling)
                String estadoActual = orden.getEstadoOrdenCarga().getNombre();
                if (!estadoActual.equalsIgnoreCase("En Curso")) {
                        throw new IllegalArgumentException(
                                        "Solo se puede reportar la entrega de órdenes que están 'En Curso'. Estado actual: "
                                                        + estadoActual);
                }

                // 3. Fetch Staging State
                EstadoOrdenCarga estadoPendiente = estadoOrdenCargaRepository.findByNombre("Pendiente de Confirmación")
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Estado 'Pendiente de Confirmación' no encontrado."));

                // 4. Update Driver Numbers (Do NOT modify truck/trailer states yet!)
                orden.setEstadoOrdenCarga(estadoPendiente);

                if (dto.litrosEntregados() != null) {
                        orden.setLitrosEntregados(dto.litrosEntregados());
                }
                // Set the delivery timestamp automatically to the current system time
                orden.setFechaEntregaReal(LocalDateTime.now());

                if (dto.observaciones() != null) {
                        orden.setObservaciones(orden.getObservaciones() + " | Reporte Chofer: " + dto.observaciones());
                }

                OrdenCarga reportada = ordenCargaRepository.save(orden);
                return toResponseDTO(reportada);
        }

        @Transactional
        public OrdenCargaResponseDTO confirmarEntrega(Integer id, ConfirmarEntregaRequestDTO dto) {

                // ==========================================================
                // 1. FETCH THE TARGET ORDER
                // ==========================================================
                OrdenCarga orden = ordenCargaRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Orden no encontrada con el ID: " + id));

                // ==========================================================
                // 2. VALIDATE CURRENT STATE
                // ==========================================================
                String estadoActual = orden.getEstadoOrdenCarga().getNombre();
                if (!estadoActual.equalsIgnoreCase("En Curso")) {
                        throw new IllegalArgumentException(
                                        "Solo se puede confirmar la entrega de órdenes que están 'En Curso'. Estado actual: "
                                                        + estadoActual);
                }

                // ==========================================================
                // 3. FETCH THE "DISPONIBLE" LOOKUP STATES
                // ==========================================================
                var estadoVehiculoDisponible = estadoVehiculoRepository.findByNombre("Disponible")
                                .orElseThrow(() -> new IllegalStateException(
                                                "Estado de vehículo 'Disponible' no existe en la base de datos."));

                var estadoAcopladoDisponible = estadoAcopladoRepository.findByNombre("Disponible")
                                .orElseThrow(() -> new IllegalStateException(
                                                "Estado de acoplado 'Disponible' no existe en la base de datos."));

                // ==========================================================
                // 4. FREE UP THE ASSETS
                // ==========================================================
                var camion = orden.getCamion();
                var acoplado = orden.getAcoplado();
                var transportista = orden.getTransportista();

                if (camion != null) {
                        camion.setEstado(estadoVehiculoDisponible);
                        vehiculoRepository.save(camion);
                }

                if (acoplado != null) {
                        acoplado.setEstado(estadoAcopladoDisponible);
                        acopladoRepository.save(acoplado);
                }

                if (transportista != null) {
                        transportista.setDisponible(true);
                        transportistaRepository.save(transportista);
                }

                // ==========================================================
                // 5. UPDATE ORDER DELIVERY DETAILS AND STATE
                // ==========================================================
                EstadoOrdenCarga estadoEntregada = estadoOrdenCargaRepository.findByNombre("Entregada")
                                .orElseThrow(() -> new IllegalArgumentException("Estado 'Entregada' no encontrado."));

                orden.setEstadoOrdenCarga(estadoEntregada);

                // Map payload details from your ConfirmarEntregaRequestDTO
                if (dto.litrosEntregados() != null) {
                        orden.setLitrosEntregados(dto.litrosEntregados());
                }

                // Set the delivery timestamp automatically to the current system time
                orden.setFechaEntregaReal(LocalDateTime.now());

                OrdenCarga entregada = ordenCargaRepository.save(orden);

                return toResponseDTO(entregada);
        }
}
