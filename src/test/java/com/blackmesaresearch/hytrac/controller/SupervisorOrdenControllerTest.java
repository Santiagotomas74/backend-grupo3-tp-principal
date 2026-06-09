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
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.blackmesaresearch.hytrac.config.JwtAuthFilter;
import com.blackmesaresearch.hytrac.service.JwtService;
import com.blackmesaresearch.hytrac.service.OrdenCargaService;
import com.blackmesaresearch.hytrac.service.UsuarioService;

@WebMvcTest(SupervisorOrdenController.class)
@AutoConfigureMockMvc(addFilters = false)
class SupervisorOrdenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrdenCargaService ordenCargaService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;      

    @MockBean
    private JwtService jwtService;            

    @MockBean
    private UsuarioService usuarioService;

    
    // GET /api/supervisor/ordenes
    

    @Test
    void obtenerTodas_DebeRetornar200ConListaVacia() throws Exception {
        when(ordenCargaService.obtenerTodasSupervisor()).thenReturn(List.of());

        mockMvc.perform(get("/api/supervisor/ordenes"))
            .andExpect(status().isOk());
    }

    
    // GET /api/supervisor/ordenes/{id}
    

    @Test
    void obtenerPorId_DebeRetornar400CuandoNoExiste() throws Exception {
        when(ordenCargaService.obtenerOrdenSupervisor(99))
            .thenThrow(new IllegalArgumentException("Orden no encontrada."));

        mockMvc.perform(get("/api/supervisor/ordenes/99"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Orden no encontrada."));
    }

    
    // PUT /api/supervisor/ordenes/{id}/confirmar
    

    @Test
    void confirmarOrden_DebeRetornar200CuandoEsValida() throws Exception {
        doNothing().when(ordenCargaService).confirmarOrden(1);

        mockMvc.perform(put("/api/supervisor/ordenes/1/confirmar"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void confirmarOrden_DebeRetornar400CuandoOrdenNoExiste() throws Exception {
        doThrow(new IllegalArgumentException("Orden no encontrada."))
            .when(ordenCargaService).confirmarOrden(99);

        mockMvc.perform(put("/api/supervisor/ordenes/99/confirmar"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Orden no encontrada."));
    }

    
    // PUT /api/supervisor/ordenes/{id}/rechazar
    

    @Test
    void rechazarOrden_DebeRetornar200CuandoEsValida() throws Exception {
        doNothing().when(ordenCargaService).rechazarOrden(anyInt(), anyString(), anyString());

        String body = """
            {"legajoSupervisor": "LEG-001", "motivoRechazo": "Datos incorrectos"}
            """;

        mockMvc.perform(put("/api/supervisor/ordenes/1/rechazar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void rechazarOrden_DebeRetornar400CuandoOrdenNoExiste() throws Exception {
        doThrow(new IllegalArgumentException("Orden no encontrada."))
            .when(ordenCargaService).rechazarOrden(anyInt(), anyString(), anyString());

        String body = """
            {"legajoSupervisor": "LEG-001", "motivoRechazo": "Motivo"}
            """;

        mockMvc.perform(put("/api/supervisor/ordenes/99/rechazar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Orden no encontrada."));
    }

    
    // PUT /api/supervisor/ordenes/{id}/aprobar-inicio
    

    @Test
    void aprobarInicioViaje_DebeRetornar200CuandoEsValido() throws Exception {
        doNothing().when(ordenCargaService).aprobarInicioViaje(anyInt(), anyString());

        String body = """
            {"legajoSupervisor": "LEG-001"}
            """;

        mockMvc.perform(put("/api/supervisor/ordenes/1/aprobar-inicio")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void aprobarInicioViaje_DebeRetornar400CuandoOrdenNoTieneRuta() throws Exception {
        doThrow(new IllegalArgumentException("No se puede iniciar el viaje porque la orden no tiene una ruta asignada."))
            .when(ordenCargaService).aprobarInicioViaje(anyInt(), anyString());

        String body = """
            {"legajoSupervisor": "LEG-001"}
            """;

        mockMvc.perform(put("/api/supervisor/ordenes/1/aprobar-inicio")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("No se puede iniciar el viaje porque la orden no tiene una ruta asignada."));
    }

   
    // PUT /api/supervisor/ordenes/{id}/confirmar-entrega
    

    @Test
    void confirmarEntrega_DebeRetornar200CuandoEsValida() throws Exception {
        doNothing().when(ordenCargaService).confirmarEntrega(anyInt(), anyString());

        String body = """
            {"legajoSupervisor": "LEG-001", "litrosEntregados": 5000.0}
            """;

        mockMvc.perform(put("/api/supervisor/ordenes/1/confirmar-entrega")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void confirmarEntrega_DebeRetornar400CuandoEstadoEsIncorrecto() throws Exception {
        doThrow(new IllegalArgumentException("La orden no está pendiente de confirmación de entrega."))
            .when(ordenCargaService).confirmarEntrega(anyInt(), anyString());

        String body = """
            {"legajoSupervisor": "LEG-001", "litrosEntregados": 5000.0}
            """;

        mockMvc.perform(put("/api/supervisor/ordenes/1/confirmar-entrega")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("La orden no está pendiente de confirmación de entrega."));
    }

    @Test
    void rechazarOrden_DebeRetornar200CuandoEsValido() throws Exception {
        String body = "{\"legajoSupervisor\": \"LEG-001\", \"motivoRechazo\": \"Datos mal cargados\"}";
        
        mockMvc.perform(put("/api/supervisor/ordenes/1/rechazar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void rechazarInicioViaje_DebeRetornar200CuandoEsValido() throws Exception {
        String body = "{\"legajoSupervisor\": \"LEG-001\", \"motivoRechazo\": \"Error en ruta\"}";
        
        mockMvc.perform(put("/api/supervisor/ordenes/1/rechazar-inicio")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void rechazarEntrega_DebeRetornar200CuandoEsValido() throws Exception {
        String body = "{\"legajoSupervisor\": \"LEG-001\", \"motivoRechazo\": \"Entrega fallida\"}";
        
        mockMvc.perform(put("/api/supervisor/ordenes/1/rechazar-entrega")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void cancelarOrden_DebeRetornar200CuandoEsValido() throws Exception {
        String body = "{\"motivo\": \"Cancelación por cliente\"}";
        
        mockMvc.perform(put("/api/supervisor/ordenes/remito/REM-001/gestion-incidencia")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerTodas_DebeRetornarListaDeOrdenes() throws Exception {
        // Simulamos una respuesta no vacía
        var ordenDto = new com.blackmesaresearch.hytrac.dto.response.OrdenSupervisorResponseDTO(
            1, "HT-001", "REM-001", "COT-001", 
            "Pendiente", "Juan", "Perez", "LEG-001", 
            "Scania", 5000.0, 0.0, "Planta YPF", 
            "Estacion Centro", "Nafta", 
            LocalDateTime.now(), LocalDateTime.now(), false
        );

        when(ordenCargaService.obtenerTodasSupervisor()).thenReturn(List.of(ordenDto));

        mockMvc.perform(get("/api/supervisor/ordenes"))
               .andExpect(status().isOk());
    }
}
