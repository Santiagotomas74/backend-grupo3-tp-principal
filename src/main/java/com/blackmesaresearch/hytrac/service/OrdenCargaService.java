package com.blackmesaresearch.hytrac.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blackmesaresearch.hytrac.dto.request.CancelarOrdenRequestDTO;
import com.blackmesaresearch.hytrac.dto.request.ConfirmarEntregaRequestDTO;
import com.blackmesaresearch.hytrac.dto.request.GenerarCotRequestDTO;
import com.blackmesaresearch.hytrac.dto.request.OrdenCargaRequestDTO;
import com.blackmesaresearch.hytrac.dto.response.CotResponseDTO;
import com.blackmesaresearch.hytrac.dto.response.OrdenCargaDetalleResponseDTO;
import com.blackmesaresearch.hytrac.dto.response.OrdenCargaResponseDTO;
import com.blackmesaresearch.hytrac.dto.response.OrdenSupervisorDetalleResponseDTO;
import com.blackmesaresearch.hytrac.dto.response.OrdenSupervisorResponseDTO;
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
import com.blackmesaresearch.hytrac.util.CotGenerator;
import com.blackmesaresearch.hytrac.service.ArbaService;

 
import java.time.LocalDate;
import java.util.UUID;

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
        @Autowired
        private NotificacionService notificacionService;
        @Autowired
        private CotGenerator cotGenerator;
        @Autowired
        private ArbaService arbaService;

        public List<OrdenCargaResponseDTO> obtenerTodas() {
                return ordenCargaRepository.findAll()
                                .stream()
                                .map(this::toResponseDTO)
                                .toList();
        }

        private String generarNumeroRemito() {

    return "REM-"
            + LocalDate.now().getYear()
            + "-"
            + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
}

      public OrdenCargaResponseDTO guardarNuevaOrdenCarga(
        OrdenCargaRequestDTO dto) {


    // =========================
    // VALIDACIONES BÁSICAS
    // =========================

    if (dto.litrosCargados() == null
            || dto.litrosCargados() <= 0) {

        throw new IllegalArgumentException(
                "Los litros cargados son obligatorios.");
    }


    if (dto.plantaDespachoId()
            .equals(dto.estacionDestinoId())) {

        throw new IllegalArgumentException(
                "La planta de despacho y el destino no pueden ser iguales.");
    }



    // =========================
    // OBTENER ENTIDADES
    // =========================


    var camion = vehiculoRepository.findById(dto.camionId())
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "Camión no encontrado."));


    var acoplado = acopladoRepository.findById(dto.acopladoId())
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "Acoplado no encontrado."));


    var transportista = transportistaRepository.findById(dto.transportistaId())
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "Transportista no encontrado."));


    var plantaDespacho = lugarOperativoRepository.findById(dto.plantaDespachoId())
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "Planta de despacho no encontrada."));


    var estacionDestino = lugarOperativoRepository.findById(dto.estacionDestinoId())
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "Estación destino no encontrada."));


    var operador = usuarioRepository.findById(dto.operadorId())
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "Operador no encontrado."));


    var combustible = combustibleRepository.findById(dto.combustibleId())
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "Combustible no encontrado."));


    var estado = estadoOrdenCargaRepository.findById(dto.estadoId())
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "Estado no encontrado."));


    var ruta = rutaRepository.findById(dto.rutaId())
            .orElse(null);



    // =========================
    // VALIDACIONES NEGOCIO
    // =========================


    if (!camion.getEmpresa()
            .getId()
            .equals(acoplado.getEmpresa().getId())) {

        throw new IllegalArgumentException(
                "El camión y el acoplado pertenecen a empresas distintas.");
    }



    if (dto.litrosCargados()
            > acoplado.getCapacidadMaximaLitros()) {

        throw new IllegalArgumentException(
                "Los litros cargados superan la capacidad máxima del acoplado.");
    }



    if (!camion.getEstado()
            .getNombre()
            .equalsIgnoreCase("Disponible")) {

        throw new IllegalArgumentException(
                "El camión no está disponible.");
    }



    if (!acoplado.getEstado()
            .getNombre()
            .equalsIgnoreCase("Disponible")) {

        throw new IllegalArgumentException(
                "El acoplado no está disponible.");
    }




    // =========================
    // GENERAR REMITO
    // =========================


    String numeroRemito = generarNumeroRemito();



    while(
        ordenCargaRepository
            .findByNumeroRemito(numeroRemito)
            .isPresent()
    ){

        numeroRemito = generarNumeroRemito();

    }



    // =========================
    // GENERAR COT
    // =========================

