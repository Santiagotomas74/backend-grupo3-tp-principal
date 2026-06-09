package com.blackmesaresearch.hytrac.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.blackmesaresearch.hytrac.dto.request.CancelarOrdenRequestDTO;
import com.blackmesaresearch.hytrac.dto.request.ConfirmarEntregaRequestDTO;
import com.blackmesaresearch.hytrac.dto.request.OrdenCargaRequestDTO;
import com.blackmesaresearch.hytrac.model.core.Acoplado;
import com.blackmesaresearch.hytrac.model.core.EmpresaTercerizada;
import com.blackmesaresearch.hytrac.model.core.LugarOperativo;
import com.blackmesaresearch.hytrac.model.core.OrdenCarga;
import com.blackmesaresearch.hytrac.model.core.Ruta;
import com.blackmesaresearch.hytrac.model.core.Transportista;
import com.blackmesaresearch.hytrac.model.core.Usuario;
import com.blackmesaresearch.hytrac.model.core.Vehiculo;
import com.blackmesaresearch.hytrac.model.lookup.EstadoOrdenCarga;
import com.blackmesaresearch.hytrac.model.lookup.EstadoVehiculo;
import com.blackmesaresearch.hytrac.model.lookup.Rol;
import com.blackmesaresearch.hytrac.model.reference.Combustible;
import com.blackmesaresearch.hytrac.repository.AcopladoRepository;
import com.blackmesaresearch.hytrac.repository.AuditoriaEstadoRepository;
import com.blackmesaresearch.hytrac.repository.CombustibleRepository;
import com.blackmesaresearch.hytrac.repository.EstadoOrdenCargaRepository;
import com.blackmesaresearch.hytrac.repository.IncidenciaRepository;
import com.blackmesaresearch.hytrac.repository.LugarOperativoRepository;
import com.blackmesaresearch.hytrac.repository.OrdenCargaRepository;
import com.blackmesaresearch.hytrac.repository.RutaRepository;
import com.blackmesaresearch.hytrac.repository.TipoIncidenciaRepository;
import com.blackmesaresearch.hytrac.repository.TransportistaRepository;
import com.blackmesaresearch.hytrac.repository.UsuarioRepository;
import com.blackmesaresearch.hytrac.repository.VehiculoRepository;


@ExtendWith(MockitoExtension.class)
public class OrdenCargaServiceTest {

    @Mock private OrdenCargaRepository ordenCargaRepository;
    @Mock private EstadoOrdenCargaRepository estadoOrdenCargaRepository;
    @Mock private VehiculoRepository vehiculoRepository;
    @Mock private TransportistaRepository transportistaRepository;
    @Mock private LugarOperativoRepository lugarOperativoRepository;
    @Mock private CombustibleRepository combustibleRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private AcopladoRepository acopladoRepository;
    @Mock private RutaRepository rutaRepository;
    @Mock private AuditoriaOrdenService auditoriaOrdenService;
    @Mock private AuditoriaEstadoRepository auditoriaEstadoRepository;
    @Mock private IncidenciaRepository incidenciaRepository;
    @Mock private TipoIncidenciaRepository tipoIncidenciaRepository;
    @Mock private NotificacionService notificacionService;



    @InjectMocks
    private OrdenCargaService ordenCargaService;

    private OrdenCargaRequestDTO dtoValido;

    // helpers y Mock de entidad.
    
    private EmpresaTercerizada empresa(int id) {
        var e = new EmpresaTercerizada();
        e.setId(id);
        return e;
    }

    private EstadoVehiculo estadoVehiculo(String nombre) {
        var e = new EstadoVehiculo();
        e.setNombre(nombre);
        return e;
    }

    private EstadoOrdenCarga estadoOrden(String nombre) {
        var e = new EstadoOrdenCarga();
        e.setNombre(nombre);
        return e;
    }

    private Vehiculo camion(int id, EmpresaTercerizada empresa, EstadoVehiculo estado) {
        var c = new Vehiculo();
        c.setId(id);
        c.setPatente("PAT-" + id);
        c.setEmpresa(empresa);
        c.setEstado(estado);
        return c;
    }

    private Acoplado acoplado(int id, EmpresaTercerizada empresa, EstadoVehiculo estado, double capacidad) {
        var a = new Acoplado();
        a.setId(id);
        a.setPatente("ACO-" + id);
        a.setEmpresa(empresa);
        a.setEstado(estado);
        a.setCapacidadMaximaLitros(capacidad);
        return a;
    }

    private Usuario usuario(String nombre, String apellido, String legajo) {
        var u = new Usuario();
        u.setNombre(nombre);
        u.setApellido(apellido);
        u.setLegajo(legajo);
        return u;
    }

    private Transportista transportista(int id, Usuario usuario) {
        var t = new Transportista();
        t.setId(id);
        t.setUsuario(usuario);
        return t;
    }

    private LugarOperativo lugar(int id, String nombre) {
        var l = new LugarOperativo();
        l.setId(id);
        l.setNombre(nombre);
        return l;
    }

