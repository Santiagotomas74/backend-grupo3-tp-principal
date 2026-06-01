package com.blackmesaresearch.hytrac.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.blackmesaresearch.hytrac.dto.response.AuditoriaOrdenResponseDTO;
import com.blackmesaresearch.hytrac.model.core.AuditoriaEstado;
import com.blackmesaresearch.hytrac.model.core.OrdenCarga;
import com.blackmesaresearch.hytrac.model.core.Usuario;
import com.blackmesaresearch.hytrac.model.lookup.EstadoOrdenCarga;
import com.blackmesaresearch.hytrac.repository.AuditoriaEstadoRepository;
import com.blackmesaresearch.hytrac.repository.EstadoOrdenCargaRepository;
import com.blackmesaresearch.hytrac.repository.OrdenCargaRepository;
import com.blackmesaresearch.hytrac.repository.UsuarioRepository;

@ExtendWith (MockitoExtension.class)
public class AuditoriaOrdenServiceTest {

    @Mock private AuditoriaEstadoRepository auditoriaEstadoRepository;
    @Mock private OrdenCargaRepository ordenCargaRepository;
    @Mock private EstadoOrdenCargaRepository estadoOrdenCargaRepository;
    @Mock private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AuditoriaOrdenService auditoriaOrdenService;
    private OrdenCarga ordenMock;
    private Usuario usuarioMock;
    private EstadoOrdenCarga estadoAnteriorMock;
    private EstadoOrdenCarga estadoNuevoMock;

    @BeforeEach
    public void setUp() {
        ordenMock = new OrdenCarga();
        ordenMock.setNumeroRemito("REM-123");

        estadoAnteriorMock = new EstadoOrdenCarga();
        estadoAnteriorMock.setNombre("Pendiente");

        estadoNuevoMock = new EstadoOrdenCarga();
        estadoNuevoMock.setNombre("En Curso");

        usuarioMock = new Usuario();
        usuarioMock.setLegajo("LEG-001");
}

    // Registrar Cambio De Estado //

    @Test
    public void RegistrarCambioEstado_RegistraCambioSinUsuario() {
        when(ordenCargaRepository.findByNumeroRemito("REM-123")).thenReturn(Optional.of(ordenMock));
        when(estadoOrdenCargaRepository.findByNombre("Pendiente")).thenReturn(Optional.of(estadoAnteriorMock));
        when(estadoOrdenCargaRepository.findByNombre("En Curso")).thenReturn(Optional.of(estadoNuevoMock));
        
        auditoriaOrdenService.registrarCambioEstado("REM-123", "Pendiente", "En Curso", null, null, "Prueba de cambio de estado sin usuario");

        verify(auditoriaEstadoRepository, times(1)).save(any(AuditoriaEstado.class));
    }

    @Test
    public void RegistrarCambioEstado_RegistraCambioConUsuario() {
        when(ordenCargaRepository.findByNumeroRemito("REM-123")).thenReturn(Optional.of(ordenMock));
        when(estadoOrdenCargaRepository.findByNombre("Pendiente")).thenReturn(Optional.of(estadoAnteriorMock));
        when(estadoOrdenCargaRepository.findByNombre("En Curso")).thenReturn(Optional.of(estadoNuevoMock));
        when(usuarioRepository.findByLegajo("LEG-001")).thenReturn(Optional.of(usuarioMock));
        when(usuarioRepository.findByLegajo("LEG-002")).thenReturn(Optional.of(usuarioMock));
        
        auditoriaOrdenService.registrarCambioEstado("REM-123", "Pendiente", "En Curso", "LEG-001", "LEG-002", "Prueba de cambio de estado con usuario");

        verify(auditoriaEstadoRepository, times(1)).save(any(AuditoriaEstado.class));
    }