CotResponseDTO cotResponse =
        arbaService.generarCot(

            new GenerarCotRequestDTO(

                plantaDespacho.getNombre(),

                estacionDestino.getNombre(),

                dto.litrosCargados(),

                combustible.getDensidad(),

                dto.valorMercaderia(),

                combustible.getNombre(),

                numeroRemito
            )
        );



    // =========================
    // CREAR ORDEN
    // =========================


    OrdenCarga orden = new OrdenCarga();



    orden.setTrackingId(
            "HT-" + System.currentTimeMillis()
    );


    orden.setNumeroRemito(
            numeroRemito
    );


    orden.setCot(
            cotResponse.cot()
    );



    orden.setCamion(camion);

    orden.setAcoplado(acoplado);

    orden.setTransportista(transportista);

    orden.setPlantaDespacho(plantaDespacho);

    orden.setEstacionDestino(estacionDestino);

    orden.setOperador(operador);

    orden.setCombustible(combustible);

    orden.setEstadoOrdenCarga(estado);



    orden.setLitrosCargados(
            dto.litrosCargados()
    );


    orden.setLitrosEntregados(
            dto.litrosEntregados()
    );


    orden.setFechaCreacion(
            dto.fechaCreacion()
    );


    orden.setFechaEntregaEstimada(
            dto.fechaEntrega()
    );


    orden.setObservaciones(
            dto.observaciones()
    );


    orden.setFieAdjunta(
            dto.fieAdjunta()
    );


    orden.setConfirmado(
            dto.confirmado()
    );


    orden.setRuta(
            ruta
    );



    // =========================
    // GUARDAR
    // =========================


    OrdenCarga guardada =
            ordenCargaRepository.save(orden);



    // =========================
    // NOTIFICAR JEFE ESTACION
    // =========================


    String legajoJefe =
            usuarioRepository
                .findByRolAndLugarOperativo(
                        "JEFE_ESTACION",
                        guardada.getEstacionDestino()
                )
                .stream()
                .map(Usuario::getLegajo)
                .findFirst()
                .orElse(null);



    if(legajoJefe != null){

        notificacionService.crearNotificacion(
                legajoJefe,
                "Nuevo envío asignado. Remito: "
                + guardada.getNumeroRemito()
        );

    }



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
                                orden.getCodigoConfirmacion(),

                                orden.getConfirmado(),
                                orden.getMotivoRechazo());
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
                                orden.getMotivoRechazo(),
                                orden.getCodigoConfirmacion(),
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

    orden.setConfirmado(true);

    ordenCargaRepository.save(orden);

        // NOTIFICACION AL TRANSPORTISTA
        notificacionService.crearNotificacion(
                orden.getTransportista().getUsuario().getLegajo(),
                "Tu envío con Remito N° " + orden.getNumeroRemito() + 
                " ha sido CONFIRMADO por el operador. Ya puedes iniciar ruta.");

        //NOTIFICACION AL OPERADOR
        if (orden.getOperador() != null) {
                notificacionService.crearNotificacion(
                orden.getOperador().getLegajo(),
                "Envío Confirmado: El Remito N° " + orden.getNumeroRemito() + 
                " fue aprobado en el sistema.");
}
        
}

