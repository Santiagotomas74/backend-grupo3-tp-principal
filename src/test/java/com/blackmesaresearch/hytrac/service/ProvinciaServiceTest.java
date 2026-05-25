package com.blackmesaresearch.hytrac.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.blackmesaresearch.hytrac.dto.response.ProvinciaResponseDTO;
import com.blackmesaresearch.hytrac.model.reference.Provincia;
import com.blackmesaresearch.hytrac.repository.ProvinciaRepository;

@ExtendWith(MockitoExtension.class)
public class ProvinciaServiceTest {

    @Mock
    private ProvinciaRepository provinciaRepository;

    @InjectMocks
    private ProvinciaService provinciaService;

    // Obtener todas //

    @Test
    void obtenerTodas_DebeRetornarListaDeProvincias() {

        var provincia1 = new Provincia();
        provincia1.setId(1);
        provincia1.setNombre("Buenos Aires");

        var provincia2 = new Provincia();
        provincia2.setId(2);
        provincia2.setNombre("Córdoba");

        when(provinciaRepository.findAll()).thenReturn(List.of(provincia1, provincia2));

        List<ProvinciaResponseDTO> resultado = provinciaService.obtenerTodas();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Buenos Aires", resultado.get(0).nombre());
        assertEquals("Córdoba", resultado.get(1).nombre());
    }

    // Obtener por ID //
    @Test
    void obtenerPorId_DebeRetornarCuandoProvinciaExiste() {

        var provincia = new Provincia();
        provincia.setId(1);
        provincia.setNombre("Buenos Aires");

        when(provinciaRepository.findById(1)).thenReturn(Optional.of(provincia));

        ProvinciaResponseDTO resultado = provinciaService.obtenerPorId(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.id());
        assertEquals("Buenos Aires", resultado.nombre());
    }

    @Test
    void obtenerPorId_DebeLanzarExcepcionCuandoProvinciaNoExiste() {

        when(provinciaRepository.findById(99)).thenReturn(Optional.empty());

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> 
            provinciaService.obtenerPorId(99)
        );

        assertEquals("Provincia no encontrada.", excepcion.getMessage());
    }

    
}
