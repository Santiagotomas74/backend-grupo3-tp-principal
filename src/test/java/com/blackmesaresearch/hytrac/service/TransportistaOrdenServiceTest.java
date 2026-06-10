package com.blackmesaresearch.hytrac.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import com.blackmesaresearch.hytrac.model.core.OrdenCarga;
import com.blackmesaresearch.hytrac.model.core.Usuario;
import com.blackmesaresearch.hytrac.model.lookup.EstadoOrdenCarga;
import com.blackmesaresearch.hytrac.repository.EstadoOrdenCargaRepository;
import com.blackmesaresearch.hytrac.repository.OrdenCargaRepository;
import com.blackmesaresearch.hytrac.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
public class TransportistaOrdenServiceTest {

    @Mock
    private OrdenCargaRepository ordenCargaRepository;

    @Mock
    private EstadoOrdenCargaRepository estadoRepository;

    @Mock
    private AuditoriaOrdenService auditoriaOrdenService;

    @Mock 
    private NotificacionService notificacionService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private TransportistaOrdenService transportistaOrdenService;

   
    
    // =========================
    // INICIAR VIAJE
    // =========================

    @Test
    void iniciarViaje_DebeLanzarExcepcionCuandoOrdenNoExiste() {

        when(ordenCargaRepository.findById(99)).thenReturn(Optional.empty());
        

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> transportistaOrdenService.iniciarViaje(99, "LEG-001")
        );

        assertEquals("Orden no encontrada.", exception.getMessage());
        verify(ordenCargaRepository, never()).save(any());
    }

    @Test 
    void iniciarViaje_DebeLanzarExcepcionCuandoEstadoNoEsPendiente() {

        var estado = new EstadoOrdenCarga();
        estado.setNombre("En Curso");
        var orden = new OrdenCarga();
        orden.setEstadoOrdenCarga(estado);

        when(ordenCargaRepository.findById(1)).thenReturn(Optional.of(orden));

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> transportistaOrdenService.iniciarViaje(1, "LEG-001")
        );

        assertEquals(
            "La orden no está en estado pendiente.",
            exception.getMessage()
        );

        verify(ordenCargaRepository, never()).save(any());  
    }

    @Test
    void iniciarViaje_DebeCambiarEstadoAPendienteDeInicioDeViaje() {

        var estadoActual = new EstadoOrdenCarga();
        estadoActual.setNombre("Pendiente");

        var orden = new OrdenCarga();
        orden.setEstadoOrdenCarga(estadoActual);

        var operador = new Usuario();
        operador.setLegajo("LEG-OP");
        orden.setOperador(operador);
            

        when(ordenCargaRepository.findById(1)).thenReturn(Optional.of(orden));

        var nuevoEstado = new EstadoOrdenCarga();
        nuevoEstado.setNombre("Pendiente de inicio de viaje");
        when(estadoRepository.findByNombre("Pendiente de inicio de viaje"))
            .thenReturn(Optional.of(nuevoEstado));

        transportistaOrdenService.iniciarViaje(1, "LEG-001");

        assertEquals("Pendiente de inicio de viaje", orden.getEstadoOrdenCarga().getNombre());
        assertNotNull(orden.getFechaSalidaPlanta());
        verify(ordenCargaRepository).save(orden);
        verify(auditoriaOrdenService, times(1)).registrarCambioEstado(
            isNull(), anyString(), anyString(), eq("LEG-001"), isNull(), anyString()
        );
    }

    // Notificar Entrega //
    
    @Test
    void notificarEntrega_DebeLanzarExcepcionCuandoOrdenNoExiste() {

        when(ordenCargaRepository.findById(99)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> transportistaOrdenService.notificarEntrega(99, "LEG-001", "123456")
        );

        assertEquals("Orden no encontrada.", exception.getMessage());
        verify(ordenCargaRepository, never()).save(any());
    }

    @Test
    void notificarEntrega_DebeLanzarExcepcionCuandoEstadoNoEsEnCurso() {

        var estado = new EstadoOrdenCarga();
        estado.setNombre("Pendiente de inicio de viaje");
        var orden = new OrdenCarga();
        orden.setEstadoOrdenCarga(estado);

        when(ordenCargaRepository.findById(1)).thenReturn(Optional.of(orden));

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> transportistaOrdenService.notificarEntrega(1, "LEG-001", "123456")
        );

        assertEquals(
            "La orden no está en curso.",
            exception.getMessage()
        );

        verify(ordenCargaRepository, never()).save(any());
    }

    @Test
    void notificarEntrega_DebeCambiarEstadoAPendienteDeConfirmacion() {

        var estadoActual = new EstadoOrdenCarga();
        estadoActual.setNombre("En Curso");
        var orden = new OrdenCarga();
        orden.setEstadoOrdenCarga(estadoActual);
        orden.setCodigoConfirmacion("123456");

        var operador = new Usuario();
        operador.setLegajo("LEG-OP");
        orden.setOperador(operador);

        when(ordenCargaRepository.findById(1)).thenReturn(Optional.of(orden));

        var nuevoEstado = new EstadoOrdenCarga();
        nuevoEstado.setNombre("Pendiente de confirmacion de entrega");
        when(estadoRepository.findByNombre("Pendiente de confirmacion de entrega"))
            .thenReturn(Optional.of(nuevoEstado));

        transportistaOrdenService.notificarEntrega(1, "LEG-001", "123456");

        assertEquals(
            "Pendiente de confirmacion de entrega",
            orden.getEstadoOrdenCarga().getNombre()
        );
        verify(ordenCargaRepository).save(orden);
        verify(auditoriaOrdenService, times(1)).registrarCambioEstado(
            isNull(), anyString(), anyString(), eq("LEG-001"), isNull(), anyString()
        );
    }

    // Obtener Orden Pendiente //

    @Test
    void obtenerOrdenPendiente_DebeLanzarExcepcionCuandoNoHayOrdenPendiente() {

        when(ordenCargaRepository.findByTransportista_Usuario_LegajoAndConfirmadoTrueAndEstadoOrdenCarga_Nombre("LEG-001", "Pendiente"))
        .thenReturn(List.of());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> transportistaOrdenService.obtenerOrdenPendiente("LEG-001")
        );

        assertEquals("No hay órdenes pendientes.", exception.getMessage());
    }

}
