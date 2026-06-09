package com.blackmesaresearch.hytrac.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import com.blackmesaresearch.hytrac.dto.response.UsuarioAdminResponseDTO;
import com.blackmesaresearch.hytrac.service.AdministradorService;
import com.blackmesaresearch.hytrac.service.JwtService;
import com.blackmesaresearch.hytrac.service.UsuarioService;

@WebMvcTest(AdministradorController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdministradorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdministradorService administradorService;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;      // ← agregar

    @MockBean
    private JwtService jwtService; 

    private UsuarioAdminResponseDTO usuarioResponse() {
        return new UsuarioAdminResponseDTO(
            1, 
            "Juan", 
            "Perez", 
            12345678L,
            "juan@mail.com", 
            "LEG-001", 
            "ADMINISTRADOR",
            null, 
            true, 
            null, 
            null,
            null
        );
    }

    
    // GET /api/admin/usuarios
    

    @Test
    void obtenerUsuarios_DebeRetornar200ConLista() throws Exception {
        when(administradorService.obtenerUsuarios()).thenReturn(List.of(usuarioResponse()));

        mockMvc.perform(get("/api/admin/usuarios"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].legajo").value("LEG-001"));
    }

    @Test
    void obtenerUsuarios_DebeRetornar200ConListaVacia() throws Exception {
        when(administradorService.obtenerUsuarios()).thenReturn(List.of());

        mockMvc.perform(get("/api/admin/usuarios"))
            .andExpect(status().isOk());
    }


    // POST /api/admin/usuarios/alta
   

    @Test
    void registrarNuevoUsuario_DebeRetornar201CuandoEsValido() throws Exception {
        doNothing().when(usuarioService).registrarNuevoUsuario(any());

        String body = """
            {
                "nombre": "Juan",
                "apellido": "Perez",
                "dni": 12345678,
                "email": "juan@mail.com",
                "passwordTemporal": "1234",
                "rolNombre": "OPERADOR",
                "lugarOperativoId": null
            }
            """;

        mockMvc.perform(post("/api/admin/usuarios/alta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void registrarNuevoUsuario_DebeRetornar400CuandoEmailYaExiste() throws Exception {
        doThrow(new IllegalArgumentException("El Email ya se encuentra registrado"))
            .when(usuarioService).registrarNuevoUsuario(any());

        String body = """
            {
                "nombre": "Juan", "apellido": "Perez",
                "dni": 12345678, "email": "juan@mail.com",
                "passwordTemporal": "1234", "rolNombre": "OPERADOR"
            }
            """;

        mockMvc.perform(post("/api/admin/usuarios/alta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("El Email ya se encuentra registrado"));
    }

    @Test
    void registrarNuevoUsuario_DebeRetornar400CuandoDniYaExiste() throws Exception {
        doThrow(new IllegalArgumentException("El DNI ya se encuentra registrado"))
            .when(usuarioService).registrarNuevoUsuario(any());

        String body = """
            {
                "nombre": "Juan", "apellido": "Perez",
                "dni": 12345678, "email": "juan@mail.com",
                "passwordTemporal": "1234", "rolNombre": "OPERADOR"
            }
            """;

        mockMvc.perform(post("/api/admin/usuarios/alta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("El DNI ya se encuentra registrado"));
    }

    
    // PUT /api/admin/usuarios/editar/{id}
    

    @Test
    void editarUsuario_DebeRetornar200CuandoEsValido() throws Exception {
        when(administradorService.editarUsuario(anyInt(), any())).thenReturn(usuarioResponse());

        String body = """
            {
                "nombre": "Juan Editado",
                "apellido": "Perez",
                "email": "juan@mail.com",
                "rolNombre": "SUPERVISOR",
                "activo": true
            }
            """;

        mockMvc.perform(put("/api/admin/usuarios/editar/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void editarUsuario_DebeRetornar400CuandoUsuarioNoExiste() throws Exception {
        doThrow(new IllegalArgumentException("Usuario no encontrado."))
            .when(administradorService).editarUsuario(anyInt(), any());

        String body = """
            {
                "nombre": "Juan", "apellido": "Perez",
                "email": "juan@mail.com", "rolNombre": "OPERADOR", "activo": true
            }
            """;

        mockMvc.perform(put("/api/admin/usuarios/editar/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Usuario no encontrado."));
    }

    
    // DELETE /api/admin/usuarios/baja/{id}
    

    @Test
    void darDeBajaUsuario_DebeRetornar200CuandoEsValido() throws Exception {
        doNothing().when(administradorService).darBajaUsuario(1);

        mockMvc.perform(delete("/api/admin/usuarios/baja/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void darDeBajaUsuario_DebeRetornar400CuandoUsuarioNoExiste() throws Exception {
        doThrow(new IllegalArgumentException("Usuario no encontrado."))
            .when(administradorService).darBajaUsuario(99);

        mockMvc.perform(delete("/api/admin/usuarios/baja/99"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Usuario no encontrado."));
    }
}