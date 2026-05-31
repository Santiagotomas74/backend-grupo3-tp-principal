package com.blackmesaresearch.hytrac.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blackmesaresearch.hytrac.dto.request.CancelarOrdenRequestDTO;
import com.blackmesaresearch.hytrac.dto.request.ConfirmarEntregaRequestDTO;
import com.blackmesaresearch.hytrac.dto.request.OrdenCargaRequestDTO;
import com.blackmesaresearch.hytrac.dto.response.OrdenCargaDetalleResponseDTO;
import com.blackmesaresearch.hytrac.dto.response.OrdenCargaResponseDTO;
import com.blackmesaresearch.hytrac.dto.response.OrdenSupervisorDetalleResponseDTO;
import com.blackmesaresearch.hytrac.dto.response.OrdenSupervisorResponseDTO;
import com.blackmesaresearch.hytrac.model.core.AuditoriaEstado;
import com.blackmesaresearch.hytrac.model.core.Incidencia;
import com.blackmesaresearch.hytrac.model.core.OrdenCarga;
import com.blackmesaresearch.hytrac.model.core.Usuario;
import com.blackmesaresearch.hytrac.model.lookup.EstadoOrdenCarga;
import com.blackmesaresearch.hytrac.repository.AcopladoRepository;
import com.blackmesaresearch.hytrac.repository.AuditoriaEstadoRepository;
import com.blackmesaresearch.hytrac.repository.CombustibleRepository;
import com.blackmesaresearch.hytrac.repository.EstadoOrdenCargaRepository;
import com.blackmesaresearch.hytrac.repository.LugarOperativoRepository;
import com.blackmesaresearch.hytrac.repository.OrdenCargaRepository;
import com.blackmesaresearch.hytrac.repository.RutaRepository;
import com.blackmesaresearch.hytrac.repository.TransportistaRepository;
import com.blackmesaresearch.hytrac.repository.UsuarioRepository;
import com.blackmesaresearch.hytrac.repository.VehiculoRepository;
import com.blackmesaresearch.hytrac.service.AuditoriaOrdenService;

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
        @Autowired
        private AuditoriaEstadoRepository auditoriaEstadoRepository;
        @Autowired
        private AuditoriaOrdenService auditoriaOrdenService;
        @Autowired
        private com.blackmesaresearch.hytrac.repository.IncidenciaRepository incidenciaRepository;
        @Autowired
        private com.blackmesaresearch.hytrac.repository.TipoIncidenciaRepository tipoIncidenciaRepository;
        private static final List<String> MOTIVOS_CANCELACION = List.of("DEMORA", "ACCIDENTE", "DOCUMENTACION");
        @Autowired
        private RutaRepository rutaRepository;

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

      public void confirmarOrden(Integer id) {

    OrdenCarga orden = ordenCargaRepository.findById(id)
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "Orden no encontrada."));

    String estadoAnterior =
            orden.getEstadoOrdenCarga().getNombre();

    EstadoOrdenCarga estadoConfirmado =
            estadoOrdenCargaRepository
                    .findByNombre("Confirmado")
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Estado Confirmado no encontrado."));

    orden.setEstadoOrdenCarga(estadoConfirmado);

    ordenCargaRepository.save(orden);

    auditoriaOrdenService.registrarCambioEstado(
            orden.getNumeroRemito(),
            estadoAnterior,
            estadoConfirmado.getNombre(),
            orden.getOperador().getLegajo(), // solicitante
            null,                            // confirmador
            "Supervisor confirmó la orden de envío");
}

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

    // =========================
    // VALIDAR RUTA
    // =========================

    if (orden.getRuta() == null) {

        throw new IllegalArgumentException(
                "No se puede iniciar el viaje porque la orden no tiene una ruta asignada. Por favor, edite la orden y confirme el recorrido.");
    }

    // =========================
    // GUARDAR ESTADO ANTERIOR
    // =========================

    String estadoAnterior =
            orden.getEstadoOrdenCarga().getNombre();

    // =========================
    // OBTENER NUEVO ESTADO
    // =========================

    EstadoOrdenCarga nuevoEstado =
            estadoOrdenCargaRepository
                    .findByNombre("En Curso")
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Estado 'En Curso' no encontrado."));

    // =========================
    // ACTUALIZAR ESTADO
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
            orden.getOperador().getLegajo(),
            null,
            "Supervisor aprobó el inicio del viaje");
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

     public OrdenCargaResponseDTO cancelarOrden(
        String numeroRemito,
        CancelarOrdenRequestDTO dto) {

    // =========================
    // BUSCAR ORDEN
    // =========================

    OrdenCarga orden = ordenCargaRepository
            .findByNumeroRemito(numeroRemito)
            .orElseThrow(() -> new IllegalArgumentException(
                    "Orden no encontrada con el remito: "
                            + numeroRemito));

    // =========================
    // VALIDAR ESTADO
    // =========================

    String estadoActual =
            orden.getEstadoOrdenCarga().getNombre();

    if (estadoActual.equalsIgnoreCase("Entregada")
            || estadoActual.equalsIgnoreCase("Cancelada")) {

        throw new IllegalArgumentException(
                "No se puede gestionar la cancelación de una orden que ya se encuentra en estado '"
                        + estadoActual + "'.");
    }

    // =========================
    // BUSCAR USUARIO
    // =========================

    Usuario solicitante = usuarioRepository
            .findByLegajo(dto.legajo())
            .orElseThrow(() -> new IllegalArgumentException(
                    "Usuario solicitante no encontrado."));

    String rol = solicitante
            .getRol()
            .getNombre();

    // =========================
    // SOLO SUPERVISOR
    // =========================

    if (!rol.equalsIgnoreCase("SUPERVISOR")) {

        throw new IllegalArgumentException(
                "Su rol no está autorizado para realizar o gestionar solicitudes de cancelación.");
    }

    // =========================
    // BUSCAR INCIDENCIA ABIERTA
    // =========================

    Incidencia incidenciaPendiente =
            incidenciaRepository.findAll()
                    .stream()
                    .filter(i ->
                            i.getOrden()
                                    .getNumeroRemito()
                                    .equals(
                                            orden.getNumeroRemito())
                                    &&
                                    !Boolean.TRUE.equals(
                                            i.getResuelto()))
                    .findFirst()
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "No hay ninguna solicitud de cancelación pendiente para esta orden."));

    // ======================================================
    // CASO 1: RECHAZA CANCELACION
    // ======================================================

    if (dto.motivo() != null
            && dto.motivo()
                    .equalsIgnoreCase("RECHAZADO")) {

        incidenciaPendiente.setResuelto(true);

        incidenciaPendiente.setUsuarioGestion(
                solicitante);

        incidenciaPendiente.setFechaResolucion(
                LocalDateTime.now());

        incidenciaPendiente.setAccionesTomadas(
                "Solicitud de cancelación rechazada por supervisor.");

        incidenciaRepository.save(
                incidenciaPendiente);

        return toResponseDTO(
                ordenCargaRepository.save(orden));
    }

    // ======================================================
    // CASO 2: APRUEBA CANCELACION
    // ======================================================

    EstadoOrdenCarga estadoAnterior =
            orden.getEstadoOrdenCarga();

    EstadoOrdenCarga estadoCancelada =
            estadoOrdenCargaRepository
                    .findByNombre("Cancelada")
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Estado 'Cancelada' no encontrado."));

    orden.setEstadoOrdenCarga(
            estadoCancelada);

    OrdenCarga ordenActualizada =
            ordenCargaRepository.save(orden);

    // =========================
    // AUDITORIA
    // =========================

    auditoriaOrdenService.registrarCambioEstado(

            ordenActualizada.getNumeroRemito(),

            estadoAnterior.getNombre(),

            estadoCancelada.getNombre(),

            solicitante.getLegajo(),

            solicitante.getLegajo(),

            "Cancelación aprobada por supervisor. "
                    + dto.motivo());

    // =========================
    // RESOLVER INCIDENCIA
    // =========================

    incidenciaPendiente.setResuelto(true);

    incidenciaPendiente.setUsuarioGestion(
            solicitante);

    incidenciaPendiente.setFechaResolucion(
            LocalDateTime.now());

    incidenciaPendiente.setAccionesTomadas(
            "Cancelación aprobada por supervisor. Orden cancelada.");

    incidenciaRepository.save(
            incidenciaPendiente);

    return toResponseDTO(
            ordenActualizada);
}

        public void reportarEntrega(
                        Integer ordenId,
                        ConfirmarEntregaRequestDTO dto) {

                OrdenCarga orden = ordenCargaRepository.findById(ordenId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Orden no encontrada."));

                // =========================
                // VALIDAR ESTADO
                // =========================

                if (!orden.getEstadoOrdenCarga()
                                .getNombre()
                                .equalsIgnoreCase(
                                                "Pendiente de confirmacion de entrega")) {

                        throw new IllegalArgumentException(
                                        "La orden no está pendiente de confirmación de entrega.");
                }

                // =========================
                // VALIDAR LITROS
                // =========================

                if (dto.litrosEntregados() == null
                                || dto.litrosEntregados() <= 0) {

                        throw new IllegalArgumentException(
                                        "Los litros entregados son obligatorios.");
                }

                // =========================
                // ACTUALIZAR DATOS
                // =========================

                orden.setLitrosEntregados(
                                dto.litrosEntregados());

                orden.setObservaciones(
                                dto.observaciones());

                orden.setFechaEntregaReal(
                                java.time.LocalDateTime.now());

                ordenCargaRepository.save(orden);
        }

    public void confirmarEntrega(Integer ordenId) {

    // =========================
    // OBTENER ORDEN
    // =========================

    OrdenCarga orden = ordenCargaRepository
            .findById(ordenId)
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "Orden no encontrada."
                    )
            );

    // =========================
    // VALIDAR ESTADO ACTUAL
    // =========================

    if (!orden.getEstadoOrdenCarga()
            .getNombre()
            .equalsIgnoreCase(
                    "Pendiente de confirmacion de entrega")) {

        throw new IllegalArgumentException(
                "La orden no está pendiente de confirmación de entrega."
        );
    }

    // =========================
    // GUARDAR ESTADO ANTERIOR
    // =========================

    String estadoAnterior =
            orden.getEstadoOrdenCarga().getNombre();

    // =========================
    // OBTENER ESTADO ENTREGADA
    // =========================

    EstadoOrdenCarga estadoEntregada =
            estadoOrdenCargaRepository
                    .findByNombre("Entregada")
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Estado 'Entregada' no encontrado."
                            )
                    );

    // =========================
    // ACTUALIZAR ESTADO
    // =========================

    orden.setEstadoOrdenCarga(
            estadoEntregada
    );

    ordenCargaRepository.save(orden);

    // =========================
    // AUDITORIA
    // =========================

    auditoriaOrdenService.registrarCambioEstado(
            orden.getNumeroRemito(),
            estadoAnterior,
            estadoEntregada.getNombre(),
            orden.getOperador().getLegajo(),
            null,
            "Supervisor confirmó la entrega"
    );
}
}
