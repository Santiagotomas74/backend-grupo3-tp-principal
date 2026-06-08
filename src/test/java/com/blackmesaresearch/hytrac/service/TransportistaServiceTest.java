package com.blackmesaresearch.hytrac.service;

import java.time.LocalDate;
import java.util.Collections;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.blackmesaresearch.hytrac.dto.request.AltaTransportistaRequestDTO;
import com.blackmesaresearch.hytrac.dto.request.DocumentoRequestDTO;
import com.blackmesaresearch.hytrac.model.core.Documentacion;
import com.blackmesaresearch.hytrac.model.core.EmpresaTercerizada;
import com.blackmesaresearch.hytrac.model.core.Transportista;
import com.blackmesaresearch.hytrac.model.core.Usuario;
import com.blackmesaresearch.hytrac.model.lookup.Rol;
import com.blackmesaresearch.hytrac.model.lookup.TipoDocumento;
import com.blackmesaresearch.hytrac.model.lookup.TipoVinculo;
import com.blackmesaresearch.hytrac.repository.DocumentacionRepository;
import com.blackmesaresearch.hytrac.repository.EmpresaTercerizadaRepository;
import com.blackmesaresearch.hytrac.repository.RolRepository;
import com.blackmesaresearch.hytrac.repository.TipoDocumentoRepository;
import com.blackmesaresearch.hytrac.repository.TipoVinculoRepository;
import com.blackmesaresearch.hytrac.repository.TransportistaRepository;
import com.blackmesaresearch.hytrac.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
public class TransportistaServiceTest {

    @Mock private TransportistaRepository transportistaRepository;
    @Mock private DocumentacionRepository documentacionRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private RolRepository rolRepository;
    @Mock private EmpresaTercerizadaRepository empresaTercerizadaRepository;
    @Mock private TipoVinculoRepository tipoVinculoRepository;
    @Mock private TipoDocumentoRepository tipoDocumentoRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TransportistaService transportistaService;

    private AltaTransportistaRequestDTO dtoValidoTercerizado;
    private AltaTransportistaRequestDTO dtoValidoContratado;

    // Helpers Mock

    private Usuario usuarioMock(String nombre, String legajo) {
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setApellido("Test");
        u.setLegajo(legajo);
        return u;
    }

    private TipoVinculo tipoVinculoMock(int id, String nombre) {
        TipoVinculo tv = new TipoVinculo();
        tv.setId(id);
        tv.setNombre(nombre);
        return tv;
    }

    private EmpresaTercerizada empresaMock(int id) {
        EmpresaTercerizada e = new EmpresaTercerizada();
        e.setId(id);
        e.setRazonSocial("Empresa Test S.A.");
        return e;
    }

    private TipoDocumento tipoDocumentoMock(int id) {
        TipoDocumento td = new TipoDocumento();
        td.setId(id);
        td.setNombre("Licencia Nacional");
        return td;
    }

    @BeforeEach
    void setUp() {
        dtoValidoTercerizado = new AltaTransportistaRequestDTO(
            "Juan", 
            "Perez", 
            12345678L, 
            "juan@test.com", 
            "pass123", 
            "20123456780", 
            1, 
            1, 
            List.of(new DocumentoRequestDTO(
                1, 
                "DOC-001", 
                LocalDate.now(), 
                LocalDate.now().plusYears(1), 
                "url/test", 
                null, 
                null  
            ))
        );

        dtoValidoContratado = new AltaTransportistaRequestDTO(
            "Carlos", 
            "Gomez", 
            87654321L, 
            "carlos@test.com", 
            "pass123", 
            "20876543210", 
            null, 
            2,    
            Collections.emptyList()
        );
    }

    // OBTENER TODOS


    @Test
    void obtenerTodos_DebeRetornarListaVacia() {
        when(transportistaRepository.findAll()).thenReturn(Collections.emptyList());

        var resultado = transportistaService.obtenerTodos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerTodos_DebeRetornarListaDeTransportistas() {
        Transportista t = new Transportista();
        t.setId(1);
        t.setCuit("20123456780");
        t.setUsuario(usuarioMock("Juan", "LEG-001"));
        t.setTipoVinculo(tipoVinculoMock(1, "Tercerizado"));

        when(transportistaRepository.findAll()).thenReturn(List.of(t));

        var resultado = transportistaService.obtenerTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Juan", resultado.get(0).nombre());
        assertEquals("Tercerizado", resultado.get(0).tipoVinculo());
    }

    
    // REGISTRAR NUEVO
    

    @Test
    void registrarNuevo_DebeLanzarExcepcionSiEmailExiste() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, 
            () -> transportistaService.registrarNuevoTransportista(dtoValidoTercerizado));

