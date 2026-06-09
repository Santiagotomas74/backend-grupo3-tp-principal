package com.blackmesaresearch.hytrac.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.blackmesaresearch.hytrac.config.JwtAuthFilter;
import com.blackmesaresearch.hytrac.dto.response.OrdenTransportistaResponseDTO;
import com.blackmesaresearch.hytrac.service.JwtService;
import com.blackmesaresearch.hytrac.service.TransportistaOrdenService;
import com.blackmesaresearch.hytrac.service.UsuarioService;

@WebMvcTest(TransportistaOrdenController.class)
@AutoConfigureMockMvc(addFilters = false)
class TransportistaOrdenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private TransportistaOrdenService service;
    @MockBean private JwtAuthFilter jwtAuthFilter;
    @MockBean private JwtService jwtService;
    @MockBean private UsuarioService usuarioService;

    private OrdenTransportistaResponseDTO dtoResponse() {
        return new OrdenTransportistaResponseDTO(
            1, "REM-001", "COT-001",
            "Pendiente", "PAT-123", "ACO-123",
            "Nafta", 5000.0,
            "Planta YPF", "Estacion Centro",
            LocalDateTime.now(), true, 2
        );
    }

    // ================================================================
    // GET /api/transportista/{legajo}/orden
    // ================================================================

    @Test
    void obtenerOrdenActiva_DebeRetornar200CuandoExiste() throws Exception {
        when(service.obtenerOrdenPendiente("LEG-001")).thenReturn(dtoResponse());

        mockMvc.perform(get("/api/transportista/LEG-001/orden"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.numeroRemito").value("REM-001"));
    }

    @Test
    void obtenerOrdenActiva_DebeRetornar400CuandoNoHayOrdenesPendientes() throws Exception {
        when(service.obtenerOrdenPendiente("LEG-001"))
            .thenThrow(new IllegalArgumentException("No hay órdenes pendientes."));

        mockMvc.perform(get("/api/transportista/LEG-001/orden"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("No hay órdenes pendientes."));
    }

    // ================================================================
    // GET /api/transportista/{legajo}/orden-en-curso
    // ================================================================

    @Test
    void obtenerOrdenEnCurso_DebeRetornar200CuandoExiste() throws Exception {
        when(service.obtenerOrdenEnCurso("LEG-001")).thenReturn(dtoResponse());

        mockMvc.perform(get("/api/transportista/LEG-001/orden-en-curso"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.numeroRemito").value("REM-001"));
    }

    @Test
    void obtenerOrdenEnCurso_DebeRetornar400CuandoNoHayOrdenEnCurso() throws Exception {
        when(service.obtenerOrdenEnCurso("LEG-001"))
            .thenThrow(new IllegalArgumentException("No hay órdenes en curso."));

        mockMvc.perform(get("/api/transportista/LEG-001/orden-en-curso"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("No hay órdenes en curso."));
    }

    // ================================================================
    // PUT /api/transportista/orden/{ordenId}/iniciar-viaje
    // ================================================================

    @Test
    void iniciarViaje_DebeRetornar200CuandoEsValido() throws Exception {
        doNothing().when(service).iniciarViaje(anyInt(), anyString());

        String body = """
            {"legajoTransportista": "LEG-001"}
            """;

        mockMvc.perform(put("/api/transportista/orden/1/iniciar-viaje")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void iniciarViaje_DebeRetornar400CuandoOrdenNoExiste() throws Exception {
        doThrow(new IllegalArgumentException("Orden no encontrada."))
            .when(service).iniciarViaje(anyInt(), anyString());

        String body = """
            {"legajoTransportista": "LEG-001"}
            """;

        mockMvc.perform(put("/api/transportista/orden/99/iniciar-viaje")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Orden no encontrada."));
    }

    @Test
    void iniciarViaje_DebeRetornar400CuandoEstadoNoEsPendiente() throws Exception {
        doThrow(new IllegalArgumentException("La orden no está en estado pendiente."))
            .when(service).iniciarViaje(anyInt(), anyString());

        String body = """
            {"legajoTransportista": "LEG-001"}
            """;

        mockMvc.perform(put("/api/transportista/orden/1/iniciar-viaje")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("La orden no está en estado pendiente."));
    }

    // ================================================================
    // PUT /api/transportista/orden/{ordenId}/notificar-entrega
    // ================================================================

    @Test
    void notificarEntrega_DebeRetornar200CuandoEsValida() throws Exception {
        doNothing().when(service).notificarEntrega(anyInt(), anyString(), anyString());

        String body = """
            {
                "legajoTransportista": "LEG-001",
                "codigoConfirmacion": "123456"
            }
            """;

        mockMvc.perform(put("/api/transportista/orden/1/notificar-entrega")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void notificarEntrega_DebeRetornar400CuandoCodigoEsIncorrecto() throws Exception {
        doThrow(new IllegalArgumentException("El código de confirmación es inválido."))
            .when(service).notificarEntrega(anyInt(), anyString(), anyString());

        String body = """
            {
                "legajoTransportista": "LEG-001",
                "codigoConfirmacion": "000000"
            }
            """;

        mockMvc.perform(put("/api/transportista/orden/1/notificar-entrega")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("El código de confirmación es inválido."));
    }

    @Test
    void notificarEntrega_DebeRetornar400CuandoOrdenNoEstaEnCurso() throws Exception {
        doThrow(new IllegalArgumentException("La orden no está en curso."))
            .when(service).notificarEntrega(anyInt(), anyString(), anyString());

        String body = """
            {
                "legajoTransportista": "LEG-001",
                "codigoConfirmacion": "123456"
            }
            """;

        mockMvc.perform(put("/api/transportista/orden/1/notificar-entrega")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("La orden no está en curso."));
    }
}