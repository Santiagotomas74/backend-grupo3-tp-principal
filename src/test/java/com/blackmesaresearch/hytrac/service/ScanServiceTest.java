package com.blackmesaresearch.hytrac.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Map;
import javax.imageio.ImageIO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.blackmesaresearch.hytrac.parser.DocumentParser;
import com.blackmesaresearch.hytrac.parser.DocumentParserFactory;

@ExtendWith(MockitoExtension.class)
public class ScanServiceTest {

    @Mock
    private DocumentParserFactory parserFactory;

    @Mock
    private DocumentParser documentParser;

    @InjectMocks
    private ScanService scanService;

    private String base64ImagenDummy;

    @BeforeEach
    void setUp() throws Exception {
        // Helper: Generamos una imagen vacía de 10x10 píxeles en memoria para simular el formato binario
        BufferedImage imagen = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(imagen, "png", baos);
        

        base64ImagenDummy = "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    // Parse
   

    @Test
    void parsePayload_DebeDelegarAlParserCorrectoSegunElTipoDeDocumento() {
        // Configuración
        String rawText = "00000000000|VTV|OK|2026";
        String docType = "vtv";
        Map<String, Object> mapaEsperado = Map.of("success", true, "documento", "VTV Aprobada");

        when(parserFactory.getParser(docType)).thenReturn(documentParser);
        when(documentParser.parse(rawText)).thenReturn(mapaEsperado);

        // Ejecución
        Map<String, Object> resultado = scanService.parsePayload(rawText, docType);

        // Aserciones
        assertNotNull(resultado);
        assertEquals(mapaEsperado, resultado);
        verify(parserFactory, times(1)).getParser(docType);
        verify(documentParser, times(1)).parse(rawText);
    }

    // Decode
    

    @Test
    void decodeBarcode_DebeLanzarExcepcionCuandoBase64EsNuloOVacio() {
        IllegalArgumentException exNulo = assertThrows(IllegalArgumentException.class,
            () -> scanService.decodeBarcode(null, "vtv")
        );
        assertEquals("Missing image data.", exNulo.getMessage());

        IllegalArgumentException exVacio = assertThrows(IllegalArgumentException.class,
            () -> scanService.decodeBarcode("   ", "vtv")
        );
        assertEquals("Missing image data.", exVacio.getMessage());
    }

    @Test
    void decodeBarcode_DebeLanzarExcepcionCuandoLaImagenEsInvalida() {
        // Le pasamos un String base64 común que no representa una estructura de imagen real
        String stringInvalido = Base64.getEncoder().encodeToString("RicardoFort".getBytes());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> scanService.decodeBarcode(stringInvalido, "vtv")
        );

        assertEquals("Invalid image data.", ex.getMessage());
    }

    @Test
    void decodeBarcode_DebeLanzarExcepcionCuandoNoDetectaCodigoDeBarras() {
        // Al pasarle la imagen dummy de 10x10 píxeles vacía (sin barras ni patrones QR reales), 
        // la librería ZXing va a arrojar internamente un NotFoundException
        Exception ex = assertThrows(Exception.class,
            () -> scanService.decodeBarcode(base64ImagenDummy, "vtv")
        );

        assertEquals("Barcode could not be read or detected from the source.", ex.getMessage());
    }

    @Test
    void decodeBarcode_DebeLanzarExcepcionCuandoNoDetectaPdf417PorDefecto() {
        // Validamos el flujo alternativo del switch: al pasarle un tipo nulo o desconocido, 
        // PDF_417 por defecto. La imagen vacía también fallará la lectura.
        Exception ex = assertThrows(Exception.class,
            () -> scanService.decodeBarcode(base64ImagenDummy, null)
        );

        assertEquals("Barcode could not be read or detected from the source.", ex.getMessage());
    }
}