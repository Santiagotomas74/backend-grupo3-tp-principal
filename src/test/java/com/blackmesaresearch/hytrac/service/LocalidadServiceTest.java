package com.blackmesaresearch.hytrac.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.blackmesaresearch.hytrac.model.reference.Localidad;
import com.blackmesaresearch.hytrac.model.reference.Provincia;
import com.blackmesaresearch.hytrac.repository.LocalidadRepository;

@ExtendWith(MockitoExtension.class)
public class LocalidadServiceTest {

    @Mock private LocalidadRepository localidadRepository;
    @InjectMocks private LocalidadService localidadService;

    @Test
    void obtenerPorProvincia_DebeRetornarListaFiltrada() {
        Provincia p = new Provincia(); p.setId(1); p.setNombre("Buenos Aires");
        Localidad l = new Localidad(); 
        l.setId(10); l.setNombre("San Miguel"); l.setCodigoPostal("1663"); l.setProvincia(p);

        when(localidadRepository.findByProvinciaId(1)).thenReturn(List.of(l));

        var result = localidadService.obtenerPorProvincia(1);

        assertEquals(1, result.size());
        assertEquals("San Miguel", result.get(0).nombre());
        assertEquals("Buenos Aires", result.get(0).provinciaNombre());
    }
}