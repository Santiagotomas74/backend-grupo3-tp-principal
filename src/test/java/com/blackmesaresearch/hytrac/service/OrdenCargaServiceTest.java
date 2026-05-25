package com.blackmesaresearch.hytrac.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
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
import com.blackmesaresearch.hytrac.model.core.OrdenCarga;
import com.blackmesaresearch.hytrac.model.core.Usuario;
import com.blackmesaresearch.hytrac.model.core.Vehiculo;
import com.blackmesaresearch.hytrac.model.lookup.EstadoOrdenCarga;
import com.blackmesaresearch.hytrac.model.lookup.Rol;
import com.blackmesaresearch.hytrac.repository.AcopladoRepository;
import com.blackmesaresearch.hytrac.repository.CombustibleRepository;
import com.blackmesaresearch.hytrac.repository.EstadoOrdenCargaRepository;
import com.blackmesaresearch.hytrac.repository.LugarOperativoRepository;
import com.blackmesaresearch.hytrac.repository.OrdenCargaRepository;
import com.blackmesaresearch.hytrac.repository.RutaRepository;
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



    @InjectMocks
    private OrdenCargaService ordenCargaService;

    private OrdenCargaRequestDTO dtoValido;

    @BeforeEach
    void setUp() {
        dtoValido = new OrdenCargaRequestDTO(
            "REM-001", "COT-001", 1, 1, 1, 1, 2, 1, 1, 1, 1,
            5000.0, 0.0, null, null, null, 0.0, 0.0, "Obs", false, false
        );
    }


    // guardarNuevaOrdenCarga //
    @Test
    void guardarNuevaOrdenCarga_DebeGuardarYRetornarDto()  {

        when(ordenCargaRepository.findByNumeroRemito("REM-001")).thenReturn(Optional.empty());
        when(ordenCargaRepository.findByCot("COT-001")).thenReturn(Optional.empty());

        var empresa = new com.blackmesaresearch.hytrac.model.core.EmpresaTercerizada();
        empresa.setId(1);

        var estadoDisponible = new com.blackmesaresearch.hytrac.model.lookup.EstadoVehiculo();
        estadoDisponible.setNombre("Disponible");

        var camion = new Vehiculo();
        camion.setId(1);
        camion.setPatente("ABC123");
        camion.setEmpresa(empresa);
        camion.setEstado(estadoDisponible);
        
        var acoplado = new com.blackmesaresearch.hytrac.model.core.Acoplado();
        acoplado.setId(1);
        acoplado.setPatente("ACO123");
        acoplado.setEmpresa(empresa);
        acoplado.setEstado(estadoDisponible);
        acoplado.setCapacidadMaximaLitros(5000.0);

        var usuarioBase = new com.blackmesaresearch.hytrac.model.core.Usuario();
        usuarioBase.setNombre("Ricardo");
        usuarioBase.setApellido("Fort");
        usuarioBase.setLegajo("Miami-123");

        var transportista = new com.blackmesaresearch.hytrac.model.core.Transportista();
        transportista.setId(1);
        transportista.setUsuario(usuarioBase);

        var planta = new com.blackmesaresearch.hytrac.model.core.LugarOperativo();
        planta.setId(1);
        planta.setNombre("Felfort");

        var destino = new com.blackmesaresearch.hytrac.model.core.LugarOperativo();
        destino.setId(2);
        destino.setNombre("Showmatch");

        var combustible = new com.blackmesaresearch.hytrac.model.reference.Combustible();
        combustible.setId(1);
        combustible.setNombre("Nafta");

        var estadoOrden = new com.blackmesaresearch.hytrac.model.lookup.EstadoOrdenCarga();
        estadoOrden.setId(1);
        estadoOrden.setNombre("Pendiente");

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

        OrdenCargaRequestDTO dtoFalso = new OrdenCargaRequestDTO(
            "REM-123", "COT-123", 1, 1, 1, 1, 1, 1, 1, 1, 1,
            0.0,
            0.0, null, null, null, 0.0, 0.0, "Obs", false, false);

        when(ordenCargaRepository.findByNumeroRemito("REM-123")).thenReturn(Optional.empty());
        when(ordenCargaRepository.findByCot("COT-123")).thenReturn(Optional.empty());

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class,
        () -> ordenCargaService.guardarNuevaOrdenCarga(dtoFalso)

        );

        assertEquals("Los litros cargados son obligatorios.", excepcion.getMessage());

        verify(ordenCargaRepository, never()).save(any());
    }

    @Test
    void guardarNuevaOrdenCarga_DebeLanzarExepcionCuandoPlantaYDestinoSonIguales() {

        OrdenCargaRequestDTO dtoIgual = new OrdenCargaRequestDTO(
            "REM-123", "COT-123", 1, 1, 1, 1, 1, 1, 1, 1, 1,
            5000.0,
            0.0, null, null, null, 0.0, 0.0, "Obs", false, false);

            when(ordenCargaRepository.findByNumeroRemito("REM-123")).thenReturn(Optional.empty());
            when(ordenCargaRepository.findByCot("COT-123")).thenReturn(Optional.empty());

            IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class,
            () -> ordenCargaService.guardarNuevaOrdenCarga(dtoIgual));

            assertEquals("La planta de despacho y el destino no pueden ser iguales.", excepcion.getMessage());

            verify(ordenCargaRepository, never()).save(any());
    }

    @Test
    void guardarNuevaOrdenCarga_DebeLanzarExcepcionCuandoCamionNoDisponible() {

        when(ordenCargaRepository.findByNumeroRemito("REM-001")).thenReturn(Optional.empty());
        when(ordenCargaRepository.findByCot("COT-001")).thenReturn(Optional.empty());

        var empresa = new com.blackmesaresearch.hytrac.model.core.EmpresaTercerizada();
        empresa.setId(1);

        var estadoMalo = new com.blackmesaresearch.hytrac.model.lookup.EstadoVehiculo();
        estadoMalo.setNombre("Mantenimiento"); 

        var camion = new Vehiculo();
        camion.setId(1);
        camion.setEmpresa(empresa);
        camion.setEstado(estadoMalo);

        var acoplado = new com.blackmesaresearch.hytrac.model.core.Acoplado();
        acoplado.setId(1);
        acoplado.setEmpresa(empresa);
        acoplado.setCapacidadMaximaLitros(5000.0);

        when(vehiculoRepository.findById(1)).thenReturn(Optional.of(camion));
        when(acopladoRepository.findById(1)).thenReturn(Optional.of(acoplado));
        when(estadoOrdenCargaRepository.findById(1)).thenReturn(Optional.of(new com.blackmesaresearch.hytrac.model.lookup.EstadoOrdenCarga()));
        when(transportistaRepository.findById(1)).thenReturn(Optional.of(new com.blackmesaresearch.hytrac.model.core.Transportista()));
        when(lugarOperativoRepository.findById(1)).thenReturn(Optional.of(new com.blackmesaresearch.hytrac.model.core.LugarOperativo()));
        when(combustibleRepository.findById(1)).thenReturn(Optional.of(new com.blackmesaresearch.hytrac.model.reference.Combustible()));
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(new com.blackmesaresearch.hytrac.model.core.Usuario()));
        when(lugarOperativoRepository.findById(2)).thenReturn(Optional.of(new com.blackmesaresearch.hytrac.model.core.LugarOperativo()));
        when(rutaRepository.findById(any())).thenReturn(Optional.of(new com.blackmesaresearch.hytrac.model.core.Ruta()));

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

        var empresa = new com.blackmesaresearch.hytrac.model.core.EmpresaTercerizada();
        empresa.setId(1);

        var estadoBueno = new com.blackmesaresearch.hytrac.model.lookup.EstadoVehiculo();
        estadoBueno.setNombre("Disponible"); 

        var estadoMalo = new com.blackmesaresearch.hytrac.model.lookup.EstadoVehiculo();
        estadoMalo.setNombre("Mantenimiento");

        var camion = new Vehiculo();
        camion.setId(1);
        camion.setEmpresa(empresa);
        camion.setEstado(estadoBueno);

        var acoplado = new com.blackmesaresearch.hytrac.model.core.Acoplado();
        acoplado.setId(1);
        acoplado.setEmpresa(empresa);
        acoplado.setEstado(estadoMalo);
        acoplado.setCapacidadMaximaLitros(5000.0);

        when(vehiculoRepository.findById(1)).thenReturn(Optional.of(camion));
        when(acopladoRepository.findById(1)).thenReturn(Optional.of(acoplado));
        when(estadoOrdenCargaRepository.findById(1)).thenReturn(Optional.of(new com.blackmesaresearch.hytrac.model.lookup.EstadoOrdenCarga()));
        when(transportistaRepository.findById(1)).thenReturn(Optional.of(new com.blackmesaresearch.hytrac.model.core.Transportista()));
        when(lugarOperativoRepository.findById(1)).thenReturn(Optional.of(new com.blackmesaresearch.hytrac.model.core.LugarOperativo()));
        when(combustibleRepository.findById(1)).thenReturn(Optional.of(new com.blackmesaresearch.hytrac.model.reference.Combustible()));
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(new com.blackmesaresearch.hytrac.model.core.Usuario()));
        when(lugarOperativoRepository.findById(2)).thenReturn(Optional.of(new com.blackmesaresearch.hytrac.model.core.LugarOperativo()));
        when(rutaRepository.findById(any())).thenReturn(Optional.of(new com.blackmesaresearch.hytrac.model.core.Ruta()));

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

        var empresa = new com.blackmesaresearch.hytrac.model.core.EmpresaTercerizada();
        empresa.setId(1);

        var estadoBueno = new com.blackmesaresearch.hytrac.model.lookup.EstadoVehiculo();
        estadoBueno.setNombre("Disponible");

        var camion = new com.blackmesaresearch.hytrac.model.core.Vehiculo();
        camion.setEmpresa(empresa);
        camion.setEstado(estadoBueno);

        var acoplado = new com.blackmesaresearch.hytrac.model.core.Acoplado();
        acoplado.setEmpresa(empresa);
        acoplado.setEstado(estadoBueno);
        acoplado.setCapacidadMaximaLitros(4999.0);

        when(vehiculoRepository.findById(1)).thenReturn(Optional.of(camion));
        when(acopladoRepository.findById(1)).thenReturn(Optional.of(acoplado));
        when(estadoOrdenCargaRepository.findById(1)).thenReturn(Optional.of(new com.blackmesaresearch.hytrac.model.lookup.EstadoOrdenCarga()));
        when(transportistaRepository.findById(1)).thenReturn(Optional.of(new com.blackmesaresearch.hytrac.model.core.Transportista()));
        when(lugarOperativoRepository.findById(1)).thenReturn(Optional.of(new com.blackmesaresearch.hytrac.model.core.LugarOperativo()));
        when(combustibleRepository.findById(1)).thenReturn(Optional.of(new com.blackmesaresearch.hytrac.model.reference.Combustible()));
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(new com.blackmesaresearch.hytrac.model.core.Usuario()));
        when(lugarOperativoRepository.findById(2)).thenReturn(Optional.of(new com.blackmesaresearch.hytrac.model.core.LugarOperativo()));
        when(rutaRepository.findById(any())).thenReturn(Optional.of(new com.blackmesaresearch.hytrac.model.core.Ruta()));

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

        var empresa1 = new com.blackmesaresearch.hytrac.model.core.EmpresaTercerizada();
        empresa1.setId(1);

        var empresa2 = new com.blackmesaresearch.hytrac.model.core.EmpresaTercerizada();
        empresa2.setId(2);

        var estadoBueno = new com.blackmesaresearch.hytrac.model.lookup.EstadoVehiculo();
        estadoBueno.setNombre("Disponible");

        var camion = new com.blackmesaresearch.hytrac.model.core.Vehiculo();
        camion.setEmpresa(empresa1);
        camion.setEstado(estadoBueno);

        var acoplado = new com.blackmesaresearch.hytrac.model.core.Acoplado();
        acoplado.setEmpresa(empresa2);
        acoplado.setEstado(estadoBueno);
        acoplado.setCapacidadMaximaLitros(5000.0);

        when(vehiculoRepository.findById(1)).thenReturn(Optional.of(camion));
        when(acopladoRepository.findById(1)).thenReturn(Optional.of(acoplado));
        when(estadoOrdenCargaRepository.findById(1)).thenReturn(Optional.of(new com.blackmesaresearch.hytrac.model.lookup.EstadoOrdenCarga()));
        when(transportistaRepository.findById(1)).thenReturn(Optional.of(new com.blackmesaresearch.hytrac.model.core.Transportista()));
        when(lugarOperativoRepository.findById(1)).thenReturn(Optional.of(new com.blackmesaresearch.hytrac.model.core.LugarOperativo()));
        when(combustibleRepository.findById(1)).thenReturn(Optional.of(new com.blackmesaresearch.hytrac.model.reference.Combustible()));
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(new com.blackmesaresearch.hytrac.model.core.Usuario()));
        when(lugarOperativoRepository.findById(2)).thenReturn(Optional.of(new com.blackmesaresearch.hytrac.model.core.LugarOperativo()));
        when(rutaRepository.findById(any())).thenReturn(Optional.of(new com.blackmesaresearch.hytrac.model.core.Ruta()));

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

        var orden = new OrdenCarga();
        orden.setConfirmado(false);

        when(ordenCargaRepository.findById(1)).thenReturn(Optional.of(orden));

        ordenCargaService.confirmarOrden(1);

        assertTrue(orden.getConfirmado());

        verify(ordenCargaRepository, times(1)).save(orden);

    }

    // aprobarInicioViaje

    @Test
    void aprobarInicioViaje_DebeLanzarExcepcionCuandoOrdenNoExiste() {
        
        when(ordenCargaRepository.findById(99)).thenReturn(Optional.empty());

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class,
            () -> ordenCargaService.aprobarInicioViaje(99)
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
            () -> ordenCargaService.aprobarInicioViaje(1)
        );

        assertEquals("La orden no está pendiente de inicio de viaje.", excepcion.getMessage());

        verify(ordenCargaRepository, never()).save(any());

    }

    @Test
    void aprobarInicioViaje_DebeCambiarEstadoAEnCurso() {

        var estadoActual = new com.blackmesaresearch.hytrac.model.lookup.EstadoOrdenCarga();
        estadoActual.setNombre("Pendiente de inicio de viaje");

        var orden = new OrdenCarga();
        orden.setEstadoOrdenCarga(estadoActual);

        when(ordenCargaRepository.findById(1)).thenReturn(Optional.of(orden));

        var estadoEnCurso = new com.blackmesaresearch.hytrac.model.lookup.EstadoOrdenCarga();
        estadoEnCurso.setNombre("En Curso");

        when(estadoOrdenCargaRepository.findByNombre("En Curso")).thenReturn(Optional.of(estadoEnCurso));

        ordenCargaService.aprobarInicioViaje(1);

        assertEquals("En Curso", orden.getEstadoOrdenCarga().getNombre());

        verify(ordenCargaRepository, times(1)).save(orden);

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
        orden.setEstadoOrdenCarga(new com.blackmesaresearch.hytrac.model.lookup.EstadoOrdenCarga(1,"Pendiente"));
        orden.setCombustible(new com.blackmesaresearch.hytrac.model.reference.Combustible(1, "Nafta", "1203", "Clase 3", 0.74, 15.0));
        orden.setPlantaDespacho(new com.blackmesaresearch.hytrac.model.core.LugarOperativo());
        orden.setEstacionDestino(new com.blackmesaresearch.hytrac.model.core.LugarOperativo());

        var camion = new Vehiculo();
        camion.setPatente("ABC");
        
        var acoplado = new com.blackmesaresearch.hytrac.model.core.Acoplado();
        orden.setCamion(camion);
        orden.setAcoplado(acoplado);

        var usuario = new com.blackmesaresearch.hytrac.model.core.Usuario();
        var transportista = new com.blackmesaresearch.hytrac.model.core.Transportista();
        transportista.setUsuario(usuario);
        orden.setTransportista(transportista);
        orden.setOperador(usuario);

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

    // Obtener Orden Supervisor //

    @Test
    void obtenerOrdenSupervisor_DebeLanzarExcepcionCuandoOrdenNoExiste() {

        when(ordenCargaRepository.findById(99)).thenReturn(Optional.empty());

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class,
            () -> ordenCargaService.obtenerOrdenSupervisor(99)
        );

        assertEquals("Orden no encontrada.", excepcion.getMessage());
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
        var estado = new EstadoOrdenCarga();
        estado.setNombre("Entregada");
        var orden = new OrdenCarga();
        orden.setEstadoOrdenCarga(estado);

        when(ordenCargaRepository.findById(1)).thenReturn(Optional.of(orden));

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class,
            () -> ordenCargaService.editarOrdenCarga(1, dtoValido)
        );

        assertTrue(excepcion.getMessage().contains("Entregada"));
        
        verify(ordenCargaRepository, never()).save(any());
    }

    @Test
    void editarOrdenCarga_DebeLanzarExcepcionCuandoOrdenEstaCancelada() {
        var estado = new EstadoOrdenCarga();
        estado.setNombre("Cancelada");
        var orden = new OrdenCarga();
        orden.setEstadoOrdenCarga(estado);

        when(ordenCargaRepository.findById(1)).thenReturn(Optional.of(orden));

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class,
            () -> ordenCargaService.editarOrdenCarga(1, dtoValido)
        );

        assertTrue(excepcion.getMessage().contains("Cancelada"));
        
        verify(ordenCargaRepository, never()).save(any());
    }

    @Test
    void editarOrdenCarga_DebeLanzarExcepcionCuandoRemitoYaExisteEnOtraOrden() {
        var estado = new EstadoOrdenCarga();
        estado.setNombre("Pendiente");
        var orden = new OrdenCarga();
        orden.setId(1);
        orden.setEstadoOrdenCarga(estado);

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
        var estado = new EstadoOrdenCarga();
        estado.setNombre("Pendiente");
        var orden = new OrdenCarga();
        orden.setId(1);
        orden.setEstadoOrdenCarga(estado);

        OrdenCargaRequestDTO dtoSinLitros = new OrdenCargaRequestDTO(
            "REM-001", "COT-001", 1,1,1,1,2,1,1,1,1, 0.0, 0.0, null, null, null, 0.0, 0.0, "Obs", false, false
        );

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
        var estado = new EstadoOrdenCarga();
        estado.setNombre("Pendiente");
        var orden = new OrdenCarga();
        orden.setId(1);
        orden.setEstadoOrdenCarga(estado);

        OrdenCargaRequestDTO dtoIgual = new OrdenCargaRequestDTO(
            "REM-001", "COT-001", 1,1,1,1,1,1,1,1,1, 5000.0, 0.0, null, null, null, 0.0, 0.0, "Obs", false, false
        );

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
        var estado = new EstadoOrdenCarga();
        estado.setNombre("Entregada");
        var orden = new OrdenCarga();
        orden.setEstadoOrdenCarga(estado);

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
        var estado = new EstadoOrdenCarga();
        estado.setNombre("Pendiente");
        var orden = new OrdenCarga();
        orden.setNumeroRemito("REM-001");
        orden.setEstadoOrdenCarga(estado);

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


    // Reportar Entrega //

    @Test
    void reportarEntrega_DebeLanzarExcepcionCuandoOrdenNoExiste() {

        when(ordenCargaRepository.findById(99)).thenReturn(Optional.empty());

        ConfirmarEntregaRequestDTO dto = new ConfirmarEntregaRequestDTO(5000.0, "Sin obs");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> ordenCargaService.reportarEntrega(99, dto)
        );

        assertEquals("Orden no encontrada.",exception.getMessage());
        verify(ordenCargaRepository, never()).save(any());
    }

    @Test
    void reportarEntrega_DebeLanzarExcepcionCuandoEstadoNoPendienteDeConfirmacion() {
        var estado = new EstadoOrdenCarga();
        estado.setNombre("En Curso");

        var orden = new OrdenCarga();
        orden.setEstadoOrdenCarga(estado);

        when(ordenCargaRepository.findById(1)).thenReturn(Optional.of(orden));

        ConfirmarEntregaRequestDTO dto = new ConfirmarEntregaRequestDTO(5000.0, "Sin obs");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> ordenCargaService.reportarEntrega(1, dto)
        );

        assertEquals("La orden no está pendiente de confirmación de entrega.", exception.getMessage());
        verify(ordenCargaRepository, never()).save(any());
    }

    @Test
    void reportarEntrega_DebeLanzarExcepcionCuandoLitrosEntregadosSonCero() {
        var estado = new EstadoOrdenCarga();
        estado.setNombre("Pendiente de confirmacion de entrega");

        var orden = new OrdenCarga();
        orden.setEstadoOrdenCarga(estado);

        when(ordenCargaRepository.findById(1)).thenReturn(Optional.of(orden));

        ConfirmarEntregaRequestDTO dto = new ConfirmarEntregaRequestDTO(0.0, "Sin obs");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> ordenCargaService.reportarEntrega(1, dto)
        );

        assertEquals("Los litros entregados son obligatorios.", exception.getMessage());
        verify(ordenCargaRepository, never()).save(any());
    }

    @Test
    void reportarEntrega_DebeActualizarDatosCorrectamente() {
        var estado = new EstadoOrdenCarga();
        estado.setNombre("Pendiente de confirmacion de entrega");
        var orden = new OrdenCarga();
        orden.setEstadoOrdenCarga(estado);

        when(ordenCargaRepository.findById(1)).thenReturn(Optional.of(orden));

        ConfirmarEntregaRequestDTO dto = new ConfirmarEntregaRequestDTO(5000.0, "Sin obs");

        ordenCargaService.reportarEntrega(1, dto);

        assertEquals(5000.0, orden.getLitrosEntregados());
        assertEquals("Sin obs", orden.getObservaciones());
        assertNotNull(orden.getFechaEntregaReal());
        verify(ordenCargaRepository).save(orden);
    }

}