// Rechazar Orden
public void rechazarOrden(Integer id, String legajoSupervisor, String motivoRechazo) {
        OrdenCarga orden = ordenCargaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada."));

        String estadoActual = orden.getEstadoOrdenCarga().getNombre();

        if (estadoActual.equalsIgnoreCase("Entregada") || estadoActual.equalsIgnoreCase("Cancelada")) {
                throw new IllegalArgumentException(
                                "No se puede rechazar una orden que ya se encuentra en estado '" + estadoActual + "'.");
        }

        // mismo estado, sacamos el true
        orden.setConfirmado(false);
        orden.setMotivoRechazo(motivoRechazo);

        ordenCargaRepository.save(orden);

        auditoriaOrdenService.registrarCambioEstado(
                orden.getNumeroRemito(),
                estadoActual,
                estadoActual, // El estado no cambia, solo se marca como no confirmado
                null,
                legajoSupervisor,
                "Supervisor rechazó la orden. Motivo: " + motivoRechazo);

                //NOTIFICACION PARA EL TRANSPORTISTA
                notificacionService.crearNotificacion(
                    orden.getTransportista().getUsuario().getLegajo(),
                    "ATENCIÓN: Tu envío con Remito N° " + orden.getNumeroRemito() + 
                    " ha sido RECHAZADO por el sistema. Motivo: " + motivoRechazo);

                //NOTIFICACION PARA EL OPERADOR
                if (orden.getOperador() != null) {
                        notificacionService.crearNotificacion(
                        orden.getOperador().getLegajo(),
                        "Envío Rechazado: El Remito N° " + orden.getNumeroRemito() + 
                        " fue cancelado/rechazado en la plataforma.");
}


        }

    public void aprobarInicioViaje(
        Integer id,
        String legajoSupervisor) {

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
    // GENERAR CODIGO DE ENTREGA
    // =========================

    String codigoConfirmacion =
            String.valueOf(
                    100000 + new java.util.Random().nextInt(900000));

    // =========================
    // ACTUALIZAR ORDEN
    // =========================

    orden.setEstadoOrdenCarga(nuevoEstado);

    orden.setCodigoConfirmacion(
            codigoConfirmacion);

    ordenCargaRepository.save(orden);

    // =========================
    // AUDITORIA
    // =========================

    auditoriaOrdenService.registrarCambioEstado(
            orden.getNumeroRemito(),
            estadoAnterior,
            nuevoEstado.getNombre(),
            null,
            legajoSupervisor,
            "Supervisor aprobó el inicio del viaje. Código generado: "
                    + codigoConfirmacion);

        notificacionService.crearNotificacion(
                orden.getTransportista().getUsuario().getLegajo(),
                "El supervisor APROBO el inicio de tu viaje para el Remito N° " + orden.getNumeroRemito() + ".");

}

// Rechazar Inicio de Viaje

