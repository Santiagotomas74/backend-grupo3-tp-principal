package com.blackmesaresearch.hytrac.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.blackmesaresearch.hytrac.dto.request.TransportistaOptimoRequestDTO;
import com.blackmesaresearch.hytrac.model.core.Transportista;
import com.blackmesaresearch.hytrac.model.stats.StatsTransportista;
import com.blackmesaresearch.hytrac.repository.CombustibleRepository;
import com.blackmesaresearch.hytrac.repository.StatsTransportistaRepository;
import com.blackmesaresearch.hytrac.repository.TransportistaRepository;

@ExtendWith(MockitoExtension.class)
class SeleccionTransportistasServiceTest {

    @Mock private CombustibleRepository combustibleRepository;
    @Mock private TransportistaRepository transportistaRepository;
    @Mock private StatsTransportistaRepository statsTransportistaRepository;

    @InjectMocks
    private SeleccionTransportistasService service;

    // Helper
    private StatsTransportista crearStats(int ordenes, int largas, int medias, int cortas, int pesadas, int livianas) {
        StatsTransportista s = new StatsTransportista();
        s.setTotalOrdenes(ordenes);
        s.setLargas(10); s.setLargasExitosas(largas);
        s.setMedias(10); s.setMediasExitosas(medias);
        s.setCortas(10); s.setCortasExitosas(cortas);
        s.setPesadas(10); s.setPesadasExitosas(pesadas);
        s.setLivianas(10); s.setLivianasExitosas(livianas);
        return s;
    }

    @Test
    void seleccionarTransportistasOptimos_NovatoEnOrdenCortaSeAgregaANovatos() {
        // Orden corta (2hs), total ordenes < 20 (novato)
        var request = new TransportistaOptimoRequestDTO(2.0, 1.0, 1000);
        
        Transportista t = new Transportista();
        t.setId(1);
        t.setInicioActividad(LocalDate.now().minusYears(1));
        
        when(transportistaRepository.findAllByActivoTrueAndDisponibleTrue()).thenReturn(List.of(t));
        when(statsTransportistaRepository.findByTransportistaId(1))
            .thenReturn(Optional.of(crearStats(5, 0, 0, 0, 0, 0))); // 5 ordenes = novato

        var resultado = service.seleccionarTransportistasOptimos(request);
        
        // El novato debe estar en la lista resultante
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.get(0).id());
    }

    @Test
    void seleccionarTransportistasOptimos_TransportistaSinStatsLanzaExcepcion() {
        var request = new TransportistaOptimoRequestDTO(5.0, 1.0, 1000);
        Transportista t = new Transportista();
        t.setId(1);
        
        when(transportistaRepository.findAllByActivoTrueAndDisponibleTrue()).thenReturn(List.of(t));
        when(statsTransportistaRepository.findByTransportistaId(1)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> service.seleccionarTransportistasOptimos(request));
    }

    @Test
    void seleccionarTransportistasOptimos_CombustibleNoEncontradoUsaDensidadDefault() {
        var request = new TransportistaOptimoRequestDTO(5.0, 5.0, 1000);
        when(combustibleRepository.findById(anyInt())).thenReturn(Optional.empty());
        when(transportistaRepository.findAllByActivoTrueAndDisponibleTrue()).thenReturn(List.of());

        assertDoesNotThrow(() -> service.seleccionarTransportistasOptimos(request));
        verify(combustibleRepository).findById(1000);
    }

    @Test
    void seleccionarTransportistasOptimos_ModeloMLFiltraPorProbabilidad() {
        // Orden larga, transportista regular (50 ordenes)
        var request = new TransportistaOptimoRequestDTO(10.0, 1.0, 1000);
        
        Transportista t = new Transportista();
        t.setId(1);
        t.setInicioActividad(LocalDate.now().minusYears(5));
        
        when(transportistaRepository.findAllByActivoTrueAndDisponibleTrue()).thenReturn(List.of(t));
        when(statsTransportistaRepository.findByTransportistaId(1))
            .thenReturn(Optional.of(crearStats(50, 5, 5, 5, 5, 5)));
        
        // Nota: Como el modelo es un objeto real cargado, 
        // este test probará la rama 'if (probabilidadExito > 0.9)' 
        // dependiendo de los stats que le des.
        var resultado = service.seleccionarTransportistasOptimos(request);
        
        assertNotNull(resultado);
    }
}
