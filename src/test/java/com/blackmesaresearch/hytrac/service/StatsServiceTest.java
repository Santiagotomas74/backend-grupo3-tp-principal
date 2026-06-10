package com.blackmesaresearch.hytrac.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.blackmesaresearch.hytrac.model.core.LugarOperativo;
import com.blackmesaresearch.hytrac.model.core.Transportista;
import com.blackmesaresearch.hytrac.model.core.Usuario;
import com.blackmesaresearch.hytrac.model.lookup.Rol;
import com.blackmesaresearch.hytrac.model.stats.*;
import com.blackmesaresearch.hytrac.repository.*;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock private StatsLugarRepository statsLugarRepository;
    @Mock private StatsTransportistaRepository statsTransportistaRepository;
    @Mock private StatsSistemaRepository statsSistemaRepository;
    @Mock private IncidenciaRepository incidenciaRepository;
    @Mock private OrdenCargaRepository ordenCargaRepository;

    @InjectMocks
    private StatsService statsService;

    @BeforeEach
    void setUp() {
        org.springframework.test.util.ReflectionTestUtils.setField(statsService, "incidenciaRepository", incidenciaRepository);
        org.springframework.test.util.ReflectionTestUtils.setField(statsService, "ordenCargaRepository", ordenCargaRepository);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private LugarOperativo lugar(int id, String nombre) {
        var l = new LugarOperativo();
        l.setId(id);
        l.setNombre(nombre);
        return l;
    }

    private Transportista transportista(int id) {
        var t = new Transportista();
        t.setId(id);
        return t;
    }

    private StatsLugar statsLugar(int lugarId, int despachos, int recepciones) {
        var s = new StatsLugar();
        s.setId(1);
        s.setLugar(lugar(lugarId, "Planta"));
        s.setDespachos(despachos);
        s.setRecepciones(recepciones);
        return s;
    }

    private StatsTransportista statsTransportista(int transportistaId,
            int totalOrdenes,
            int largasExitosas, int mediasExitosas, int cortasExitosas,
            int largas, int medias, int cortas,
            int pesadas, int pesadasExitosas,
            int livianas, int livianasExitosas) {
        var s = new StatsTransportista();
        s.setId(1);
        s.setTransportista(transportista(transportistaId));
        s.setTotalOrdenes(totalOrdenes);
        s.setLargasExitosas(largasExitosas);
        s.setMediasExitosas(mediasExitosas);
        s.setCortasExitosas(cortasExitosas);
        s.setLargas(largas);
        s.setMedias(medias);
        s.setCortas(cortas);
        s.setPesadas(pesadas);
        s.setPesadasExitosas(pesadasExitosas);
        s.setLivianas(livianas);
        s.setLivianasExitosas(livianasExitosas);
        return s;
    }

    private StatsSistema statsSistema(int totalOrdenes,
            int largasExitosas, int mediasExitosas, int cortasExitosas) {
        var s = new StatsSistema();
        s.setId(1L);
        s.setTotalOrdenes(totalOrdenes);
        s.setLargasExitosas(largasExitosas);
        s.setMediasExitosas(mediasExitosas);
        s.setCortasExitosas(cortasExitosas);
        s.setLargas(0);
        s.setMedias(0);
        s.setCortas(0);
        s.setPesadas(0);
        s.setPesadasExitosas(0);
        s.setLivianas(0);
        s.setLivianasExitosas(0);
        return s;
    }

    // ================================================================
    // getStatsLugar
    // ================================================================

    @Test
    void getStatsLugar_DebeRetornarDtoCorrecto() {
        when(statsLugarRepository.findByLugarId(1))
            .thenReturn(Optional.of(statsLugar(1, 10, 5)));

        var resultado = statsService.getStatsLugar(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.idLugar());
        assertEquals(10L, resultado.totalDespachos());
        assertEquals(5L, resultado.totalRecepciones());
    }

    @Test
    void getStatsLugar_DebeRetornarCerosCuandoDespachosYRecepcionessonNull() {
        var stats = new StatsLugar();
        stats.setId(1);
        stats.setLugar(lugar(1, "Planta"));
        stats.setDespachos(null);
        stats.setRecepciones(null);
        when(statsLugarRepository.findByLugarId(1)).thenReturn(Optional.of(stats));

        var resultado = statsService.getStatsLugar(1);

        assertEquals(0L, resultado.totalDespachos());
        assertEquals(0L, resultado.totalRecepciones());
    }

    @Test
    void getStatsLugar_DebeRetornarCerosCuandoLugarEsNull() {
        var stats = new StatsLugar();
        stats.setId(1);
        stats.setLugar(null);
        stats.setDespachos(5);
        stats.setRecepciones(3);
        when(statsLugarRepository.findByLugarId(1)).thenReturn(Optional.of(stats));

        var resultado = statsService.getStatsLugar(1);

        assertEquals(0, resultado.idLugar());
        assertEquals(5L, resultado.totalDespachos());
    }

    @Test
    void getStatsLugar_DebeLanzarExcepcionCuandoLugarNoExiste() {
        when(statsLugarRepository.findByLugarId(99)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> statsService.getStatsLugar(99));

        assertTrue(ex.getMessage().contains("99"));
    }

    // ================================================================
    // getStatsTransportista
    // ================================================================

    @Test
    void getStatsTransportista_DebeRetornarDtoCorrecto() {
        var stats = statsTransportista(1, 10, 3, 3, 2, 4, 4, 3, 2, 2, 8, 7);
        when(statsTransportistaRepository.findByTransportistaId(1))
            .thenReturn(Optional.of(stats));
        when(incidenciaRepository.countByTransportistaId(1)).thenReturn(2L);

        var resultado = statsService.getStatsTransportista(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.idTransportista());
        assertEquals(10, resultado.totalOrdenes());
        assertEquals(8, resultado.totalOrdenesExitosas()); // 3+3+2
        assertEquals(2, resultado.totalIncidencias());
    }

    @Test
    void getStatsTransportista_DebeRetornarCeroIncidenciasCuandoTransportistaIdEsCero() {
        var stats = new StatsTransportista();
        stats.setId(1);
        stats.setTransportista(null); // sin transportista
        stats.setTotalOrdenes(5);
        stats.setLargasExitosas(1);
        stats.setMediasExitosas(1);
        stats.setCortasExitosas(1);
        stats.setLargas(2);
        stats.setMedias(2);
        stats.setCortas(1);
        stats.setPesadas(0);
        stats.setPesadasExitosas(0);
        stats.setLivianas(0);
        stats.setLivianasExitosas(0);
        when(statsTransportistaRepository.findByTransportistaId(1))
            .thenReturn(Optional.of(stats));

        var resultado = statsService.getStatsTransportista(1);

        assertEquals(0, resultado.idTransportista());
        assertEquals(0, resultado.totalIncidencias()); // no se llama al repo de incidencias
        verify(incidenciaRepository, never()).countByTransportistaId(anyInt());
    }

    @Test
    void getStatsTransportista_DebeLanzarExcepcionCuandoTransportistaNoExiste() {
        when(statsTransportistaRepository.findByTransportistaId(99))
            .thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> statsService.getStatsTransportista(99));

        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    void getStatsTransportista_DebeCalcularExitosasCorrectamente() {
        // largas=2, medias=3, cortas=5 → total exitosas = 10
        var stats = statsTransportista(1, 20, 2, 3, 5, 5, 6, 9, 3, 2, 17, 15);
        when(statsTransportistaRepository.findByTransportistaId(1))
            .thenReturn(Optional.of(stats));
        when(incidenciaRepository.countByTransportistaId(1)).thenReturn(0L);

        var resultado = statsService.getStatsTransportista(1);

        assertEquals(10, resultado.totalOrdenesExitosas());
    }

    // ================================================================
    // getStatsSistema
    // ================================================================

    @Test
    void getStatsSistema_DebeRetornarDtoCorrecto() {
        // 10 totales, 3+3+2=8 entregadas, 2 canceladas
        var stats = statsSistema(10, 3, 3, 2);
        when(statsSistemaRepository.findAll()).thenReturn(List.of(stats));
        when(ordenCargaRepository.count()).thenReturn(10L);
        when(ordenCargaRepository.countByEstadoOrdenCarga_Id(3)).thenReturn(8L); // entregadas
        when(ordenCargaRepository.countByEstadoOrdenCarga_Id(4)).thenReturn(2L); // canceladas

        var resultado = statsService.getStatsSistema();

        assertNotNull(resultado);
        assertEquals(10, resultado.totalOrdenes());
        assertEquals(8, resultado.totalEntregadas());  // 3+3+2
        assertEquals(2, resultado.totalCanceladas());  // 10 - 8
        assertEquals(0, resultado.totalEnCurso());     // 10 - 8 - 2
    }

    @Test
    void getStatsSistema_DebeCalcularEnCursoCorrectamente() {
        // 20 totales, 10 entregadas (5+3+2), 5 canceladas, 5 en curso
        var stats = statsSistema(20, 5, 3, 2);
        when(statsSistemaRepository.findAll()).thenReturn(List.of(stats));
        when(ordenCargaRepository.count()).thenReturn(20L);
        when(ordenCargaRepository.countByEstadoOrdenCarga_Id(3)).thenReturn(10L);
        when(ordenCargaRepository.countByEstadoOrdenCarga_Id(4)).thenReturn(5L);

        var resultado = statsService.getStatsSistema();

        assertEquals(20, resultado.totalOrdenes());
        assertEquals(10, resultado.totalEntregadas());
        assertEquals(10, resultado.totalCanceladas()); // 20 - 10
        assertEquals(5, resultado.totalEnCurso());     // 20 - 10 - 5
    }

    @Test
    void getStatsSistema_DebeRetornarCerosCuandoNoHayOrdenes() {
        var stats = statsSistema(0, 0, 0, 0);
        when(statsSistemaRepository.findAll()).thenReturn(List.of(stats));
        when(ordenCargaRepository.count()).thenReturn(0L);
        when(ordenCargaRepository.countByEstadoOrdenCarga_Id(3)).thenReturn(0L);
        when(ordenCargaRepository.countByEstadoOrdenCarga_Id(4)).thenReturn(0L);

        var resultado = statsService.getStatsSistema();

        assertEquals(0, resultado.totalOrdenes());
        assertEquals(0, resultado.totalEntregadas());
        assertEquals(0, resultado.totalCanceladas());
        assertEquals(0, resultado.totalEnCurso());
    }
}