public void rechazarInicioViaje(Integer id, String legajoSupervisor, String motivoRechazo) {
        OrdenCarga orden = ordenCargaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada."));
        
        if (!orden.getEstadoOrdenCarga().getNombre().equalsIgnoreCase("Pendiente de inicio de viaje")) {
                throw new IllegalArgumentException("La orden no está pendiente de inicio de viaje.");
        }

        String estadoActual = orden.getEstadoOrdenCarga().getNombre();

        EstadoOrdenCarga estadoPendiente = estadoOrdenCargaRepository.findByNombre("Pendiente")
                .orElseThrow(() -> new IllegalArgumentException("Estado 'Pendiente' no encontrado."));

        // limpiamos salida y rollback
        orden.setEstadoOrdenCarga(estadoPendiente);
        orden.setFechaSalidaPlanta(null);

        ordenCargaRepository.save(orden);

        auditoriaOrdenService.registrarCambioEstado(
                orden.getNumeroRemito(),
                estadoActual,
                estadoPendiente.getNombre(), 
                null,
                legajoSupervisor,
                "Supervisor rechazó el inicio del viaje. Motivo: " + motivoRechazo);

        notificacionService.crearNotificacion(
                orden.getTransportista().getUsuario().getLegajo(),
                "El supervisor RECHAZÓ el inicio de tu viaje para el Remito N° " + orden.getNumeroRemito() +
                ". Motivo: " + motivoRechazo);
                
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
        orden.getTransportista().getUsuario().getNombre(),
        orden.getTransportista().getUsuario().getApellido(),
        orden.getTransportista().getUsuario().getLegajo(),
        orden.getOperador().getLegajo(),

        // estos dos estaban invertidos
        orden.getCodigoConfirmacion(),
        orden.getConfirmado(),

        orden.getMotivoRechazo()
);
        }

        public OrdenCargaResponseDTO editarOrdenCarga(
        Integer id,
        OrdenCargaRequestDTO dto) {


    OrdenCarga orden = ordenCargaRepository.findById(id)
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "Orden no encontrada."));


    // =========================
    // VALIDAR ESTADO
    // =========================

    String estadoActual =
            orden.getEstadoOrdenCarga().getNombre();


    if (estadoActual.equalsIgnoreCase("Entregada")
            || estadoActual.equalsIgnoreCase("Cancelada")) {


        throw new IllegalArgumentException(
                "No se puede editar una orden que ya se encuentra en estado '"
                + estadoActual + "'.");
    }



    // =========================
    // VALIDACIONES BÁSICAS
    // =========================

    if(dto.litrosCargados() == null
            || dto.litrosCargados() <= 0) {


        throw new IllegalArgumentException(
                "Los litros cargados son obligatorios.");
    }






    if(dto.plantaDespachoId()
            .equals(dto.estacionDestinoId())) {


        throw new IllegalArgumentException(
                "La planta de despacho y el destino no pueden ser iguales.");
    }




    // =========================
    // OBTENER ENTIDADES
    // =========================


    var camion =
            vehiculoRepository.findById(dto.camionId())
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "Camión no encontrado."));



    var acoplado =
            acopladoRepository.findById(dto.acopladoId())
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "Acoplado no encontrado."));



    var transportista =
            transportistaRepository.findById(dto.transportistaId())
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "Transportista no encontrado."));



    var plantaDespacho =
            lugarOperativoRepository.findById(dto.plantaDespachoId())
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "Planta no encontrada."));



    var estacionDestino =
            lugarOperativoRepository.findById(dto.estacionDestinoId())
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "Destino no encontrado."));



    var operador =
            usuarioRepository.findById(dto.operadorId())
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "Operador no encontrado."));



    var combustible =
            combustibleRepository.findById(dto.combustibleId())
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "Combustible no encontrado."));



    var estado =
            estadoOrdenCargaRepository.findById(dto.estadoId())
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "Estado no encontrado."));



    var ruta =
            rutaRepository.findById(dto.rutaId())
            .orElse(null);




    // =========================
    // VALIDACIONES NEGOCIO
    // =========================


    if(!camion.getEmpresa()
            .getId()
            .equals(acoplado.getEmpresa().getId())) {


        throw new IllegalArgumentException(
                "El camión y el acoplado pertenecen a empresas distintas.");
    }



    if(dto.litrosCargados()
            > acoplado.getCapacidadMaximaLitros()) {


        throw new IllegalArgumentException(
                "Los litros cargados superan la capacidad máxima.");
    }




    // =========================
    // ACTUALIZAR ORDEN
    // =========================


    // NO tocar:
    // numeroRemito
    // cot


    orden.setCamion(camion);

    orden.setAcoplado(acoplado);

    orden.setTransportista(transportista);

    orden.setPlantaDespacho(plantaDespacho);

    orden.setEstacionDestino(estacionDestino);

    orden.setOperador(operador);

    orden.setCombustible(combustible);

    orden.setEstadoOrdenCarga(estado);


    orden.setLitrosCargados(
            dto.litrosCargados());


    orden.setLitrosEntregados(
            dto.litrosEntregados());




    orden.setFechaSalidaPlanta(
            dto.fechaSalidaPlanta());


    orden.setFechaEntregaEstimada(
            dto.fechaEntrega());


    orden.setObservaciones(
            dto.observaciones());


    orden.setFieAdjunta(
            dto.fieAdjunta());


    orden.setRuta(ruta);



    // limpieza
    orden.setMotivoRechazo(null);

    orden.setConfirmado(false);



    OrdenCarga modificada =
            ordenCargaRepository.save(orden);



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

        // NOTIFICACION TRANSPORTISTA
        notificacionService.crearNotificacion(
                ordenActualizada.getTransportista().getUsuario().getLegajo(),
                "El viaje correspondiente al Remito N° " + ordenActualizada.getNumeroRemito() + " ha sido CANCELADO.");
        
        // NOTIFICACION OPERADOR , SE PUEDE RETIRAR
        // No se pide pero es un envio que se cancelo por algun motivo se informa al operador?
        if(orden.getOperador()!=null){
                notificacionService.crearNotificacion(
                ordenActualizada.getOperador().getLegajo(), 
                "Aviso de Cancelación: Se canceló la llegada del envío con Remito N° "
                + ordenActualizada.getNumeroRemito()
                );
        }        

        //NOTIFICACION JEFE ESTACION
        
        String legajoJefeCancel = usuarioRepository.findByRolAndLugarOperativo("JEFE_ESTACION", ordenActualizada.getEstacionDestino())
                .stream().map(Usuario::getLegajo).findFirst().orElse(null);

        if (legajoJefeCancel != null) {
                notificacionService.crearNotificacion(
                legajoJefeCancel,
                "Aviso de Cancelación: Se aprobó la cancelación del envío con Remito N° "
                + ordenActualizada.getNumeroRemito());
}



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


