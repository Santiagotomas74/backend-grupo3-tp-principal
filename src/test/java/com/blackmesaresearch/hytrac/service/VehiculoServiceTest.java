package com.blackmesaresearch.hytrac.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.blackmesaresearch.hytrac.model.core.Vehiculo;
import com.blackmesaresearch.hytrac.repository.VehiculoRepository;

@ExtendWith(MockitoExtension.class)
public class VehiculoServiceTest {

    @Mock private VehiculoRepository vehiculoRepository;
    @InjectMocks private VehiculoService vehiculoService;

    @Test
    void obtenerCamiones_DebeRetornarListaDeDTOs() {
        Vehiculo v = new Vehiculo();
        v.setId(1);
        v.setPatente("ABC-123");
        v.setMarca("Scania");
        v.setModelo("R450");
        v.setPeso_maximo_admitido(45000.0);

        when(vehiculoRepository.findAll()).thenReturn(List.of(v));

        var result = vehiculoService.obtenerCamiones();

        assertEquals(1, result.size());
        assertEquals("ABC-123", result.get(0).patente());
        assertEquals("Scania", result.get(0).marca());
    }
}
