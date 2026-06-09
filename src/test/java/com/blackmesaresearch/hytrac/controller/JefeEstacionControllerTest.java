package com.blackmesaresearch.hytrac.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.springframework.http.MediaType;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.blackmesaresearch.hytrac.config.JwtAuthFilter;
import com.blackmesaresearch.hytrac.service.JwtService;
import com.blackmesaresearch.hytrac.service.OrdenCargaService;
import com.blackmesaresearch.hytrac.service.UsuarioService;


@WebMvcTest(JefeEstacionController.class)
@AutoConfigureMockMvc(addFilters = false)
class JefeEstacionControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private OrdenCargaService service;
    @MockBean private JwtAuthFilter jwtAuthFilter;
    @MockBean private JwtService jwtService;
    @MockBean private UsuarioService usuarioService;

    @Test
    void reportarEntrega_Retorna200() throws Exception {
        String body = "{\"codigoConfirmacion\": \"123456\"}";
        
        mockMvc.perform(put("/api/jefe-estacion/orden/1/reportar-entrega")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void reportarEntrega_Retorna400ConError() throws Exception {
        doThrow(new IllegalArgumentException("Error")).when(service).reportarEntrega(anyInt(), any());
        
        mockMvc.perform(put("/api/jefe-estacion/orden/1/reportar-entrega")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
