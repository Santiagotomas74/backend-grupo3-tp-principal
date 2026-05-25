package com.blackmesaresearch.hytrac.service;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.blackmesaresearch.hytrac.dto.request.ReportarIncidenciaRequestDTO;
import com.blackmesaresearch.hytrac.dto.response.IncidenciaResponseDTO;
import com.blackmesaresearch.hytrac.model.core.Incidencia;
import com.blackmesaresearch.hytrac.model.core.OrdenCarga;
import com.blackmesaresearch.hytrac.model.core.Usuario;
import com.blackmesaresearch.hytrac.model.lookup.TipoIncidencia;
import com.blackmesaresearch.hytrac.repository.IncidenciaRepository;
import com.blackmesaresearch.hytrac.repository.OrdenCargaRepository;
import com.blackmesaresearch.hytrac.repository.TipoIncidenciaRepository;
import com.blackmesaresearch.hytrac.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
public class IncidenciaServiceTest {

    @Mock
    private IncidenciaRepository incidenciaRepository;

    @Mock
    private OrdenCargaRepository ordenCargaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TipoIncidenciaRepository tipoIncidenciaRepository;

    @InjectMocks
    private IncidenciaService incidenciaService;


    // Obtener Todas //

    @Test
    void obtenerTodas_DebeRetornarListaDeIncidencias() {

        var orden = new OrdenCarga();
        orden.setNumeroRemito("REM-001");

        var usuario = new Usuario();
        usuario.setLegajo("LEG-001");

        var tipo = new TipoIncidencia();
        tipo.setNombre("Cancha Incendiada");

        var incidencia = new Incidencia();
        incidencia.setId(1);
        incidencia.setOrden(orden);
        incidencia.setUsuarioRegistro(usuario);
        incidencia.setTipoIncidencia(tipo);
        incidencia.setDescripcion("26/06/2011");
        incidencia.setResuelto(false);

        when(incidenciaRepository.findAll()).thenReturn(List.of(incidencia));

        List<IncidenciaResponseDTO> resultado = incidenciaService.obtenerTodas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("REM-001", resultado.get(0).ordenNumeroRemito());
        assertFalse(resultado.get(0).resuelto());
    }

    // Reportar Incidencia //
    @Test
    void reportarIncidencia_DebeGuardarCorrectamente() {
        var dto = new ReportarIncidenciaRequestDTO("REM-123", "LEG-001", "Demora", "Trafico en la ruta");

        var orden = new OrdenCarga();
        when(ordenCargaRepository.findByNumeroRemito("REM-123")).thenReturn(Optional.of(orden));

        var usuario = new Usuario();
        when(usuarioRepository.findByLegajo("LEG-001")).thenReturn(Optional.of(usuario));

        var tipoIncidencia = new TipoIncidencia();
        when(tipoIncidenciaRepository.findByNombre("Demora")).thenReturn(Optional.of(tipoIncidencia));

        ArgumentCaptor<Incidencia> captor = ArgumentCaptor.forClass(Incidencia.class);

        incidenciaService.reportarIncidencia(dto);

        verify(incidenciaRepository,times(1)).save(captor.capture());
        Incidencia guardada = captor.getValue();

        assertEquals(orden, guardada.getOrden());
        assertEquals(usuario, guardada.getUsuarioRegistro());
        assertEquals(tipoIncidencia, guardada.getTipoIncidencia());
        assertEquals("Trafico en la ruta", guardada.getDescripcion());
        assertFalse(guardada.getResuelto());
        assertNotNull(guardada.getFechaIncidente());
    }

    @Test
    void reportarIncidencia_DebeLanzarExcepcionCuandoOrdenNoExiste() {
        var dto = new ReportarIncidenciaRequestDTO("REM-999", "LEG-001", "Demora", "Trafico en la ruta");

        when(ordenCargaRepository.findByNumeroRemito("REM-999")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> incidenciaService.reportarIncidencia(dto));

        assertEquals("Orden no encontrada.",exception.getMessage());

        verify(incidenciaRepository, never()).save(any());
    }

    
}
