package com.blackmesaresearch.hytrac.service;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.blackmesaresearch.hytrac.dto.graphhopper.GraphhopperResponse;
import com.blackmesaresearch.hytrac.dto.graphhopper.GraphhopperRoute;
import com.blackmesaresearch.hytrac.dto.response.RutaResponseDTO;
import com.blackmesaresearch.hytrac.model.core.LugarOperativo;
import com.blackmesaresearch.hytrac.model.core.Ruta;
import com.blackmesaresearch.hytrac.repository.LugarOperativoRepository;
import com.blackmesaresearch.hytrac.repository.RutaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;


@ExtendWith(MockitoExtension.class)
public class RutaServiceTest {

    @Mock 
    private RutaRepository rutaRepository;

    @Mock
    private LugarOperativoRepository lugarOperativoRepository;


    @Mock private RestTemplate restTemplate;

    @Mock private ObjectMapper objectMapper;

    @InjectMocks
    private RutaService rutaService;
    private LugarOperativo origenValido;
    private LugarOperativo destinoValido;

    @BeforeEach
    void setUp() {
        // Mockito no sabe leer alores @Value, por lo que los seteamos manualmente
        ReflectionTestUtils.setField(rutaService, "graphhopperApiKey", "fake-api-key-123");
        ReflectionTestUtils.setField(rutaService, "graphhopperApiUrl", "https://graphhopper.com/api/1/route");

        origenValido = new LugarOperativo();
        origenValido.setId(1);
        origenValido.setNombre("Planta Pilar");
        origenValido.setLatitud(-34.4500);
        origenValido.setLongitud(-58.9000);

        destinoValido = new LugarOperativo();
        destinoValido.setId(2);
        destinoValido.setNombre("Puerto de Buenos Aires");
        destinoValido.setLatitud(-34.6000);
        destinoValido.setLongitud(-58.3700);

    }

    // Obtener Ruta por ID //
    @Test
    void obtenerRutaPorId_DebeLanzarRutaSiExiste() {
        Ruta ruta = new Ruta();
        ruta.setId(100);
        ruta.setDistanciaKm(50.0);
        ruta.setTiempoEstimadoHoras(1.5);
        ruta.setGeometriaJson("[]");

        when(rutaRepository.findById(100)).thenReturn(Optional.of(ruta));

        RutaResponseDTO response = rutaService.obtenerRutaPorId(100);

        assertNotNull(response);
        assertEquals(50, response.distanciaKm());
    }

    @Test
    void obtenerRutaPorId_DebeLanzarExcepcionSiNoExiste() {
        when(rutaRepository.findById(999)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            rutaService.obtenerRutaPorId(999);
        });

        assertTrue(exception.getMessage().contains("Ruta no encontrada"));
    }

    // Calcular Ruta //

    @Test
    void calcularRuta_DebeLanzarExcepcionSiOrigenNoTieneCoordenadas() {
        LugarOperativo origenSinCoordenadas = new LugarOperativo();
        origenSinCoordenadas.setId(3);
        origenSinCoordenadas.setNombre("Origen Sin Coordenadas");

        when(lugarOperativoRepository.findById(3)).thenReturn(Optional.of(origenSinCoordenadas));
        when(lugarOperativoRepository.findById(2)).thenReturn(Optional.of(destinoValido));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            rutaService.calcularRuta(3, 2);
        });

        assertTrue(exception.getMessage().contains("Lugar operativo origen no tiene coordenadas válidas"));
        verify(restTemplate, never()).getForObject(anyString(), any());
    }

    @Test
    void calcularRuta_DebeLanzarExcepcionSiNoHayApiKey() {
        ReflectionTestUtils.setField(rutaService, "graphhopperApiKey", "");

        when(lugarOperativoRepository.findById(1)).thenReturn(Optional.of(origenValido));
        when(lugarOperativoRepository.findById(2)).thenReturn(Optional.of(destinoValido));
        when(rutaRepository.findByOrigenIdAndDestinoId(1, 2)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            rutaService.calcularRuta(1, 2);
        });

        assertEquals("GraphHopper API key no está configurada", exception.getMessage());
    }

    @Test
    void calcularRuta_DebeRetornarDeDBSiRutaYaExiste() {
        Ruta rutaExistente = new Ruta();
        rutaExistente.setId(200);
        rutaExistente.setDistanciaKm(60.0);
        rutaExistente.setGeometriaJson("[]");

        when(lugarOperativoRepository.findById(1)).thenReturn(Optional.of(origenValido));
        when(lugarOperativoRepository.findById(2)).thenReturn(Optional.of(destinoValido));
        when(rutaRepository.findByOrigenIdAndDestinoId(1, 2)).thenReturn(Optional.of(rutaExistente));

        RutaResponseDTO response = rutaService.calcularRuta(1, 2);

        assertNotNull(response);
        assertEquals(60.0, response.distanciaKm());
        verify(restTemplate, never()).getForObject(anyString(), any());  
        verify(rutaRepository, never()).save(any());
    }

    @Test
    void calcularRuta_DebeLanzarExcepcionSiApiDevuelveError() {
        when(lugarOperativoRepository.findById(1)).thenReturn(Optional.of(origenValido));
        when(lugarOperativoRepository.findById(2)).thenReturn(Optional.of(destinoValido));
        when(rutaRepository.findByOrigenIdAndDestinoId(1, 2)).thenReturn(Optional.empty());
        
        //Forzar Error en la llamada a la API
        when(restTemplate.getForObject(anyString(), eq(GraphhopperResponse.class))).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            rutaService.calcularRuta(1, 2);
        });

        assertEquals("Error al llamar a GraphHopper API: Respuesta nula de GraphHopper API", exception.getMessage());
        verify(rutaRepository, never()).save(any());

    }

    @Test
    void calcularRuta_DebeLlamarApiGuardarYRetornarRuta() throws Exception {
        var ghResponse = new GraphhopperResponse(
            List.of(new GraphhopperRoute(
                10000.0,    
                3600000L,   
                null       
            ))
        );

        when(lugarOperativoRepository.findById(1)).thenReturn(Optional.of(origenValido));
        when(lugarOperativoRepository.findById(2)).thenReturn(Optional.of(destinoValido));
        when(rutaRepository.findByOrigenIdAndDestinoId(1, 2)).thenReturn(Optional.empty());
        
     
        when(restTemplate.getForObject(anyString(), eq(GraphhopperResponse.class))).thenReturn(ghResponse);
        
       
        when(objectMapper.writeValueAsString(any())).thenReturn("[]");

        when(rutaRepository.save(any(Ruta.class))).thenAnswer(i -> {
            Ruta r = i.getArgument(0);
            r.setId(500);
            return r;
        });

        
        RutaResponseDTO response = rutaService.calcularRuta(1, 2);

  
        assertNotNull(response);
        assertEquals(10.0, response.distanciaKm());
        assertEquals(1.375, response.tiempoEstimadoHoras(), 0.001); 
        
        verify(rutaRepository, times(1)).save(any(Ruta.class));
    }




    
}
