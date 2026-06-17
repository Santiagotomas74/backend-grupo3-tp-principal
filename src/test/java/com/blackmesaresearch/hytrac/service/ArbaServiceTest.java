package com.blackmesaresearch.hytrac.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.blackmesaresearch.hytrac.dto.request.GenerarCotRequestDTO;
import com.blackmesaresearch.hytrac.dto.response.CotResponseDTO;

@ExtendWith(MockitoExtension.class)
public class ArbaServiceTest {

    @InjectMocks
    private ArbaService arbaService;

    private GenerarCotRequestDTO dtoValido;

    @BeforeEach
    void setUp() {
        dtoValido = new GenerarCotRequestDTO(
            "Planta Felfort",    
            "Destino Showmatch", 
            5000.0,              
            0.74,               
            150000.0,            
            "Nafta Súper",      
            "REM-2026-TEST"      
        );
    }

    // Generar Cot

    @Test
    void generarCot_DebeGenerarCotYRetornarDtoCorrecto() {
        
        CotResponseDTO resultado = arbaService.generarCot(dtoValido);

     
        assertNotNull(resultado);
        assertNotNull(resultado.cot());
        assertTrue(resultado.cot().startsWith("COT-"));
        assertEquals("COT generado correctamente", resultado.mensaje()); 
        assertNotNull(resultado.fechaGeneracion());
    }

    @Test
    void generarCot_DebeLanzarExcepcionCuandoOrigenEsInvalido() {
        var dtoSinOrigen = new GenerarCotRequestDTO(
            "", "Destino", 5000.0, 0.74, 150000.0, "Nafta", "REM-123"
        );

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class,
            () -> arbaService.generarCot(dtoSinOrigen)
        );

        assertEquals("Origen y destino son obligatorios para generar COT.", excepcion.getMessage());
    }

    @Test
    void generarCot_DebeLanzarExcepcionCuandoDestinoEsInvalido() {
        var dtoSinDestino = new GenerarCotRequestDTO(
            "Origen", null, 5000.0, 0.74, 150000.0, "Nafta", "REM-123"
        );

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class,
            () -> arbaService.generarCot(dtoSinDestino)
        );

        assertEquals("Origen y destino son obligatorios para generar COT.", excepcion.getMessage());
    }

    @Test
    void generarCot_DebeLanzarExcepcionCuandoLitrosSonInvalidos() {
        var dtoLitrosCero = new GenerarCotRequestDTO(
            "Origen", "Destino", 0.0, 0.74, 150000.0, "Nafta", "REM-123"
        );

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class,
            () -> arbaService.generarCot(dtoLitrosCero)
        );

        assertEquals("Los litros cargados son obligatorios.", excepcion.getMessage());
    }

    @Test
    void generarCot_DebeLanzarExcepcionCuandoDensidadEsInvalida() {
        var dtoDensidadNegativa = new GenerarCotRequestDTO(
            "Origen", "Destino", 5000.0, -0.1, 150000.0, "Nafta", "REM-123"
        );

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class,
            () -> arbaService.generarCot(dtoDensidadNegativa)
        );

        assertEquals("La densidad del producto es obligatoria.", excepcion.getMessage());
    }

    @Test
    void generarCot_DebeLanzarExcepcionCuandoValorMercaderiaEsInvalido() {
        var dtoValorNulo = new GenerarCotRequestDTO(
            "Origen", "Destino", 5000.0, 0.74, null, "Nafta", "REM-123"
        );

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class,
            () -> arbaService.generarCot(dtoValorNulo)
        );

        assertEquals("El valor de mercadería es obligatorio.", excepcion.getMessage());
    }

    @Test
    void generarCot_DebeLanzarExcepcionCuandoProductoEsInvalido() {
        var dtoProductoVacio = new GenerarCotRequestDTO(
            "Origen", "Destino", 5000.0, 0.74, 150000.0, "   ", "REM-123"
        );

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class,
            () -> arbaService.generarCot(dtoProductoVacio)
        );

        assertEquals("El producto es obligatorio.", excepcion.getMessage());
    }
}