package com.blackmesaresearch.hytrac.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blackmesaresearch.hytrac.dto.request.AltaTransportistaRequestDTO;
import com.blackmesaresearch.hytrac.dto.request.DocumentoRequestDTO;
import com.blackmesaresearch.hytrac.dto.response.DocumentoResponseDTO;
import com.blackmesaresearch.hytrac.dto.response.TransportistaResponseDTO;
import com.blackmesaresearch.hytrac.model.core.Documentacion;
import com.blackmesaresearch.hytrac.model.core.Transportista;
import com.blackmesaresearch.hytrac.model.core.Usuario;
import com.blackmesaresearch.hytrac.model.lookup.Rol;
import com.blackmesaresearch.hytrac.repository.DocumentacionRepository;
import com.blackmesaresearch.hytrac.repository.EmpresaTercerizadaRepository;
import com.blackmesaresearch.hytrac.repository.RolRepository;
import com.blackmesaresearch.hytrac.repository.TipoDocumentoRepository;
import com.blackmesaresearch.hytrac.repository.TipoVinculoRepository;
import com.blackmesaresearch.hytrac.repository.TransportistaRepository;
import com.blackmesaresearch.hytrac.repository.UsuarioRepository;

@Service
public class TransportistaService {

    private final TransportistaRepository transportistaRepository;
    private final DocumentacionRepository documentacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final EmpresaTercerizadaRepository empresaTercerizadaRepository;
    private final TipoVinculoRepository tipoVinculoRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;
    private final PasswordEncoder passwordEncoder;

    public TransportistaService(
            TransportistaRepository transportistaRepository,
            DocumentacionRepository documentacionRepository,
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            EmpresaTercerizadaRepository empresaTercerizadaRepository,
            TipoVinculoRepository tipoVinculoRepository,
            TipoDocumentoRepository tipoDocumentoRepository,
            PasswordEncoder passwordEncoder) {
        this.transportistaRepository = transportistaRepository;
        this.documentacionRepository = documentacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.empresaTercerizadaRepository = empresaTercerizadaRepository;
        this.tipoVinculoRepository = tipoVinculoRepository;
        this.tipoDocumentoRepository = tipoDocumentoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<TransportistaResponseDTO> obtenerTodos() {

        return transportistaRepository.findAll()
                .stream()
                .map(t -> new TransportistaResponseDTO(

                        t.getId(),

                        t.getUsuario().getNombre(),
                        t.getUsuario().getApellido(),
                        t.getCuit(),
                        t.getUsuario().getLegajo(),
                        t.getTipoVinculo().getNombre()))
                .toList();
    }

    @Transactional
    public void registrarNuevoTransportista(AltaTransportistaRequestDTO dto) {

        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("El Email ya se encuentra registrado");
        }

        if (usuarioRepository.existsByDni(dto.dni())) {
            throw new IllegalArgumentException("El DNI ya se encuentra registrado");
        }

        if (transportistaRepository.existsByCuit(dto.cuit())) {
                throw new IllegalArgumentException("El CUIT ya se encuentra registrado.");
        }

        Rol rolTransportista = rolRepository.findByNombre("TRANSPORTISTA")
                .orElseThrow(() -> new IllegalArgumentException("El Rol TRANSPORTISTA no fue encontrado en el sistema"));

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(dto.nombre());
        nuevoUsuario.setApellido(dto.apellido());
        nuevoUsuario.setDni(dto.dni());
        nuevoUsuario.setEmail(dto.email());
        nuevoUsuario.setLegajo(generarNuevoLegajo());
        nuevoUsuario.setRol(rolTransportista);
        nuevoUsuario.setPasswordHash(passwordEncoder.encode(dto.passwordTemporal()));
        nuevoUsuario.setActivo(true);

        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        var tipoVinculo = tipoVinculoRepository.findById(dto.tipoVinculoId())
                .orElseThrow(() -> new IllegalArgumentException("El Tipo de Vinculo especificado no existe: " + dto.tipoVinculoId()));

        String nombreVinculo = tipoVinculo.getNombre();

        // Si es tercerizado la empresa es obligatoria. Sino no puede estar vinculado a una empresa.  
        if(nombreVinculo.equalsIgnoreCase("Tercerizado")) {
                if (dto.empresaId() == null) {
                        throw new IllegalArgumentException("Debe especificar una empresa obligatoriamente para el tipo de Vinculo Tercerizado");
                }
        } else {
                if (dto.empresaId() != null)
                        throw new IllegalArgumentException("Un Transportista con vinculo '" + nombreVinculo + "' no puede estar asociado a una empresa tercerizada");
        }

        // buscamos la empresa
        var empresa = dto.empresaId() != null
                ? empresaTercerizadaRepository.findById(dto.empresaId())
                        .orElseThrow(() -> new IllegalArgumentException("La Empresa Tercerizada especificada no existe: " + dto.empresaId()))
                : null;
        
        Transportista nuevoTransportista = new Transportista();
        nuevoTransportista.setUsuario(usuarioGuardado);
        nuevoTransportista.setCuit(dto.cuit());
        nuevoTransportista.setEmpresa(empresa);
        nuevoTransportista.setTipoVinculo(tipoVinculo);
        nuevoTransportista.setActivo(true);

        Transportista transportistaGuardado = transportistaRepository.save(nuevoTransportista);

        // Guardar Documentación Asociada //

        if (dto.documentos() != null && !dto.documentos().isEmpty()) {
            for (DocumentoRequestDTO docDto : dto.documentos()) {
                var tipoDocumento = tipoDocumentoRepository.findById(docDto.tipoDocumentoId())
                        .orElseThrow(() -> new IllegalArgumentException("El Tipo de Documento no existe: " + docDto.tipoDocumentoId()));   

            Documentacion doc = new Documentacion();
            doc.setTransportista(transportistaGuardado);
            doc.setTipoDocumento(tipoDocumento);
            doc.setNroDocumento(docDto.nroDocumento());
            doc.setFechaEmision(docDto.fechaEmision());
            doc.setFechaVencimiento(docDto.fechaVencimiento());
            doc.setArchivoUrl(docDto.archivoUrl());
            doc.setEstadoVerificacion(false); // Supervisor aprueba?


            documentacionRepository.save(doc);

            }
        }



    }

    // Obtener Documentación 

    public List<DocumentoResponseDTO> obtenerDocumentosPorTransportista(Integer transportistaId) {

        if (!transportistaRepository.existsById(transportistaId)) {
        throw new IllegalArgumentException("Transportista no encontrado.");
        }
        return documentacionRepository.findByTransportistaId(transportistaId)
            .stream()
            .map(doc -> new DocumentoResponseDTO(
                    doc.getId(),
                    doc.getTipoDocumento().getNombre(),
                    doc.getNroDocumento(),
                    doc.getFechaEmision(),
                    doc.getFechaVencimiento(),
                    doc.getArchivoUrl()
            ))
            .toList();
        }


    // AUX Generador Legajo //
        private String generarNuevoLegajo() {
                return usuarioRepository.findTopByLegajoStartingWithOrderByLegajoDesc("LEG")
                        .map(Usuario::getLegajo)
                        .filter(legajo -> legajo != null && legajo.startsWith("LEG"))
                        .map(legajo -> {
                                // Parte Númerica del legajo
                                String numeroStr = legajo.substring(3);
                                try {
                                        int numero = Integer.parseInt(numeroStr);
                                        return String.format("LEG%03d", numero + 1);
                                } catch (NumberFormatException e) {
                                        return String.format("LEG%03d", usuarioRepository.count() + 1);
                                }
                        })
                        .orElse("LEG001");
        }


}