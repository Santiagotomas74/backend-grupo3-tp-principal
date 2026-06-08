package com.blackmesaresearch.hytrac.service;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.blackmesaresearch.hytrac.model.core.LugarOperativo;
import com.blackmesaresearch.hytrac.model.reference.Localidad;
import com.blackmesaresearch.hytrac.model.reference.Provincia;
import com.blackmesaresearch.hytrac.repository.LugarOperativoRepository;

@ExtendWith(MockitoExtension.class)
public class LugarOperativoServiceTest {

    @Mock
    private LugarOperativoRepository lugarOperativoRepository;

    @InjectMocks
    private LugarOperativoService lugarOperativoService;

    // Helper
    private LugarOperativo crearLugar(int id, String nombre, boolean puedeDespachar, boolean puedeRecibir) {
        LugarOperativo l = new LugarOperativo();
        l.setId(id);
        l.setNombre(nombre);
        l.setPuedeDespachar(puedeDespachar);
        l.setPuedeRecibir(puedeRecibir);
        
        Provincia p = new Provincia();
        p.setId(1);
        p.setNombre("Buenos Aires");
        
        Localidad loc = new Localidad();
        loc.setId(10);
        loc.setNombre("San Miguel");
        loc.setProvincia(p);
        
        l.setLocalidad(loc);
        return l;
    }

    @Test
    void obtenerPlantas_DebeRetornarListaFiltrada() {
        LugarOperativo planta = crearLugar(1, "Planta A", true, false);
        when(lugarOperativoRepository.findActivosByPuedeDespachar()).thenReturn(List.of(planta));

        var resultado = lugarOperativoService.obtenerPlantas();

        assertEquals(1, resultado.size());
        assertEquals("Planta A", resultado.get(0).nombre());
        assertTrue(resultado.get(0).puedeDespachar());
    }

    @Test
    void obtenerEstacionesServicio_DebeRetornarListaFiltrada() {
        LugarOperativo est = crearLugar(2, "Estacion A", false, true);
        when(lugarOperativoRepository.findActivosByPuedeRecibir()).thenReturn(List.of(est));

        var resultado = lugarOperativoService.obtenerEstacionesServicio();

        assertEquals(1, resultado.size());
        assertEquals("Estacion A", resultado.get(0).nombre());
        assertTrue(resultado.get(0).puedeRecibir());
    }

    @Test
    void obtenerPlantasPorLocalidad_DebeFiltrarCorrectamente() {
        LugarOperativo plantaCorrecta = crearLugar(1, "Planta Local", true, false);
      
        LugarOperativo plantaErronea = crearLugar(2, "Planta Lejos", true, false);
        plantaErronea.getLocalidad().setId(99);

        when(lugarOperativoRepository.findActivosByPuedeDespachar()).thenReturn(List.of(plantaCorrecta, plantaErronea));

        var resultado = lugarOperativoService.obtenerPlantasPorLocalidad(10); // Buscamos localidad 10

        assertEquals(1, resultado.size());
        assertEquals("Planta Local", resultado.get(0).nombre());
    }

    @Test
    void obtenerEstacionesPorLocalidad_DebeManejarNulos() {
        LugarOperativo est = crearLugar(1, "Estacion Sin Localidad", false, true);
        est.setLocalidad(null);

        when(lugarOperativoRepository.findActivosByPuedeRecibir()).thenReturn(List.of(est));

        var resultado = lugarOperativoService.obtenerEstacionesPorLocalidad(10);

        assertTrue(resultado.isEmpty()); 
    }

    @Test
    void obtenerEstacionesPorLocalidad_DebeRetornarVacioSiNoHayMatch() {
        when(lugarOperativoRepository.findActivosByPuedeRecibir()).thenReturn(Collections.emptyList());

        var resultado = lugarOperativoService.obtenerEstacionesPorLocalidad(10);

        assertTrue(resultado.isEmpty());
    }
}
