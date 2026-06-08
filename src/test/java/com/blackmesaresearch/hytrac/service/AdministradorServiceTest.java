package com.blackmesaresearch.hytrac.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.blackmesaresearch.hytrac.dto.request.EditarUsuarioRequestDTO;
import com.blackmesaresearch.hytrac.model.core.EmpresaTercerizada;
import com.blackmesaresearch.hytrac.model.core.LugarOperativo;
import com.blackmesaresearch.hytrac.model.core.Transportista;
import com.blackmesaresearch.hytrac.model.core.Usuario;
import com.blackmesaresearch.hytrac.model.lookup.Rol;
import com.blackmesaresearch.hytrac.model.lookup.TipoVinculo;
import com.blackmesaresearch.hytrac.repository.EmpresaTercerizadaRepository;
import com.blackmesaresearch.hytrac.repository.LugarOperativoRepository;
import com.blackmesaresearch.hytrac.repository.RolRepository;
import com.blackmesaresearch.hytrac.repository.TipoVinculoRepository;
import com.blackmesaresearch.hytrac.repository.TransportistaRepository;
import com.blackmesaresearch.hytrac.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
public class AdministradorServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private TransportistaRepository transportistaRepository;
    @Mock private RolRepository rolRepository;
    @Mock private LugarOperativoRepository lugarOperativoRepository;
    @Mock private TipoVinculoRepository tipoVinculoRepository;
    @Mock private EmpresaTercerizadaRepository empresaTercerizadaRepository;

    @InjectMocks
    private AdministradorService administradorService;

    // Helpers Mock
    

    private Usuario usuarioMock(int id, String email, Long dni) {
        var u = new Usuario();
        u.setId(id);
        u.setNombre("Test");
        u.setApellido("User");
        u.setEmail(email);
        u.setDni(dni);
        u.setLegajo("LEG-00" + id);
        u.setActivo(true);
        var rol = new Rol();
        rol.setNombre("OPERADOR");
        u.setRol(rol);
        var lugar = new LugarOperativo();
        lugar.setNombre("Planta Test");
        u.setLugarOperativo(lugar);
        return u;
    }

    private Transportista transportistaMock(Usuario u, String cuit) {
        var t = new Transportista();
        t.setId(u.getId());
        t.setUsuario(u);
        t.setCuit(cuit);
        var vinculo = new TipoVinculo();
        vinculo.setNombre("Contratado");
        t.setTipoVinculo(vinculo);
        var empresa = new EmpresaTercerizada();
        empresa.setNombreFantasia("Logistica SA");
        t.setEmpresa(empresa);
        return t;
    }

    // Obtener Usuarios
   

    @Test
    void obtenerUsuarios_DebeRetornarListaVacia() {
        when(usuarioRepository.findAll()).thenReturn(Collections.emptyList());

        var resultado = administradorService.obtenerUsuarios();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerUsuarios_DebeRetornarListaConUsuariosYTransportistas() {
        var u1 = usuarioMock(1, "u1@test.com", 1111L);
        var t1 = transportistaMock(u1, "20-1111-0"); // Es transportista
        var u2 = usuarioMock(2, "u2@test.com", 2222L); // Usuario normal

        when(usuarioRepository.findAll()).thenReturn(List.of(u1, u2));
        when(transportistaRepository.findByUsuario(u1)).thenReturn(Optional.of(t1));
        when(transportistaRepository.findByUsuario(u2)).thenReturn(Optional.empty());

        var resultado = administradorService.obtenerUsuarios();

        assertEquals(2, resultado.size());
        assertEquals("Contratado", resultado.get(0).tipoVinculo()); // El 1ero tiene vinculo
        assertNull(resultado.get(1).tipoVinculo()); // El 2do no es transportista
    }

    // Dar Baja Usuario


    @Test
    void darBajaUsuario_DebeLanzarExcepcionSiUsuarioNoExiste() {
        when(usuarioRepository.findById(99)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, 
            () -> administradorService.darBajaUsuario(99));

        assertTrue(ex.getMessage().contains("Usuario no encontrado con ID"));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void darBajaUsuario_DebeLanzarExcepcionSiYaEstaInactivo() {
        var u = usuarioMock(1, "u@test.com", 1111L);
        u.setActivo(false); // Ya está dado de baja
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(u));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, 
            () -> administradorService.darBajaUsuario(1));

        assertEquals("El usuario ya se encuentra dado de baja", ex.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void darBajaUsuario_DebeDesactivarYGuardar() {
        var u = usuarioMock(1, "u@test.com", 1111L);
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(u));

        administradorService.darBajaUsuario(1);

        assertFalse(u.isActivo());
        verify(usuarioRepository, times(1)).save(u);
    }

    // Editar Usuario
   

    @Test
    void editarUsuario_DebeLanzarExcepcionSiEmailYaExiste() {
        var u = usuarioMock(1, "viejo@test.com", 1111L);
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(u));
    
        when(usuarioRepository.existsByEmail("nuevo@test.com")).thenReturn(true);

        var dto = new EditarUsuarioRequestDTO(null, null, null, "nuevo@test.com", null, null, null, null, null, null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, 
            () -> administradorService.editarUsuario(1, dto));

        assertEquals("El Email ya se encuentra registrado o en uso", ex.getMessage());
    }

    @Test
    void editarUsuario_DebeLanzarExcepcionSiDniYaExiste() {
        var u = usuarioMock(1, "test@test.com", 1111L);
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(u));
        
        when(usuarioRepository.existsByDni(2222L)).thenReturn(true);

        var dto = new EditarUsuarioRequestDTO(null, null, 2222L, null, null, null, null, null, null, null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, 
            () -> administradorService.editarUsuario(1, dto));

        assertEquals("El DNI ya se encuentra registrado o en uso", ex.getMessage());
    }

    @Test
    void editarUsuario_DebeLanzarExcepcion_SiCuitTransportistaYaExiste() {
        var u = usuarioMock(1, "test@test.com", 1111L);
        var t = transportistaMock(u, "20-VIEJO-0");
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(u));
        when(usuarioRepository.save(any())).thenReturn(u); // Pasa la validacion de usuario
        when(transportistaRepository.findByUsuario(u)).thenReturn(Optional.of(t));
        
        when(transportistaRepository.existsByCuit("20-NUEVO-0")).thenReturn(true);

        var dto = new EditarUsuarioRequestDTO(null, null, null, null, null, null, null, "20-NUEVO-0", null, null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, 
            () -> administradorService.editarUsuario(1, dto));

        assertEquals("El CUIT ingresado ya esta registrado o en uso", ex.getMessage());
    }

    @Test
    void editarUsuario_DebeActualizarUsuarioNormalExitosamente() {
        var u = usuarioMock(1, "viejo@test.com", 1111L);
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(u));
        when(usuarioRepository.existsByEmail("nuevo@test.com")).thenReturn(false);
        when(usuarioRepository.existsByDni(2222L)).thenReturn(false);
        
        var nuevoRol = new Rol(); nuevoRol.setNombre("ADMIN");
        when(rolRepository.findById(99)).thenReturn(Optional.of(nuevoRol));
        
        var nuevoLugar = new LugarOperativo(); nuevoLugar.setNombre("Planta Nueva");
        when(lugarOperativoRepository.findById(88)).thenReturn(Optional.of(nuevoLugar));

        when(usuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(transportistaRepository.findByUsuario(u)).thenReturn(Optional.empty()); // No es transportista

        var dto = new EditarUsuarioRequestDTO("nvoNombre", "NvoNombre", 2222L, "nuevo@test.com", 99, 88, false, null, null, null);

        var resultado = administradorService.editarUsuario(1, dto);

        assertEquals("nuevo@test.com", resultado.email());
        assertEquals(2222L, resultado.dni());
        assertEquals("nvoNombre", resultado.nombre());
        assertEquals("ADMIN", resultado.rol());
        assertEquals(false, resultado.activo());
        verify(usuarioRepository, times(1)).save(u);
    }

    @Test
    void editarUsuario_DebeActualizarUsuarioYTransportistaExitosamente() {
        var u = usuarioMock(1, "test@test.com", 1111L);
        var t = transportistaMock(u, "20-111-0");
        
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(u));
        when(usuarioRepository.save(any())).thenReturn(u);
        when(transportistaRepository.findByUsuario(u)).thenReturn(Optional.of(t));
        when(transportistaRepository.existsByCuit("20-222-0")).thenReturn(false);

        var nuevoVinculo = new TipoVinculo(); nuevoVinculo.setNombre("Tercerizado");
        when(tipoVinculoRepository.findById(77)).thenReturn(Optional.of(nuevoVinculo));

        var nuevaEmpresa = new EmpresaTercerizada(); nuevaEmpresa.setNombreFantasia("Nueva Empresa");
        when(empresaTercerizadaRepository.findById(66)).thenReturn(Optional.of(nuevaEmpresa));

        // Solo editamos datos del transportista
        var dto = new EditarUsuarioRequestDTO(null, null, null, null, null, null, null, "20-222-0", 77, 66);

        var resultado = administradorService.editarUsuario(1, dto);

        assertEquals("20-222-0", t.getCuit());
        assertEquals("Nueva Empresa", resultado.empresa());
        assertEquals("Tercerizado", resultado.tipoVinculo());
        verify(transportistaRepository, times(1)).save(t);
    }
}
