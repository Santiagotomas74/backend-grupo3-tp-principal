package com.blackmesaresearch.hytrac.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import com.blackmesaresearch.hytrac.dto.response.TransportistaResponseDTO;
import com.blackmesaresearch.hytrac.service.IncidenciaService;
import com.blackmesaresearch.hytrac.service.JwtService;
import com.blackmesaresearch.hytrac.service.SeleccionTransportistasService;
import com.blackmesaresearch.hytrac.service.TransportistaService;
import com.blackmesaresearch.hytrac.service.UsuarioService;

@WebMvcTest(TransportistaController.class)
@AutoConfigureMockMvc(addFilters = false)
class TransportistaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private TransportistaService transportistaService;
    @MockBean private IncidenciaService incidenciaService;
    @MockBean private JwtAuthFilter jwtAuthFilter;
    @MockBean private JwtService jwtService;
    @MockBean private UsuarioService usuarioService;
    @MockBean private SeleccionTransportistasService seleccionTransportistasService;

    private TransportistaResponseDTO dtoResponse() {
        return new TransportistaResponseDTO(
            1, "Juan", "Perez", "20-12345678-9", "LEG-001", java.time.LocalDate.now(),"MONOTRIBUTISTA"
        );
    }

    
    // GET /api/transportistas
    

    @Test
    void obtenerTodos_DebeRetornar200ConListaVacia() throws Exception {
        when(transportistaService.obtenerTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/transportistas"))
            .andExpect(status().isOk());
    }

    @Test
    void obtenerTodos_DebeRetornar200ConTransportistas() throws Exception {
        when(transportistaService.obtenerTodos()).thenReturn(List.of(dtoResponse()));

        mockMvc.perform(get("/api/transportistas"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].legajo").value("LEG-001"));
    }

   
    // POST /api/transportistas/incidencia
   

    @Test
    void reportarIncidencia_DebeRetornar200CuandoEsValida() throws Exception {
        doNothing().when(incidenciaService).reportarIncidencia(any());

        String body = """
            {
                "legajoTransportista": "LEG-001",
                "numeroRemito": "REM-001",
                "tipoIncidenciaId": 1,
                "descripcion": "Accidente en ruta"
            }
            """;

        mockMvc.perform(post("/api/transportistas/incidencia")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void reportarIncidencia_DebeRetornar400CuandoOrdenNoExiste() throws Exception {
        doThrow(new IllegalArgumentException("Orden no encontrada."))
            .when(incidenciaService).reportarIncidencia(any());

        String body = """
            {
                "legajoTransportista": "LEG-001",
                "numeroRemito": "REM-999",
                "tipoIncidenciaId": 1,
                "descripcion": "Accidente en ruta"
            }
            """;

        mockMvc.perform(post("/api/transportistas/incidencia")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Orden no encontrada."));
    }

    
    // POST /api/transportistas/alta
  

    @Test
    void registrarTransportista_DebeRetornar201CuandoEsValido() throws Exception {
        doNothing().when(transportistaService).registrarNuevoTransportista(any());

        String body = """
            {
                "nombre": "Juan",
                "apellido": "Perez",
                "dni": 12345678,
                "email": "juan@mail.com",
                "passwordTemporal": "1234",
                "cuit": "20-12345678-9",
                "tipoVinculoId": 1,
                "empresaId": null,
                "documentos": []
            }
            """;

        mockMvc.perform(post("/api/transportistas/alta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void registrarTransportista_DebeRetornar400CuandoEmailYaExiste() throws Exception {
        doThrow(new IllegalArgumentException("El Email ya se encuentra registrado"))
            .when(transportistaService).registrarNuevoTransportista(any());

        String body = """
            {
                "nombre": "Juan", "apellido": "Perez",
                "dni": 12345678, "email": "juan@mail.com",
                "passwordTemporal": "1234",
                "cuit": "20-12345678-9",
                "tipoVinculoId": 1
            }
            """;

        mockMvc.perform(post("/api/transportistas/alta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("El Email ya se encuentra registrado"));
    }

    @Test
    void registrarTransportista_DebeRetornar400CuandoCuitYaExiste() throws Exception {
        doThrow(new IllegalArgumentException("El CUIT ya se encuentra registrado."))
            .when(transportistaService).registrarNuevoTransportista(any());

        String body = """
            {
                "nombre": "Juan", "apellido": "Perez",
                "dni": 12345678, "email": "juan@mail.com",
                "passwordTemporal": "1234",
                "cuit": "20-12345678-9",
                "tipoVinculoId": 1
            }
            """;

        mockMvc.perform(post("/api/transportistas/alta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("El CUIT ya se encuentra registrado."));
    }
}