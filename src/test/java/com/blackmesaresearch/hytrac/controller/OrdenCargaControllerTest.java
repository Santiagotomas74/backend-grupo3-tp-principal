package com.blackmesaresearch.hytrac.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.blackmesaresearch.hytrac.config.JwtAuthFilter;
import com.blackmesaresearch.hytrac.dto.response.OrdenCargaDetalleResponseDTO;
import com.blackmesaresearch.hytrac.dto.response.OrdenCargaResponseDTO;
import com.blackmesaresearch.hytrac.service.AuditoriaOrdenService;
import com.blackmesaresearch.hytrac.service.JwtService;
import com.blackmesaresearch.hytrac.service.OrdenCargaService;
import com.blackmesaresearch.hytrac.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(OrdenCargaController.class)
@AutoConfigureMockMvc(addFilters = false) // desactiva JWT para tests
class OrdenCargaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrdenCargaService ordenCargaService;

    @MockBean
    private AuditoriaOrdenService auditoriaOrdenService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean  
    private JwtService jwtService;

    @MockBean
    private UsuarioService usuarioService;

     //Helper

    private OrdenCargaResponseDTO dtoResponse() {
        return new OrdenCargaResponseDTO(
            1, "HT-001", "REM-001", "COT-001",
            "Pendiente", "Nafta",
            "Planta YPF", "Estacion Centro",
            5000.0, 0.0, null,
            null, null,
            "PAT-123", "ACO-123",
            "Juan", "Perez", "LEG-001",
            "LEG-002", null, false, null
        );
    }

    
    // GET /api/ordenes/get
    
    @Test
    void obtenerOrdenes_DebeRetornar200ConListaVacia() throws Exception {
        when(ordenCargaService.obtenerTodas()).thenReturn(List.of());

        mockMvc.perform(get("/api/ordenes/get"))
            .andExpect(status().isOk());
    }

    @Test
    void obtenerOrdenes_DebeRetornar200ConOrdenes() throws Exception {
        when(ordenCargaService.obtenerTodas()).thenReturn(List.of(dtoResponse()));

        mockMvc.perform(get("/api/ordenes/get"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].numeroRemito").value("REM-001"));
    }

   
    // GET /api/ordenes/remito/{numeroRemito}
   

    @Test
    void obtenerPorRemito_DebeRetornar200CuandoExiste() throws Exception {
        when(ordenCargaService.obtenerPorRemito("REM-001")).thenReturn(dtoResponse());

        mockMvc.perform(get("/api/ordenes/remito/REM-001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.numeroRemito").value("REM-001"));
    }

    @Test
    void obtenerPorRemito_DebeRetornar400CuandoNoExiste() throws Exception {
        when(ordenCargaService.obtenerPorRemito("REM-999"))
            .thenThrow(new IllegalArgumentException("Orden no encontrada."));

        mockMvc.perform(get("/api/ordenes/remito/REM-999"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Orden no encontrada."));
    }

    
    // POST /api/ordenes/crear
    
    @Test
    void crearOrdenCarga_DebeRetornar201CuandoEsValida() throws Exception {
        when(ordenCargaService.guardarNuevaOrdenCarga(any())).thenReturn(dtoResponse());

        String body = """
            {
                "numeroRemito": "REM-001",
                "cot": "COT-001",
                "camionId": 1,
                "acopladoId": 1,
                "transportistaId": 1,
                "plantaDespachoId": 1,
                "estacionDestinoId": 2,
                "operadorId": 1,
                "estadoId": 1,
                "combustibleId": 1,
                "rutaId": 1,
                "litrosCargados": 5000.0,
                "litrosEntregados": 0.0,
                "observaciones": "Test",
                "fieAdjunta": false,
                "confirmado": false
            }
            """;

        mockMvc.perform(post("/api/ordenes/crear")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.numeroRemito").value("REM-001"));
    }

    @Test
    void crearOrdenCarga_DebeRetornar400CuandoRemitoExiste() throws Exception {
        when(ordenCargaService.guardarNuevaOrdenCarga(any()))
            .thenThrow(new IllegalArgumentException("El número de remito ya existe en el sistema."));

        String body = """
            {
                "numeroRemito": "REM-001", "cot": "COT-001",
                "camionId": 1, "acopladoId": 1, "transportistaId": 1,
                "plantaDespachoId": 1, "estacionDestinoId": 2,
                "operadorId": 1, "estadoId": 1, "combustibleId": 1, "rutaId": 1,
                "litrosCargados": 5000.0, "litrosEntregados": 0.0,
                "fieAdjunta": false, "confirmado": false
            }
            """;

        mockMvc.perform(post("/api/ordenes/crear")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("El número de remito ya existe en el sistema."));
    }

    
    // GET /api/ordenes/{id}
    

    @Test
    void obtenerPorId_DebeRetornar200CuandoExiste() throws Exception {
        var detalle = new OrdenCargaDetalleResponseDTO(
            1, "HT-001", "REM-001", "COT-001", "Pendiente",
            "PAT-123", "ACO-123", 30000.0, 5000.0, null,
            "Juan Perez", "Nafta", "Planta", "Estacion",
            5000.0, 0.0, null, null, null, null,
            "Obs", false, false, null, null,
            "Nafta", "1203", "Clase 3"
        );
        when(ordenCargaService.obtenerDetallePorId(1)).thenReturn(detalle);

        mockMvc.perform(get("/api/ordenes/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.numeroRemito").value("REM-001"));
    }

    
    // PUT /api/ordenes/{id}/editar
    

    @Test
    void editarOrdenCarga_DebeRetornar200CuandoEsValida() throws Exception {
        when(ordenCargaService.editarOrdenCarga(anyInt(), any())).thenReturn(dtoResponse());

        String body = """
            {
                "numeroRemito": "REM-001", "cot": "COT-001",
                "camionId": 1, "acopladoId": 1, "transportistaId": 1,
                "plantaDespachoId": 1, "estacionDestinoId": 2,
                "operadorId": 1, "estadoId": 1, "combustibleId": 1, "rutaId": 1,
                "litrosCargados": 5000.0, "litrosEntregados": 0.0,
                "fieAdjunta": false, "confirmado": false
            }
            """;

        mockMvc.perform(put("/api/ordenes/1/editar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.numeroRemito").value("REM-001"));
    }

    @Test
    void editarOrdenCarga_DebeRetornar400CuandoOrdenEstaEntregada() throws Exception {
        when(ordenCargaService.editarOrdenCarga(anyInt(), any()))
            .thenThrow(new IllegalArgumentException("No se puede editar una orden que ya se encuentra en estado 'Entregada'."));

        String body = """
            {
                "numeroRemito": "REM-001", "cot": "COT-001",
                "camionId": 1, "acopladoId": 1, "transportistaId": 1,
                "plantaDespachoId": 1, "estacionDestinoId": 2,
                "operadorId": 1, "estadoId": 1, "combustibleId": 1, "rutaId": 1,
                "litrosCargados": 5000.0, "litrosEntregados": 0.0,
                "fieAdjunta": false, "confirmado": false
            }
            """;

        mockMvc.perform(put("/api/ordenes/1/editar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("No se puede editar una orden que ya se encuentra en estado 'Entregada'."));
    }
}