public void confirmarEntrega(
        Integer ordenId,
        String legajoSupervisor) {

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
        null,
        legajoSupervisor,
        "Supervisor confirmó la entrega"
);
        // NOTIFICACION TRANSPORTISTA
        notificacionService.crearNotificacion(
                orden.getTransportista().getUsuario().getLegajo(),
                "La entrega de tu Remito N° " + orden.getNumeroRemito() + " fue aprobada de manera exitosa.");
        
        // NOTIFICACION JEFE ESTACION
        String legajoJefeEntrega = usuarioRepository.findByRolAndLugarOperativo("JEFE_ESTACION", orden.getEstacionDestino())
                .stream().map(Usuario::getLegajo).findFirst().orElse(null);

        if (legajoJefeEntrega != null) {
                notificacionService.crearNotificacion(
                legajoJefeEntrega,
                "Confirmación de Entrega: El Remito N° " + orden.getNumeroRemito() + 
                " fue recibido y asentado físicamente en el destino.");
}
}

// Rechazar Confirmación de Entrega
public void rechazarEntrega(Integer id, String legajoSupervisor, String motivoRechazo) {
        OrdenCarga orden = ordenCargaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada."));

        if (!orden.getEstadoOrdenCarga().getNombre().equalsIgnoreCase("Pendiente de confirmacion de entrega")) {
                throw new IllegalArgumentException("La orden no está pendiente de confirmación de entrega.");
        }

        String estadoActual = orden.getEstadoOrdenCarga().getNombre();

        EstadoOrdenCarga estadoEnCurso = estadoOrdenCargaRepository.findByNombre("En Curso")
                .orElseThrow(() -> new IllegalArgumentException("Estado 'En Curso' no encontrado."));

        // se anula los datos cargados por el transportista y rollback
        orden.setEstadoOrdenCarga(estadoEnCurso);
        orden.setFechaEntregaReal(null);
        orden.setLitrosEntregados(null);

        ordenCargaRepository.save(orden);

        auditoriaOrdenService.registrarCambioEstado(
                orden.getNumeroRemito(),
                estadoActual,
                estadoEnCurso.getNombre(), 
                null,
                legajoSupervisor,
                "Supervisor rechazó la entrega. Motivo: " + motivoRechazo);

        //NOTIFICACION TRANSPORTISTA
        notificacionService.crearNotificacion(
                orden.getTransportista().getUsuario().getLegajo(),
                "La entrega de tu Remito N° "
                + orden.getNumeroRemito() + 
                " fue rechazada. Motivo: "+ motivoRechazo);
        //NOTIFICACION OPERADOR
        notificacionService.crearNotificacion(
                orden.getOperador().getLegajo(),
                "La entrega del Remito N° " 
                + orden.getNumeroRemito() + 
                " fue rechazada. Motivo :"+ motivoRechazo);     


        }
}