    @Test
    public void registrarCambioEstado_DebeLanzarExcepcionSiOrdenNoExiste() {
        when(ordenCargaRepository.findByNumeroRemito("REM-999")).thenReturn(Optional.empty());

        IllegalArgumentException exception = org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            auditoriaOrdenService.registrarCambioEstado("REM-999", "Pendiente", "En Curso", null, null, "No Existo");
        });

        assertEquals("Orden no encontrada.", exception.getMessage());
        verify(auditoriaEstadoRepository, times(0)).save(any());
    }

    @Test
    public void registrarCambioEstado_DebeLanzarExcepcionSiEstadoAnteriorNoExiste() {
        when(ordenCargaRepository.findByNumeroRemito("REM-123")).thenReturn(Optional.of(ordenMock));
        when(estadoOrdenCargaRepository.findByNombre("Falso")).thenReturn(Optional.empty());

        IllegalArgumentException exception = org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            auditoriaOrdenService.registrarCambioEstado("REM-123", "Falso", "En Curso", null, null, "Estado Anterior No Existe");
        });

        assertEquals("Estado anterior no encontrado.", exception.getMessage());
        verify(auditoriaEstadoRepository, times(0)).save(any());
    }

    @Test
    public void registrarCambioEstado_DebeLanzarExcepcionSiEstadoNuevoNoExiste() {
        when(ordenCargaRepository.findByNumeroRemito("REM-123")).thenReturn(Optional.of(ordenMock));
        when(estadoOrdenCargaRepository.findByNombre("Pendiente")).thenReturn(Optional.of(estadoAnteriorMock));
        when(estadoOrdenCargaRepository.findByNombre("Falso")).thenReturn(Optional.empty());

        IllegalArgumentException exception = org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            auditoriaOrdenService.registrarCambioEstado("REM-123", "Pendiente", "Falso", null, null, "Estado Nuevo No Existe");
        });

        assertEquals("Estado nuevo no encontrado.", exception.getMessage());
        verify(auditoriaEstadoRepository, times(0)).save(any());
    }

    @Test
    public void registrarCambioEstado_DebeLanzarExcepcionSiSolicitanteNoExiste() {
        when(ordenCargaRepository.findByNumeroRemito("REM-123")).thenReturn(Optional.of(ordenMock));
        when(estadoOrdenCargaRepository.findByNombre("Pendiente")).thenReturn(Optional.of(estadoAnteriorMock));
        when(estadoOrdenCargaRepository.findByNombre("En Curso")).thenReturn(Optional.of(estadoNuevoMock));
        when(usuarioRepository.findByLegajo("LEG-999")).thenReturn(Optional.empty());

        IllegalArgumentException exception = org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            auditoriaOrdenService.registrarCambioEstado("REM-123", "Pendiente", "En Curso", "LEG-999", null, "Solicitante No Existe");
        });

        assertEquals("Solicitante no encontrado.", exception.getMessage());
        verify(auditoriaEstadoRepository, times(0)).save(any());
    }

    // Obtener Auditoria //

    @Test
    public void obtenerAuditoria_DebeRetornarListaDTO() {
        AuditoriaEstado auditoriaMock = new AuditoriaEstado();
        auditoriaMock.setOrden(ordenMock);
        auditoriaMock.setEstadoAnterior(estadoAnteriorMock);
        auditoriaMock.setEstadoNuevo(estadoNuevoMock);
        auditoriaMock.setFechaCambio(LocalDateTime.now());

        when(auditoriaEstadoRepository.findAll()).thenReturn(List.of(auditoriaMock));

        List<AuditoriaOrdenResponseDTO> resultado = auditoriaOrdenService.obtenerAuditoria();

        assertEquals(1, resultado.size());
        assertFalse(resultado.isEmpty());
        assertEquals("REM-123", resultado.get(0).ordenNumeroRemito());
    }

    // Obtener por Numero Remito //

    @Test
    public void obtenerPorNumeroRemito_DebeRetornarListaDTO() {
        AuditoriaEstado auditoriaMock = new AuditoriaEstado();
        auditoriaMock.setOrden(ordenMock);
        auditoriaMock.setEstadoAnterior(estadoAnteriorMock);
        auditoriaMock.setEstadoNuevo(estadoNuevoMock);
        auditoriaMock.setFechaCambio(LocalDateTime.now());

        when(auditoriaEstadoRepository.findAll()).thenReturn(List.of(auditoriaMock));

        List<AuditoriaOrdenResponseDTO> resultado = auditoriaOrdenService.obtenerPorNumeroRemito("REM-123");

        assertEquals(1, resultado.size());
        assertFalse(resultado.isEmpty());
        assertEquals("REM-123", resultado.get(0).ordenNumeroRemito());

    }

    @Test
    public void obtenerPorNumeroRemito_DebeRetornarListaVaciaSiNoHayAuditoria() {
        when(auditoriaEstadoRepository.findAll()).thenReturn(List.of());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            auditoriaOrdenService.obtenerPorNumeroRemito("REM-999")
        );

        assertEquals("No se encontraron auditorías para el remito.", exception.getMessage());
    }
}
