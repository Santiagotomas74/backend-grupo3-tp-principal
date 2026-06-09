package com.blackmesaresearch.hytrac.controller;

import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.blackmesaresearch.hytrac.config.JwtAuthFilter;
import com.blackmesaresearch.hytrac.service.AuditoriaOrdenService;

@WebMvcTest(AuditoriaOrdenController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuditoriaOrdenControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private AuditoriaOrdenService service;
    @MockBean private JwtAuthFilter jwtAuthFilter;

    @Test
    void obtenerTodaLaAuditoria_Retorna200() throws Exception {
        when(service.obtenerAuditoria()).thenReturn(List.of());
        mockMvc.perform(get("/api/auditoria")).andExpect(status().isOk());
    }

    @Test
    void obtenerHistorialRemito_Retorna200() throws Exception {
        when(service.obtenerPorNumeroRemito("REM-001")).thenReturn(List.of());
        mockMvc.perform(get("/api/auditoria/REM-001")).andExpect(status().isOk());
    }
}
