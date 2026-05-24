package com.blackmesaresearch.hytrac.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blackmesaresearch.hytrac.dto.request.CancelarOrdenRequestDTO;
import com.blackmesaresearch.hytrac.dto.request.OrdenCargaRequestDTO;
import com.blackmesaresearch.hytrac.dto.request.ConfirmarEntregaRequestDTO;
import com.blackmesaresearch.hytrac.dto.response.OrdenCargaDetalleResponseDTO;
import com.blackmesaresearch.hytrac.dto.response.OrdenCargaResponseDTO;
import com.blackmesaresearch.hytrac.dto.response.OrdenSupervisorDetalleResponseDTO;
import com.blackmesaresearch.hytrac.dto.response.OrdenSupervisorResponseDTO;
import com.blackmesaresearch.hytrac.model.core.AuditoriaEstado;
import com.blackmesaresearch.hytrac.model.core.OrdenCarga;
import com.blackmesaresearch.hytrac.model.core.Usuario;
import com.blackmesaresearch.hytrac.model.lookup.EstadoOrdenCarga;
import com.blackmesaresearch.hytrac.repository.AcopladoRepository;
import com.blackmesaresearch.hytrac.repository.AuditoriaEstadoRepository;
import com.blackmesaresearch.hytrac.repository.CombustibleRepository;
import com.blackmesaresearch.hytrac.repository.EstadoOrdenCargaRepository;
import com.blackmesaresearch.hytrac.repository.LugarOperativoRepository;
import com.blackmesaresearch.hytrac.repository.OrdenCargaRepository;
import com.blackmesaresearch.hytrac.repository.TransportistaRepository;
import com.blackmesaresearch.hytrac.repository.UsuarioRepository;
import com.blackmesaresearch.hytrac.repository.VehiculoRepository;

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
    private com.blackmesaresearch.hytrac.repository.IncidenciaRepository incidenciaRepository;
    @Autowired
    private com.blackmesaresearch.hytrac.repository.TipoIncidenciaRepository tipoIncidenciaRepository;
    private static final List<String> MOTIVOS_CANCELACION = List.of("DEMORA", "ACCIDENTE", "DOCUMENTACION");

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

        // =========================
        // GUARDAR
        // =========================
        OrdenCarga guardada = ordenCargaRepository.save(orden);

        return toResponseDTO(guardada);
    }

    private OrdenCargaResponseDTO toResponseDTO(OrdenCarga orden) {
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
                orden.getPlantaDespacho().getNombre(),
                orden.getEstacionDestino().getNombre(),
                orden.getFechaCreacion(),
                orden.getFechaEntregaEstimada(),
                orden.getConfirmado()
        ))
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
                .orElseThrow(() -> new IllegalArgumentException(
                "Orden no encontrada."));

        orden.setConfirmado(true);

        ordenCargaRepository.save(orden);
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
                    "No se puede editar una orden que ya se encuentra en estado '" + estadoActual + "'.");
        }

        // Validar unico Remito y COT excluyendo la orden actual
        ordenCargaRepository.findByNumeroRemito(dto.numeroRemito()).ifPresent(o -> {
            if (!o.getId().equals(id)) {
                throw new IllegalArgumentException("El número de remito ya existe en otra orden del sistema.");
            }
        });

        ordenCargaRepository.findByCot(dto.cot()).ifPresent(o -> {
            if (!o.getId().equals(id)) {
                throw new IllegalArgumentException("El COT ya existe en otra orden del sistema.");
            }
        });

        // Validaciones de formatos y posibles valores contradicctorios //
        if (dto.litrosCargados() == null || dto.litrosCargados() <= 0) {
            throw new IllegalArgumentException("Los litros cargados son obligatorios y deben ser mayores a cero.");
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

        // Reglas de Negocio Cruzadas
        if (!camion.getEmpresa().getId().equals(acoplado.getEmpresa().getId())) {
            throw new IllegalArgumentException("El camión y el acoplado pertenecen a empresas distintas.");
        }

        if (dto.litrosCargados() > acoplado.getCapacidadMaximaLitros()) {
            throw new IllegalArgumentException("Los litros cargados superan la capacidad máxima del acoplado.");
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

        // Guardar la orden modificada
        OrdenCarga modificada = ordenCargaRepository.save(orden);

        return toResponseDTO(modificada);
    }

    public OrdenCargaResponseDTO cancelarOrden(String numeroRemito, CancelarOrdenRequestDTO dto) {

        // Buscar la orden por número de remito
        OrdenCarga orden = ordenCargaRepository.findByNumeroRemito(numeroRemito)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada con el remito: " + numeroRemito));

        //  Validar que la orden no este en un estado final irreversible
        String estadoActual = orden.getEstadoOrdenCarga().getNombre();
        if (estadoActual.equalsIgnoreCase("Entregada") || estadoActual.equalsIgnoreCase("Cancelada")) {
            throw new IllegalArgumentException(
                    "No se puede gestionar la cancelación de una orden que ya se encuentra en estado '" + estadoActual + "'.");
        }

        //  Buscar el usuario que realiza la acción
        Usuario solicitante = usuarioRepository.findByLegajo(dto.legajo())
                .orElseThrow(() -> new IllegalArgumentException("Usuario solicitante no encontrado."));

        String rol = solicitante.getRol().getNombre();

        // =========================================================================
        // FLUJO 1: EL TRANSPORTISTA SOLICITA LA CANCELACIÓN
        // =========================================================================
        if (rol.equalsIgnoreCase("TRANSPORTISTA")) {
            
                // 3 motivos predefinidos de cancelación, el chofer debe elegir uno al solicitar la cancelación. Validamos que haya enviado un motivo válido dentro de las opciones predefinidas.
           boolean esMotivoValido = MOTIVOS_CANCELACION.stream()
                    .anyMatch(m -> m.equalsIgnoreCase(dto.motivo()));
                    
            if (!esMotivoValido) {
                throw new IllegalArgumentException("El motivo de cancelación no es válido. Debe ser uno de: " + MOTIVOS_CANCELACION);
            }

            // Verificar si ya existe una solicitud de cancelación abierta para esta orden
            boolean tieneIncidenciaAbierta = incidenciaRepository.findAll().stream()
                    .anyMatch(i -> i.getOrden().getNumeroRemito().equals(orden.getNumeroRemito())
                    && !i.getResuelto()
                    && MOTIVOS_CANCELACION.stream().anyMatch(m -> m.equalsIgnoreCase(i.getDescripcion())));

            if (tieneIncidenciaAbierta) {
                throw new IllegalArgumentException("Ya existe una solicitud de cancelación pendiente para esta orden.");
            }

            // Buscamos el tipo de incidencia de tu base de datos para asociarlo a esta solicitud de cancelación. Si no existe, se asigna null (podría mejorarse creando un tipo específico para cancelaciones)
           var tipoIncidencia = tipoIncidenciaRepository.findAll().stream()
                    .filter(t -> t.getNombre().equalsIgnoreCase(dto.motivo()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("El tipo de incidencia '" + dto.motivo() + "' no está dado de alta en el sistema."));

            // Crear y guardar la Incidencia asociada a esta solicitud de cancelación (La incidencia quedará abierta para que el supervisor la gestione desde su panel)
            com.blackmesaresearch.hytrac.model.core.Incidencia nuevaIncidencia = new com.blackmesaresearch.hytrac.model.core.Incidencia();
            nuevaIncidencia.setOrden(orden);
            nuevaIncidencia.setUsuarioRegistro(solicitante);
            nuevaIncidencia.setTipoIncidencia(tipoIncidencia);
            nuevaIncidencia.setDescripcion("SOLICITUD DE CANCELACIÓN - Motivo chofer: " + dto.motivo());
            nuevaIncidencia.setFechaIncidente(java.time.LocalDateTime.now());
            nuevaIncidencia.setResuelto(false); // Queda abierta para el supervisor

            incidenciaRepository.save(nuevaIncidencia);

            // Marcamos provisionalmente confirmado como false para alertar al supervisor en su panel
            OrdenCarga ordenGuardada = ordenCargaRepository.save(orden);

            return toResponseDTO(ordenGuardada);
        } // =========================================================================
        // FLUJO 2: EL ADMIN(Mas adelante supervisor/cambiar) ACEPTA O RECHAZA
        //  Modificar/preguntar a gonza
        // =========================================================================
        else if (rol.equalsIgnoreCase("ADMIN")) {

            // Buscar la incidencia de cancelación abierta previamente por el transportista
            com.blackmesaresearch.hytrac.model.core.Incidencia incidenciaPendiente = incidenciaRepository.findAll().stream()
                    .filter(i -> i.getOrden().getNumeroRemito().equals(orden.getNumeroRemito()) 
                        && !i.getResuelto())
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("No hay ninguna solicitud de cancelación pendiente de transportista para esta orden."));
            // CASO A: El supervisor RECHAZA la cancelación del chofer
            if (dto.motivo() != null && (dto.motivo().equalsIgnoreCase("RECHAZADO"))) {

                // Se resuelve la incidencia (La incidencia resolvió)
                incidenciaPendiente.setResuelto(true);
                incidenciaPendiente.setUsuarioGestion(solicitante);
                incidenciaPendiente.setFechaResolucion(java.time.LocalDateTime.now());
                incidenciaPendiente.setAccionesTomadas("Solicitud de cancelación RECHAZADA por el supervisor. El viaje debe continuar.");
                incidenciaRepository.save(incidenciaPendiente);

                // Volvemos a dejar la orden disponible/confirmada para operar con normalidad
                OrdenCarga ordenGuardada = ordenCargaRepository.save(orden);

                return toResponseDTO(ordenGuardada);
            } // CASO B: El supervisor CONFIRMA la cancelación
            else {
                // Buscar el estado "Cancelada"
                EstadoOrdenCarga estadoCancelada = estadoOrdenCargaRepository.findByNombre("Cancelada")
                        .orElseThrow(() -> new IllegalArgumentException("Estado 'Cancelada' no encontrado en el sistema."));

                EstadoOrdenCarga estadoAnterior = orden.getEstadoOrdenCarga();

                // Actualizar la orden a estado Cancelada definitivamente
                orden.setEstadoOrdenCarga(estadoCancelada);
                OrdenCarga ordenActualizada = ordenCargaRepository.save(orden);

                // Registrar en el historial de auditoría de estados de la orden
                AuditoriaEstado auditoria = new AuditoriaEstado();
                auditoria.setOrden(ordenActualizada);
                auditoria.setEstadoAnterior(estadoAnterior);
                auditoria.setEstadoNuevo(estadoCancelada);
                auditoria.setFechaCambio(java.time.LocalDateTime.now());
                auditoria.setSolicitante(solicitante);
                auditoria.setMotivo("Cancelación aprobada por supervisor. Notas: " + dto.motivo());
                auditoriaEstadoRepository.save(auditoria);

                // Se resuelve la incidencia (La incidencia resolvio)
                incidenciaPendiente.setResuelto(true);
                incidenciaPendiente.setUsuarioGestion(solicitante);
                incidenciaPendiente.setFechaResolucion(java.time.LocalDateTime.now());
                incidenciaPendiente.setAccionesTomadas("Cancelación CONFIRMADA por supervisor. Orden dada de baja del sistema.");
                incidenciaRepository.save(incidenciaPendiente);

                return toResponseDTO(ordenActualizada);
            }
        } // OTRO ROL NO INGRESA A ESTE FLUJO
        else {
            throw new IllegalArgumentException("Su rol no está autorizado para realizar o gestionar solicitudes de cancelación.");
        }
    }

    public void reportarEntrega(
    Integer ordenId,
    ConfirmarEntregaRequestDTO dto
) {

    OrdenCarga orden =
        ordenCargaRepository.findById(ordenId)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "Orden no encontrada."
                )
            );

    // =========================
    // VALIDAR ESTADO
    // =========================

    if (!orden.getEstadoOrdenCarga()
        .getNombre()
        .equalsIgnoreCase(
            "Pendiente de confirmacion de entrega"
        )) {

        throw new IllegalArgumentException(
            "La orden no está pendiente de confirmación de entrega."
        );
    }

    // =========================
    // VALIDAR LITROS
    // =========================

    if (dto.litrosEntregados() == null
        || dto.litrosEntregados() <= 0) {

        throw new IllegalArgumentException(
            "Los litros entregados son obligatorios."
        );
    }

    // =========================
    // ACTUALIZAR DATOS
    // =========================

    orden.setLitrosEntregados(
        dto.litrosEntregados()
    );

    orden.setObservaciones(
        dto.observaciones()
    );

    orden.setFechaEntregaReal(
        java.time.LocalDateTime.now()
    );

    ordenCargaRepository.save(orden);
}
}