    private void mockEntidadesVehiculo(Vehiculo camion, Acoplado acoplado) {
        when(vehiculoRepository.findById(1)).thenReturn(Optional.of(camion));
        when(acopladoRepository.findById(1)).thenReturn(Optional.of(acoplado));
        when(estadoOrdenCargaRepository.findById(1)).thenReturn(Optional.of(estadoOrden("Pendiente")));
        when(transportistaRepository.findById(1)).thenReturn(Optional.of(new Transportista()));
        when(lugarOperativoRepository.findById(1)).thenReturn(Optional.of(lugar(1, "Planta")));
        when(lugarOperativoRepository.findById(2)).thenReturn(Optional.of(lugar(2, "Estacion")));
        when(combustibleRepository.findById(1)).thenReturn(Optional.of(new Combustible()));
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(new Usuario()));
        when(rutaRepository.findById(any())).thenReturn(Optional.of(new Ruta()));
    }

    private OrdenCarga ordenConTransportista(String estadoNombre) {
        var orden = new OrdenCarga();
        orden.setEstadoOrdenCarga(estadoOrden(estadoNombre));
        orden.setTransportista(transportista(1, usuario("Trans", "Port", "LEG-T")));
        return orden;
    }

    @BeforeEach
    void setUp() {
        dtoValido = new OrdenCargaRequestDTO(
            "REM-001", "COT-001",
            1,    
            1,    
            1,    
            1,    
            2,    
            1,    
            1,    
            1,    
            1,    
            5000.0, 0.0,
            null, null, null,
            0.0, 0.0,
            "Obs", false, false
        );
    }

    // guardarNuevaOrdenCarga //
    @Test
    void guardarNuevaOrdenCarga_DebeGuardarYRetornarDto()  {

        when(ordenCargaRepository.findByNumeroRemito("REM-001")).thenReturn(Optional.empty());
        when(ordenCargaRepository.findByCot("COT-001")).thenReturn(Optional.empty());

        var emp = empresa(1);
        var disp = estadoVehiculo("Disponible");
        var camion = camion(1, emp, disp);
        var acoplado = acoplado(1, emp, disp, 5000.0);
        var usuarioBase = usuario("Ricardo", "Fort", "LEG-001");
        var transportista = transportista(1, usuarioBase);
        var planta = lugar(1, "Felfort");
        var destino = lugar(2, "Showmatch");
        var combustible = new Combustible(1, "Nafta", "1203", "Clase 3", 0.74, 15.0);
        var estadoOrden = new EstadoOrdenCarga(1, "Pendiente");

        when(vehiculoRepository.findById(1)).thenReturn(Optional.of(camion));
        when(acopladoRepository.findById(1)).thenReturn(Optional.of(acoplado));
        when(estadoOrdenCargaRepository.findById(1)).thenReturn(Optional.of(estadoOrden));
        when(transportistaRepository.findById(1)).thenReturn(Optional.of(transportista));
        when(lugarOperativoRepository.findById(1)).thenReturn(Optional.of(planta));
        when(lugarOperativoRepository.findById(2)).thenReturn(Optional.of(destino));
        when(combustibleRepository.findById(1)).thenReturn(Optional.of(combustible));
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioBase));

        when(ordenCargaRepository.save(any(OrdenCarga.class))).thenAnswer(invocation -> {
            OrdenCarga ordenGuardada = invocation.getArgument(0);
            ordenGuardada.setId(100);
            return ordenGuardada;
        });

        var resultado = ordenCargaService.guardarNuevaOrdenCarga(dtoValido);

        assertNotNull(resultado);
        assertEquals("REM-001", resultado.numeroRemito());
        assertEquals("Felfort", resultado.plantaDespacho());
        assertEquals("Showmatch", resultado.estacionDestino());

        verify(ordenCargaRepository, times(1)).save(any(OrdenCarga.class));

    }

    @Test
    void guardarNuevaOrdenCarga_DebeLanzarExcepcionCuandoRemitoYaExiste() {
        when(ordenCargaRepository.findByNumeroRemito("REM-001")).thenReturn(Optional.of(new OrdenCarga()));

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class,
            () -> ordenCargaService.guardarNuevaOrdenCarga(dtoValido)
        );

        assertEquals("El número de remito ya existe en el sistema.", excepcion.getMessage());
        
        //Verifica que no se guardo la orden de carga en la base de datos
        verify(ordenCargaRepository, never()).save(any());
    }


    @Test 
    void guardarNuevaOrdenCarga_DebeLanzarExcepcionCuandoCotYaExiste() {
        when(ordenCargaRepository.findByNumeroRemito("REM-001")).thenReturn(Optional.empty());
        when(ordenCargaRepository.findByCot("COT-001")).thenReturn(Optional.of(new OrdenCarga()));

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class,
            () -> ordenCargaService.guardarNuevaOrdenCarga(dtoValido)
        );

        assertEquals("El COT ya existe en el sistema.", excepcion.getMessage());
        
        verify(ordenCargaRepository, never()).save(any());
    }

    @Test
    void guardarNuevaOrdenCarga_DebeLanzarExcepcionCuandoLitrosSonCero() {

        var dto = new OrdenCargaRequestDTO(
            "REM-123", "COT-123", 1,1,1,1,2,1,1,1,1,
            0.0, 0.0, null, null, null, 0.0, 0.0, "Obs", false, false);

        when(ordenCargaRepository.findByNumeroRemito("REM-123")).thenReturn(Optional.empty());
        when(ordenCargaRepository.findByCot("COT-123")).thenReturn(Optional.empty());

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class,
        () -> ordenCargaService.guardarNuevaOrdenCarga(dto)

        );

        assertEquals("Los litros cargados son obligatorios.", excepcion.getMessage());

        verify(ordenCargaRepository, never()).save(any());
    }

    @Test
    void guardarNuevaOrdenCarga_DebeLanzarExepcionCuandoPlantaYDestinoSonIguales() {

        var dto = new OrdenCargaRequestDTO(
            "REM-123", "COT-123", 1,1,1,1,1,1,1,1,1,
            5000.0, 0.0, null, null, null, 0.0, 0.0, "Obs", false, false);

            when(ordenCargaRepository.findByNumeroRemito("REM-123")).thenReturn(Optional.empty());
            when(ordenCargaRepository.findByCot("COT-123")).thenReturn(Optional.empty());

            IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class,
            () -> ordenCargaService.guardarNuevaOrdenCarga(dto));

            assertEquals("La planta de despacho y el destino no pueden ser iguales.", excepcion.getMessage());

            verify(ordenCargaRepository, never()).save(any());
    }

    @Test
    void guardarNuevaOrdenCarga_DebeLanzarExcepcionCuandoCamionNoDisponible() {

        when(ordenCargaRepository.findByNumeroRemito("REM-001")).thenReturn(Optional.empty());
        when(ordenCargaRepository.findByCot("COT-001")).thenReturn(Optional.empty());

        var emp = empresa(1);
        var camion = camion(1, emp, estadoVehiculo("Mantenimiento"));
        var acoplado = acoplado(1, emp, estadoVehiculo("Disponible"), 5000.0);
        mockEntidadesVehiculo(camion, acoplado);

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class,
            () -> ordenCargaService.guardarNuevaOrdenCarga(dtoValido)
        );

        assertEquals("El camión no está disponible.", excepcion.getMessage());

        verify(ordenCargaRepository, never()).save(any());

    }

    @Test
    void guardarNuevaOrdenCarga_DebeLanzarExcepcionCuandoAcopladoNoDisponible() {

        when(ordenCargaRepository.findByNumeroRemito("REM-001")).thenReturn(Optional.empty());
        when(ordenCargaRepository.findByCot("COT-001")).thenReturn(Optional.empty());

        var emp = empresa(1);
        var camion = camion(1, emp, estadoVehiculo("Disponible"));
        var acoplado = acoplado(1, emp, estadoVehiculo("Mantenimiento"), 5000.0);
        mockEntidadesVehiculo(camion, acoplado);

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class,
            () -> ordenCargaService.guardarNuevaOrdenCarga(dtoValido)
        );

        assertEquals("El acoplado no está disponible.", excepcion.getMessage());

        verify(ordenCargaRepository, never()).save(any());
    }
    
    @Test
    void guardarNuevaOrdenCarga_DebeLanzarExcepcionCuandoLitrosExcedenCapacidadAcoplado() {

        when(ordenCargaRepository.findByNumeroRemito("REM-001")).thenReturn(Optional.empty());
        when(ordenCargaRepository.findByCot("COT-001")).thenReturn(Optional.empty());

        var emp = empresa(1);
        var disp = estadoVehiculo("Disponible");
        var camion = camion(1, emp, disp);
        var acoplado = acoplado(1, emp, disp, 4999.0); // menor que 5000
        mockEntidadesVehiculo(camion, acoplado);

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class,
            () -> ordenCargaService.guardarNuevaOrdenCarga(dtoValido)
        );

        assertEquals("Los litros cargados superan la capacidad máxima del acoplado.", excepcion.getMessage());

        verify(ordenCargaRepository, never()).save(any());

    }

    @Test
    void guardarNuevaOrdenCarga_DebeLanzarExcepcionCuandoCamionYAcopladoSonDeDistintasEmpresa() {
        
        when(ordenCargaRepository.findByNumeroRemito("REM-001")).thenReturn(Optional.empty());
        when(ordenCargaRepository.findByCot("COT-001")).thenReturn(Optional.empty());

        var disp = estadoVehiculo("Disponible");
        var camion = camion(1, empresa(1), disp);
        var acoplado = acoplado(1, empresa(2), disp, 5000.0); // empresa distinta
        mockEntidadesVehiculo(camion, acoplado);

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class,
            () -> ordenCargaService.guardarNuevaOrdenCarga(dtoValido)
        );

        assertEquals("El camión y el acoplado pertenecen a empresas distintas.", excepcion.getMessage());

        verify(ordenCargaRepository, never()).save(any());


    }

    // confirmarOrden //

    @Test 
    void confirmarOrden_DebeLanzarExcepcionCuandoOrdenNoExiste() {
        
        when(ordenCargaRepository.findById(99)).thenReturn(Optional.empty());

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class,
            () -> ordenCargaService.confirmarOrden(99)
        );

        assertEquals("Orden no encontrada.", excepcion.getMessage());

        verify(ordenCargaRepository, never()).save(any());

    }

    @Test
    void confirmarOrden_DebeSetearEstadoConfirmado() {

        var orden = ordenConTransportista("Pendiente");
        orden.setConfirmado(false);

        when(ordenCargaRepository.findById(1)).thenReturn(Optional.of(orden));

        ordenCargaService.confirmarOrden(1);

        assertTrue(orden.getConfirmado());

        verify(ordenCargaRepository, times(1)).save(orden);

    }

     // Rechazar Orden
    @Test
    void rechazarOrden_DebeLanzarExcepcionCuandoOrdenNoExiste() {
        when(ordenCargaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
            () -> ordenCargaService.rechazarOrden(99, "LEG-001", "Motivo"));

        verify(ordenCargaRepository, never()).save(any());
    }

    @Test
    void rechazarOrden_DebeLanzarExcepcionCuandoOrdenEstaEntregada() {
        var orden = new OrdenCarga();
        orden.setEstadoOrdenCarga(estadoOrden("Entregada"));
        when(ordenCargaRepository.findById(1)).thenReturn(Optional.of(orden));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> ordenCargaService.rechazarOrden(1, "LEG-001", "Motivo"));

        assertTrue(ex.getMessage().contains("Entregada"));
        verify(ordenCargaRepository, never()).save(any());
    }

    @Test
    void rechazarOrden_DebeQuedarConfirmadoFalseYGuardarMotivo() {
        var orden = ordenConTransportista("Pendiente");
        orden.setNumeroRemito("REM-001");
        orden.setEstadoOrdenCarga(estadoOrden("Pendiente"));
        orden.setConfirmado(true);
        when(ordenCargaRepository.findById(1)).thenReturn(Optional.of(orden));

        ordenCargaService.rechazarOrden(1, "LEG-SUP-01", "Faltan datos de que river esta en la B");

        assertFalse(orden.getConfirmado());
        assertEquals("Faltan datos de que river esta en la B", orden.getMotivoRechazo());
        verify(ordenCargaRepository, times(1)).save(orden);
        verify(auditoriaOrdenService, times(1)).registrarCambioEstado(
            eq("REM-001"), anyString(), anyString(), isNull(), eq("LEG-SUP-01"), anyString());
    }

    // aprobarInicioViaje

    @Test
    void aprobarInicioViaje_DebeLanzarExcepcionCuandoOrdenNoExiste() {
        
        when(ordenCargaRepository.findById(99)).thenReturn(Optional.empty());

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class,
            () -> ordenCargaService.aprobarInicioViaje(99, "LEG-001")
        );

        assertEquals("Orden no encontrada.", excepcion.getMessage());

        verify(ordenCargaRepository, never()).save(any());

    }

    @Test
    void aprobarInicioViaje_DebeLanzarExcepcionCuandoEstadoNoEsPendiente() {

        var estadoActual = new com.blackmesaresearch.hytrac.model.lookup.EstadoOrdenCarga();
        estadoActual.setNombre("Pendiente");

        var orden = new OrdenCarga();
        orden.setEstadoOrdenCarga(estadoActual);

        when(ordenCargaRepository.findById(1)).thenReturn(Optional.of(orden));

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class,
            () -> ordenCargaService.aprobarInicioViaje(1, "LEG-001")
        );

        assertEquals("La orden no está pendiente de inicio de viaje.", excepcion.getMessage());

        verify(ordenCargaRepository, never()).save(any());

    }

    @Test
    void aprobarInicioViaje_DebeLanzarExcepcionCuandoNoTieneRuta() {
        var orden = new OrdenCarga();
        orden.setEstadoOrdenCarga(estadoOrden("Pendiente de inicio de viaje"));
        orden.setRuta(null); 
        when(ordenCargaRepository.findById(1)).thenReturn(Optional.of(orden));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> ordenCargaService.aprobarInicioViaje(1, "LEG-001"));

        assertTrue(ex.getMessage().contains("ruta"));
        verify(ordenCargaRepository, never()).save(any());
    }

    @Test
    void aprobarInicioViaje_DebeCambiarEstadoAEnCurso() {

        var orden = ordenConTransportista("Pendiente de inicio de viaje");
        orden.setEstadoOrdenCarga(estadoOrden("Pendiente de inicio de viaje"));
        orden.setRuta(new Ruta());
        orden.setNumeroRemito("REM-001");

        when(ordenCargaRepository.findById(1)).thenReturn(Optional.of(orden));
        when(estadoOrdenCargaRepository.findByNombre("En Curso")).thenReturn(Optional.of(estadoOrden("En Curso")));

        ordenCargaService.aprobarInicioViaje(1, "LEG-001");

        assertEquals("En Curso", orden.getEstadoOrdenCarga().getNombre());
        assertNotNull(orden.getCodigoConfirmacion());

        verify(ordenCargaRepository, times(1)).save(orden);
        verify(auditoriaOrdenService, times(1)).registrarCambioEstado(
            eq("REM-001"), anyString(), anyString(), isNull(), eq("LEG-001"), anyString()
        );
    }

    // Rechazar Inicio de Viaje

    @Test
    void rechazarInicioViaje_DebeLanzarExcepcionCuandoOrdenNoExiste() {
        when(ordenCargaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
            () -> ordenCargaService.rechazarInicioViaje(99, "LEG-001", "Motivo"));

        verify(ordenCargaRepository, never()).save(any());
    }

    @Test
    void rechazarInicioViaje_DebeLanzarExcepcionCuandoEstadoNoEsPendienteDeInicio() {
        var orden = new OrdenCarga();
        orden.setEstadoOrdenCarga(estadoOrden("Pendiente"));
        when(ordenCargaRepository.findById(1)).thenReturn(Optional.of(orden));

        assertThrows(IllegalArgumentException.class,
            () -> ordenCargaService.rechazarInicioViaje(1, "LEG-001", "Motivo"));

        verify(ordenCargaRepository, never()).save(any());
    }

    @Test
    void rechazarInicioViaje_DebeVolverAPendienteYLimpiarFechaSalida() {
        var orden = ordenConTransportista("Pendiente de inicio de viaje");
        orden.setNumeroRemito("REM-001");
        orden.setEstadoOrdenCarga(estadoOrden("Pendiente de inicio de viaje"));
        orden.setFechaSalidaPlanta(java.time.LocalDateTime.now());
        when(ordenCargaRepository.findById(1)).thenReturn(Optional.of(orden));
        when(estadoOrdenCargaRepository.findByNombre("Pendiente"))
            .thenReturn(Optional.of(estadoOrden("Pendiente")));

        ordenCargaService.rechazarInicioViaje(1, "LEG-SUP-02", "Camión con fallas");

        assertEquals("Pendiente", orden.getEstadoOrdenCarga().getNombre());
        assertNull(orden.getFechaSalidaPlanta());
        verify(ordenCargaRepository, times(1)).save(orden);
        verify(auditoriaOrdenService, times(1)).registrarCambioEstado(
            eq("REM-001"), anyString(), anyString(), isNull(), eq("LEG-SUP-02"), anyString());
    }

     // Reportar Entrega //

    @Test
    void reportarEntrega_DebeLanzarExcepcionCuandoOrdenNoExiste() {

        when(ordenCargaRepository.findById(99)).thenReturn(Optional.empty());

        ConfirmarEntregaRequestDTO dto = new ConfirmarEntregaRequestDTO("LEG-001",5000.0, "Sin obs");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> ordenCargaService.reportarEntrega(99, dto)
        );

        assertEquals("Orden no encontrada.",exception.getMessage());
        verify(ordenCargaRepository, never()).save(any());
    }

    @Test
    void reportarEntrega_DebeLanzarExcepcionCuandoEstadoNoPendienteDeConfirmacion() {
        var orden = new OrdenCarga();
        orden.setEstadoOrdenCarga(estadoOrden("En Curso"));
        when(ordenCargaRepository.findById(1)).thenReturn(Optional.of(orden));

        ConfirmarEntregaRequestDTO dto = new ConfirmarEntregaRequestDTO("LEG-001", 5000.0, "Sin obs");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> ordenCargaService.reportarEntrega(1, dto)
        );

        assertEquals("La orden no está pendiente de confirmación de entrega.", exception.getMessage());
        verify(ordenCargaRepository, never()).save(any());
    }

    @Test
    void reportarEntrega_DebeLanzarExcepcionCuandoLitrosEntregadosSonCero() {
        var orden = new OrdenCarga();
        orden.setEstadoOrdenCarga(estadoOrden("Pendiente de confirmacion de entrega"));
        when(ordenCargaRepository.findById(1)).thenReturn(Optional.of(orden));

        ConfirmarEntregaRequestDTO dto = new ConfirmarEntregaRequestDTO("LEG-001", 0.0, "Sin obs");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> ordenCargaService.reportarEntrega(1, dto)
        );

        assertEquals("Los litros entregados son obligatorios.", exception.getMessage());
        verify(ordenCargaRepository, never()).save(any());
    }

    @Test
    void reportarEntrega_DebeActualizarDatosCorrectamente() {
        var orden = new OrdenCarga();
        orden.setEstadoOrdenCarga(estadoOrden("Pendiente de confirmacion de entrega"));
        when(ordenCargaRepository.findById(1)).thenReturn(Optional.of(orden));

        ConfirmarEntregaRequestDTO dto = new ConfirmarEntregaRequestDTO("LEG-001", 5000.0, "Sin obs");

        ordenCargaService.reportarEntrega(1, dto);

        assertEquals(5000.0, orden.getLitrosEntregados());
        assertEquals("Sin obs", orden.getObservaciones());
        assertNotNull(orden.getFechaEntregaReal());
        verify(ordenCargaRepository).save(orden);
    }

    // Confirmar Entrega

    @Test
    void confirmarEntrega_DebeLanzarExcepcionCuandoOrdenNoExiste() {
        when(ordenCargaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
            () -> ordenCargaService.confirmarEntrega(99, "LEG-001"));

        verify(ordenCargaRepository, never()).save(any());
    }

    @Test
    void confirmarEntrega_DebeLanzarExcepcionCuandoEstadoNoEsPendienteDeConfirmacion() {
        var orden = new OrdenCarga();
        orden.setEstadoOrdenCarga(estadoOrden("En Curso"));
        when(ordenCargaRepository.findById(1)).thenReturn(Optional.of(orden));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> ordenCargaService.confirmarEntrega(1, "LEG-001"));

        assertEquals("La orden no está pendiente de confirmación de entrega.", ex.getMessage());
        verify(ordenCargaRepository, never()).save(any());
    }

    @Test
    void confirmarEntrega_DebeCambiarEstadoAEntregada() {
        var orden = ordenConTransportista("Pendiente de confirmacion de entrega");
        orden.setNumeroRemito("REM-001");

        var operador = usuario("Operador", "Test", "LEG-OPE");
        orden.setOperador(operador);

        when(ordenCargaRepository.findById(1)).thenReturn(Optional.of(orden));
        when(estadoOrdenCargaRepository.findByNombre("Entregada"))
            .thenReturn(Optional.of(estadoOrden("Entregada")));

        ordenCargaService.confirmarEntrega(1, "LEG-SUP-01");

        assertEquals("Entregada", orden.getEstadoOrdenCarga().getNombre());
        verify(ordenCargaRepository, times(1)).save(orden);
        verify(auditoriaOrdenService, times(1)).registrarCambioEstado(
            eq("REM-001"), anyString(), anyString(), isNull(), eq("LEG-SUP-01"), anyString());
    }


    // rechazar Entrega

    @Test
    void rechazarEntrega_DebeLanzarExcepcionCuandoOrdenNoExiste() {
        when(ordenCargaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
            () -> ordenCargaService.rechazarEntrega(99, "LEG-001", "Motivo"));

        verify(ordenCargaRepository, never()).save(any());
    }

    @Test
    void rechazarEntrega_DebeLanzarExcepcionCuandoEstadoNoEsPendienteDeConfirmacion() {
        var orden = new OrdenCarga();
        orden.setEstadoOrdenCarga(estadoOrden("En Curso"));
        when(ordenCargaRepository.findById(1)).thenReturn(Optional.of(orden));

        assertThrows(IllegalArgumentException.class,
            () -> ordenCargaService.rechazarEntrega(1, "LEG-001", "Motivo"));

        verify(ordenCargaRepository, never()).save(any());
    }

    @Test
    void rechazarEntrega_DebeVolverAEnCursoYLimpiarDatos() {
        var orden = ordenConTransportista("Pendiente de confirmacion de entrega");
        orden.setNumeroRemito("REM-001");
        orden.setFechaEntregaReal(java.time.LocalDateTime.now());
        orden.setLitrosEntregados(15000.0);

        var operador = usuario("Operador", "Test", "LEG-OPE");
        orden.setOperador(operador);

        when(ordenCargaRepository.findById(1)).thenReturn(Optional.of(orden));
        when(estadoOrdenCargaRepository.findByNombre("En Curso"))
            .thenReturn(Optional.of(estadoOrden("En Curso")));

        ordenCargaService.rechazarEntrega(1, "LEG-SUP-03", "Producto dañado");

        assertEquals("En Curso", orden.getEstadoOrdenCarga().getNombre());
        assertNull(orden.getFechaEntregaReal());
        assertNull(orden.getLitrosEntregados());
        verify(ordenCargaRepository, times(1)).save(orden);
        verify(auditoriaOrdenService, times(1)).registrarCambioEstado(
            eq("REM-001"), anyString(), anyString(), isNull(), eq("LEG-SUP-03"), anyString());
    }

    // Obtener todas //

    @Test
    void obtenerTodas_DebeRetornarListaVaciaCuandoNoHayOrdenes() {
        when(ordenCargaRepository.findAll()).thenReturn(java.util.Collections.emptyList());

        var resultado = ordenCargaService.obtenerTodas();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test 
    void obtenerTodas_DebeRetornarListaDeOrdenes() {

        var orden = new OrdenCarga();
        orden.setId(1);
        orden.setNumeroRemito("REM-123");
        orden.setEstadoOrdenCarga(new EstadoOrdenCarga(1, "Pendiente"));
        orden.setCombustible(new Combustible(1, "Nafta", "1203", "Clase 3", 0.74, 15.0));
        orden.setPlantaDespacho(lugar(1, "Planta"));
        orden.setEstacionDestino(lugar(2, "Estacion"));
        var camion = new Vehiculo();
        camion.setPatente("ABC");
        orden.setCamion(camion);
        orden.setAcoplado(new Acoplado());
        var usuarioBase = usuario("Juan", "Perez", "LEG-001");
        orden.setTransportista(transportista(1, usuarioBase));
        orden.setOperador(usuarioBase);
        when(ordenCargaRepository.findAll()).thenReturn(java.util.List.of(orden));

        var resultado = ordenCargaService.obtenerTodas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("REM-123", resultado.get(0).numeroRemito());

    }

    // Obtener detalle por id //
    @Test
    void obtenerDetallePorId_DebeLanzarExcepcionCuandoOrdenNoExiste() {

        when(ordenCargaRepository.findById(99)).thenReturn(Optional.empty());

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class,
            () -> ordenCargaService.obtenerDetallePorId(99)
        );

        assertEquals("Orden no encontrada.", excepcion.getMessage());
    }

    @Test
    void obtenerDetallePorId_DebeRetornarDtoCorrecto() {
        var orden = new OrdenCarga();
        orden.setId(1);
        orden.setNumeroRemito("REM-123");
        orden.setEstadoOrdenCarga(estadoOrden("En Curso"));
        orden.setCamion(camion(1, empresa(1), estadoVehiculo("Disp")));
        orden.setAcoplado(acoplado(1, empresa(1), estadoVehiculo("Disp"), 5000.0));
        orden.setTransportista(transportista(1, usuario("Trans", "Port", "LEG-T")));
        orden.setCombustible(new Combustible(1, "Gasoil", "1202", "3", 1.0, 1.0));
        orden.setPlantaDespacho(lugar(1, "Planta A"));
        orden.setEstacionDestino(lugar(2, "Estacion B"));
        orden.setOperador(usuario("Ope", "Rador", "LEG-O"));

        when(ordenCargaRepository.findById(1)).thenReturn(Optional.of(orden));

        var resultado = ordenCargaService.obtenerDetallePorId(1);

        assertNotNull(resultado);
        assertEquals("REM-123", resultado.numeroRemito());
        assertEquals("En Curso", resultado.estado());
    }

    // Obtener Orden Supervisor //

    @Test
    void obtenerOrdenSupervisor_DebeLanzarExcepcionCuandoOrdenNoExiste() {

        when(ordenCargaRepository.findById(99)).thenReturn(Optional.empty());

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class,
            () -> ordenCargaService.obtenerOrdenSupervisor(99)
        );

        assertEquals("Orden no encontrada.", excepcion.getMessage());
    }

    @Test
    void obtenerOrdenSupervisor_DebeRetornarDtoCorrecto() {
        var orden = new OrdenCarga();
        orden.setId(1);
        orden.setNumeroRemito("REM-123");
        orden.setEstadoOrdenCarga(estadoOrden("Pendiente"));
        orden.setCamion(camion(1, empresa(1), estadoVehiculo("Disp")));
        orden.setAcoplado(acoplado(1, empresa(1), estadoVehiculo("Disp"), 5000.0));
        var transportista = transportista(1, usuario("Trans", "Port", "LEG-T"));
        transportista.setTipoVinculo(new com.blackmesaresearch.hytrac.model.lookup.TipoVinculo(1, "Contratado"));
        orden.setTransportista(transportista);
        orden.setCombustible(new Combustible(1, "Gasoil", "1202", "3", 1.0, 1.0));
        orden.setPlantaDespacho(lugar(1, "Planta A"));
        orden.setEstacionDestino(lugar(2, "Estacion B"));
        orden.setOperador(usuario("Ope", "Rador", "LEG-O"));

        when(ordenCargaRepository.findById(1)).thenReturn(Optional.of(orden));

        var resultado = ordenCargaService.obtenerOrdenSupervisor(1);

        assertNotNull(resultado);
        assertEquals("REM-123", resultado.numeroRemito());
        assertEquals("Trans", resultado.transportistaNombre());
    }

    //  Obtener todas supervisor //

    @Test
    void obtenerTodasSupervisor_DebeRetornarListaVaciaCuandoNohayOrdenes() {

        when(ordenCargaRepository.findAll()).thenReturn(java.util.Collections.emptyList());

        var resultado = ordenCargaService.obtenerTodasSupervisor();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // Editar Orden Carga //

    @Test
    void editarOrdenCarga_DebeActualizarYRetornarDto() {
        var ordenExistente = new OrdenCarga();
        ordenExistente.setId(1);
        ordenExistente.setNumeroRemito("REM-VIEJO");
        ordenExistente.setEstadoOrdenCarga(estadoOrden("Pendiente"));
        ordenExistente.setCamion(camion(1, empresa(1), estadoVehiculo("Disponible")));
        ordenExistente.setAcoplado(acoplado(1, empresa(1), estadoVehiculo("Disponible"), 6000.0));

        when(ordenCargaRepository.findById(1)).thenReturn(Optional.of(ordenExistente));
        
        when(ordenCargaRepository.findByNumeroRemito(dtoValido.numeroRemito())).thenReturn(Optional.empty());
        when(ordenCargaRepository.findByCot(dtoValido.cot())).thenReturn(Optional.empty());

        var emp = empresa(1);
        var disp = estadoVehiculo("Disponible");
        when(vehiculoRepository.findById(1)).thenReturn(Optional.of(camion(1, emp, disp)));
        when(acopladoRepository.findById(1)).thenReturn(Optional.of(acoplado(1, emp, disp, 6000.0)));
        when(transportistaRepository.findById(1)).thenReturn(Optional.of(transportista(1, usuario("T", "T", "L-1"))));
        when(lugarOperativoRepository.findById(1)).thenReturn(Optional.of(lugar(1, "Planta")));
        when(lugarOperativoRepository.findById(2)).thenReturn(Optional.of(lugar(2, "Destino")));
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario("O", "O", "L-2")));
        when(combustibleRepository.findById(1)).thenReturn(Optional.of(new Combustible(1, "Nafta", "1", "1", 1.0, 1.0)));
        when(estadoOrdenCargaRepository.findById(1)).thenReturn(Optional.of(estadoOrden("Pendiente")));

        when(ordenCargaRepository.save(any(OrdenCarga.class))).thenAnswer(i -> i.getArgument(0));

        var resultado = ordenCargaService.editarOrdenCarga(1, dtoValido);

        assertNotNull(resultado);
        assertEquals(dtoValido.numeroRemito(), resultado.numeroRemito());
        assertFalse(ordenExistente.getConfirmado()); // Verifica que se limpió la confirmación
        assertNull(ordenExistente.getMotivoRechazo()); // Verifica que se limpió el motivo
        verify(ordenCargaRepository, times(1)).save(ordenExistente);
    }

    @Test
    void editarOrdenCarga_DebeLanzarExcepcionCuandoOrdenNoExiste() {

        when(ordenCargaRepository.findById(99)).thenReturn(Optional.empty());

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class,
            () -> ordenCargaService.editarOrdenCarga(99, dtoValido)
        );

        assertEquals("Orden no encontrada.", excepcion.getMessage());

        verify(ordenCargaRepository, never()).save(any());
    }

    @Test
    void editarOrdenCarga_DebeLanzarExcepcionCuandoOrdenEstaEntregada() {
        var orden = new OrdenCarga();
        orden.setEstadoOrdenCarga(estadoOrden("Entregada"));
        when(ordenCargaRepository.findById(1)).thenReturn(Optional.of(orden));

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class,
            () -> ordenCargaService.editarOrdenCarga(1, dtoValido)
        );

        assertTrue(excepcion.getMessage().contains("Entregada"));
        
        verify(ordenCargaRepository, never()).save(any());
    }

    @Test
    void editarOrdenCarga_DebeLanzarExcepcionCuandoOrdenEstaCancelada() {
        var orden = new OrdenCarga();
        orden.setEstadoOrdenCarga(estadoOrden("Cancelada"));
        when(ordenCargaRepository.findById(1)).thenReturn(Optional.of(orden));

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class,
            () -> ordenCargaService.editarOrdenCarga(1, dtoValido)
        );

        assertTrue(excepcion.getMessage().contains("Cancelada"));
        
        verify(ordenCargaRepository, never()).save(any());
    }

    @Test
    void editarOrdenCarga_DebeLanzarExcepcionCuandoRemitoYaExisteEnOtraOrden() {
        var orden = new OrdenCarga();
        orden.setId(1);
        orden.setEstadoOrdenCarga(estadoOrden("Pendiente"));
        var otraOrden = new OrdenCarga();
        otraOrden.setId(99);
        when(ordenCargaRepository.findById(1)).thenReturn(Optional.of(orden));
        when(ordenCargaRepository.findByNumeroRemito("REM-001")).thenReturn(Optional.of(otraOrden));

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class,
            () -> ordenCargaService.editarOrdenCarga(1, dtoValido)
        );

        assertEquals("El número de remito ya existe en otra orden del sistema.", excepcion.getMessage());
        verify(ordenCargaRepository, never()).save(any());
    }

    @Test
    void editarOrdenCarga_DebeLanzarExcepcionCuandoCotYaExisteEnOtraOrden() {
        var estado = new EstadoOrdenCarga();
        estado.setNombre("Pendiente");
        var orden = new OrdenCarga();
        orden.setId(1);
        orden.setEstadoOrdenCarga(estado);

        var otraOrden = new OrdenCarga();
        otraOrden.setId(99);

        when(ordenCargaRepository.findById(1)).thenReturn(Optional.of(orden));
        when(ordenCargaRepository.findByCot("COT-001")).thenReturn(Optional.of(otraOrden));

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class,
            () -> ordenCargaService.editarOrdenCarga(1, dtoValido)
        );

        assertEquals("El COT ya existe en otra orden del sistema.", excepcion.getMessage());
        verify(ordenCargaRepository, never()).save(any());
    }

    @Test
    void editarOrdenCarga_DebeLanzarExcepcionCuandoLitrosSonCero() {
        var orden = new OrdenCarga();
        orden.setId(1);
        orden.setEstadoOrdenCarga(estadoOrden("Pendiente"));
        var dtoSinLitros = new OrdenCargaRequestDTO(
            "REM-001", "COT-001", 1,1,1,1,2,1,1,1,1,
            0.0, 0.0, null, null, null, 0.0, 0.0, "Obs", false, false);

        when(ordenCargaRepository.findById(1)).thenReturn(Optional.of(orden));
        when(ordenCargaRepository.findByNumeroRemito("REM-001")).thenReturn(Optional.empty());
        when(ordenCargaRepository.findByCot("COT-001")).thenReturn(Optional.empty());

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class,
            () -> ordenCargaService.editarOrdenCarga(1, dtoSinLitros)
        );

        assertEquals("Los litros cargados son obligatorios y deben ser mayores a cero.", excepcion.getMessage());
    }

    @Test
    void editarOrdenCarga_DebeLanzarExcepcionCuanoPlantayDestinoSonIguales() {
        var orden = new OrdenCarga();
        orden.setId(1);
        orden.setEstadoOrdenCarga(estadoOrden("Pendiente"));
        var dtoIgual = new OrdenCargaRequestDTO(
            "REM-001", "COT-001", 1,1,1,1,1,1,1,1,1,
            5000.0, 0.0, null, null, null, 0.0, 0.0, "Obs", false, false);

        when(ordenCargaRepository.findById(1)).thenReturn(Optional.of(orden));
        when(ordenCargaRepository.findByNumeroRemito("REM-001")).thenReturn(Optional.empty());
        when(ordenCargaRepository.findByCot("COT-001")).thenReturn(Optional.empty());

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class,
            () -> ordenCargaService.editarOrdenCarga(1, dtoIgual)
        );

        assertEquals("La planta de despacho y el destino no pueden ser iguales.", excepcion.getMessage());
    }


    // Cancelar Orden //

    @Test
    void cancelarOrden_DebeLanzarExcepcionCuandoOrdenNoExiste() {
        when(ordenCargaRepository.findByNumeroRemito("REM-999")).thenReturn(Optional.empty());

        CancelarOrdenRequestDTO dto = new CancelarOrdenRequestDTO("LEG-001", "Motivo Test");

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class,
            () -> ordenCargaService.cancelarOrden("REM-999", dto)
        );

        assertTrue(excepcion.getMessage().contains("REM-999"));

        verify(ordenCargaRepository, never()).save(any());
    }

    @Test
    void cancelarOrden_DebeLanzarExcepcionCuandoOrdenYaEstaEntregada() {
        var orden = new OrdenCarga();
        orden.setEstadoOrdenCarga(estadoOrden("Entregada"));
        when(ordenCargaRepository.findByNumeroRemito("REM-001")).thenReturn(Optional.of(orden));

        CancelarOrdenRequestDTO dto = new CancelarOrdenRequestDTO("LEG-001", "Motivo");

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, ()
            -> ordenCargaService.cancelarOrden("REM-001", dto)
        );

        assertTrue(excepcion.getMessage().contains("Entregada"));
        verify(ordenCargaRepository, never()).save(any());

    }

    @Test
    void cancelarOrden_DebeLanzarExcepcionCuandoOrdenYaEstaCancelada() {
        var estado = new EstadoOrdenCarga();
        estado.setNombre("Cancelada");
        var orden = new OrdenCarga();
        orden.setEstadoOrdenCarga(estado);

        when(ordenCargaRepository.findByNumeroRemito("REM-001")).thenReturn(Optional.of(orden));

        CancelarOrdenRequestDTO dto = new CancelarOrdenRequestDTO("LEG-001", "Motivo");

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, ()
            -> ordenCargaService.cancelarOrden("REM-001", dto)
        );

        assertTrue(excepcion.getMessage().contains("Cancelada"));
        verify(ordenCargaRepository, never()).save(any());

    }

    @Test
    void cancelarOrden_DebeLanzarExcepcionCuanoRolNoEsSupervisor() {
        var orden = new OrdenCarga();
        orden.setNumeroRemito("REM-001");
        orden.setEstadoOrdenCarga(estadoOrden("Pendiente"));
        when(ordenCargaRepository.findByNumeroRemito("REM-001")).thenReturn(Optional.of(orden));

        var rol = new Rol();
        rol.setNombre("OPERADOR");
        var solicitante = new Usuario();
        solicitante.setRol(rol);
        when(usuarioRepository.findByLegajo("LEG-001")).thenReturn(Optional.of(solicitante));

        CancelarOrdenRequestDTO dto = new CancelarOrdenRequestDTO("LEG-001", "Motivo");

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, ()
            -> ordenCargaService.cancelarOrden("REM-001", dto)
        );

        assertEquals("Su rol no está autorizado para realizar o gestionar solicitudes de cancelación.", excepcion.getMessage());
        verify(ordenCargaRepository, never()).save(any());
    }

    @Test
    void cancelarOrden_DebeRechazarCancelacionYResolverIncidencia() {
        var orden = new OrdenCarga();
        orden.setId(1);
        orden.setNumeroRemito("REM-001");
        orden.setEstadoOrdenCarga(estadoOrden("Pendiente"));
        
        orden.setCamion(camion(1, empresa(1), estadoVehiculo("Disponible")));
        orden.setAcoplado(acoplado(1, empresa(1), estadoVehiculo("Disponible"), 5000.0));
        orden.setTransportista(transportista(1, usuario("Trans", "Port", "LEG-T")));
        orden.setPlantaDespacho(lugar(1, "Planta"));
        orden.setEstacionDestino(lugar(2, "Destino"));
        orden.setCombustible(new Combustible(1, "Gasoil", "1202", "3", 1.0, 1.0));
        orden.setOperador(usuario("Ope", "Rador", "LEG-O"));

        when(ordenCargaRepository.findByNumeroRemito("REM-001")).thenReturn(Optional.of(orden));

        var rol = new Rol();
        rol.setNombre("SUPERVISOR");
        var solicitante = usuario("Sup", "Evisor", "LEG-SUP"); // Usamos tu helper
        solicitante.setRol(rol);
        when(usuarioRepository.findByLegajo("LEG-SUP")).thenReturn(Optional.of(solicitante));

        var incidencia = new com.blackmesaresearch.hytrac.model.core.Incidencia();
        incidencia.setOrden(orden);
        incidencia.setResuelto(false);
        when(incidenciaRepository.findAll()).thenReturn(java.util.List.of(incidencia));

        when(ordenCargaRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var dto = new CancelarOrdenRequestDTO("LEG-SUP", "RECHAZADO");
        var resultado = ordenCargaService.cancelarOrden("REM-001", dto);

        assertNotNull(resultado);
        assertTrue(incidencia.getResuelto());
        assertEquals("Solicitud de cancelación rechazada por supervisor.", incidencia.getAccionesTomadas());
        verify(incidenciaRepository, times(1)).save(incidencia);
    }

    @Test
    void cancelarOrden_DebeAprobarCancelacionYResolverIncidencia() {
        // 1. Setup Orden (Completa)
        var estadoAnterior = estadoOrden("Pendiente");
        var orden = new OrdenCarga();
        orden.setId(1);
        orden.setNumeroRemito("REM-001");
        orden.setEstadoOrdenCarga(estadoAnterior);
        
        orden.setCamion(camion(1, empresa(1), estadoVehiculo("Disponible")));
        orden.setAcoplado(acoplado(1, empresa(1), estadoVehiculo("Disponible"), 5000.0));
        orden.setTransportista(transportista(1, usuario("Trans", "Port", "LEG-T")));
        orden.setPlantaDespacho(lugar(1, "Planta"));
        orden.setEstacionDestino(lugar(2, "Destino"));
        orden.setCombustible(new Combustible(1, "Gasoil", "1202", "3", 1.0, 1.0));
        orden.setOperador(usuario("Ope", "Rador", "LEG-O"));

        when(ordenCargaRepository.findByNumeroRemito("REM-001")).thenReturn(Optional.of(orden));

        var rol = new Rol();
        rol.setNombre("SUPERVISOR");
        var solicitante = usuario("Sup", "Evisor", "LEG-SUP");
        solicitante.setRol(rol);
        when(usuarioRepository.findByLegajo("LEG-SUP")).thenReturn(Optional.of(solicitante));


        var incidencia = new com.blackmesaresearch.hytrac.model.core.Incidencia();
        incidencia.setOrden(orden);
        incidencia.setResuelto(false);
        when(incidenciaRepository.findAll()).thenReturn(java.util.List.of(incidencia));

        var estadoCancelado = estadoOrden("Cancelada");
        when(estadoOrdenCargaRepository.findByNombre("Cancelada")).thenReturn(Optional.of(estadoCancelado));

        when(ordenCargaRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var dto = new CancelarOrdenRequestDTO("LEG-SUP", "Falla mecánica grave");
        var resultado = ordenCargaService.cancelarOrden("REM-001", dto);

        assertNotNull(resultado);
        assertEquals("Cancelada", orden.getEstadoOrdenCarga().getNombre());
        assertTrue(incidencia.getResuelto());
        verify(incidenciaRepository, times(1)).save(incidencia);
        verify(auditoriaOrdenService, times(1)).registrarCambioEstado(
            eq("REM-001"), eq("Pendiente"), eq("Cancelada"), eq("LEG-SUP"), eq("LEG-SUP"), anyString()
        );
    }


   

}