        assertEquals("El Email ya se encuentra registrado", ex.getMessage());
        verify(transportistaRepository, never()).save(any());
    }

    @Test
    void registrarNuevo_DebeLanzarExcepcionSiDniExiste() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioRepository.existsByDni(anyLong())).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, 
            () -> transportistaService.registrarNuevoTransportista(dtoValidoTercerizado));

        assertEquals("El DNI ya se encuentra registrado", ex.getMessage());
        verify(transportistaRepository, never()).save(any());
    }

    @Test
    void registrarNuevo_DebeLanzarExcepcionSiCuitExiste() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioRepository.existsByDni(anyLong())).thenReturn(false);
        when(transportistaRepository.existsByCuit(anyString())).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, 
            () -> transportistaService.registrarNuevoTransportista(dtoValidoTercerizado));

        assertEquals("El CUIT ya se encuentra registrado.", ex.getMessage());
    }

    @Test
    void registrarNuevo_DebeLanzarExcepcionSiRolNoExiste() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioRepository.existsByDni(anyLong())).thenReturn(false);
        when(transportistaRepository.existsByCuit(anyString())).thenReturn(false);
        when(rolRepository.findByNombre("TRANSPORTISTA")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, 
            () -> transportistaService.registrarNuevoTransportista(dtoValidoTercerizado));

        assertEquals("El Rol TRANSPORTISTA no fue encontrado en el sistema", ex.getMessage());
    }

    @Test
    void registrarNuevo_DebeLanzarExcepcionSiTipoVinculoNoExiste() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioRepository.existsByDni(anyLong())).thenReturn(false);
        when(transportistaRepository.existsByCuit(anyString())).thenReturn(false);
        when(rolRepository.findByNombre("TRANSPORTISTA")).thenReturn(Optional.of(new Rol()));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(new Usuario());
        
        when(tipoVinculoRepository.findById(1)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, 
            () -> transportistaService.registrarNuevoTransportista(dtoValidoTercerizado));

        assertTrue(ex.getMessage().contains("El Tipo de Vinculo especificado no existe"));
    }

    @Test
    void registrarNuevo_DebeLanzarExcepcionSiEsTercerizadoYFaltaEmpresa() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioRepository.existsByDni(anyLong())).thenReturn(false);
        when(transportistaRepository.existsByCuit(anyString())).thenReturn(false);
        when(rolRepository.findByNombre("TRANSPORTISTA")).thenReturn(Optional.of(new Rol()));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(new Usuario());
        when(tipoVinculoRepository.findById(1)).thenReturn(Optional.of(tipoVinculoMock(1, "Tercerizado")));

        var dtoSinEmpresa = new AltaTransportistaRequestDTO(
            "J", "P", 1L, "e@e.c", "p", "20", null, 1, Collections.emptyList()
        ); 

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, 
            () -> transportistaService.registrarNuevoTransportista(dtoSinEmpresa));

        assertEquals("Debe especificar una empresa obligatoriamente para el tipo de Vinculo Tercerizado", ex.getMessage());
    }

    @Test
    void registrarNuevo_DebeLanzarExcepcionSiEsContratadoYTieneEmpresa() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioRepository.existsByDni(anyLong())).thenReturn(false);
        when(transportistaRepository.existsByCuit(anyString())).thenReturn(false);
        when(rolRepository.findByNombre("TRANSPORTISTA")).thenReturn(Optional.of(new Rol()));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(new Usuario());
        when(tipoVinculoRepository.findById(2)).thenReturn(Optional.of(tipoVinculoMock(2, "Contratado")));

        var dtoConEmpresa = new AltaTransportistaRequestDTO(
            "C", "G", 2L, "c@c.c", "p", "20", 1, 2, Collections.emptyList()
        );

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, 
            () -> transportistaService.registrarNuevoTransportista(dtoConEmpresa));

        assertTrue(ex.getMessage().contains("no puede estar asociado a una empresa tercerizada"));
    }
   
    @Test
    void registrarNuevo_TercerizadoConDocumentosDebeGuardarExitosamente() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioRepository.existsByDni(anyLong())).thenReturn(false);
        when(transportistaRepository.existsByCuit(anyString())).thenReturn(false);
        when(rolRepository.findByNombre("TRANSPORTISTA")).thenReturn(Optional.of(new Rol()));
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPass");
        
        Usuario usuarioGuardado = usuarioMock("Juan", "LEG-001");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioGuardado);
        
        when(tipoVinculoRepository.findById(1)).thenReturn(Optional.of(tipoVinculoMock(1, "Tercerizado")));
        when(empresaTercerizadaRepository.findById(1)).thenReturn(Optional.of(empresaMock(1)));
        
        Transportista transportistaGuardado = new Transportista();
        when(transportistaRepository.save(any(Transportista.class))).thenReturn(transportistaGuardado);
        
        when(tipoDocumentoRepository.findById(1)).thenReturn(Optional.of(tipoDocumentoMock(1)));

     
        transportistaService.registrarNuevoTransportista(dtoValidoTercerizado);

      
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(transportistaRepository, times(1)).save(any(Transportista.class));
        verify(documentacionRepository, times(1)).save(any(Documentacion.class));
    }

    @Test
    void registrarNuevo_ContratadoSinDocumentosDebeGuardarExitosamente() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioRepository.existsByDni(anyLong())).thenReturn(false);
        when(transportistaRepository.existsByCuit(anyString())).thenReturn(false);
        when(rolRepository.findByNombre("TRANSPORTISTA")).thenReturn(Optional.of(new Rol()));
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPass");
        
        Usuario usuarioGuardado = usuarioMock("Carlos", "LEG-002");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioGuardado);
        
        when(tipoVinculoRepository.findById(2)).thenReturn(Optional.of(tipoVinculoMock(2, "Contratado")));
        
        Transportista transportistaGuardado = new Transportista();
        when(transportistaRepository.save(any(Transportista.class))).thenReturn(transportistaGuardado);

     
        transportistaService.registrarNuevoTransportista(dtoValidoContratado);

       
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(transportistaRepository, times(1)).save(any(Transportista.class));
        verify(empresaTercerizadaRepository, never()).findById(any()); 
        verify(documentacionRepository, never()).save(any(Documentacion.class)); 
    }
}
