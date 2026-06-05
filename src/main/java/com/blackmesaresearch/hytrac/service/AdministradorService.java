package com.blackmesaresearch.hytrac.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.blackmesaresearch.hytrac.dto.request.EditarUsuarioRequestDTO;
import com.blackmesaresearch.hytrac.dto.response.UsuarioAdminResponseDTO;
import com.blackmesaresearch.hytrac.model.core.Transportista;
import com.blackmesaresearch.hytrac.model.core.Usuario;
import com.blackmesaresearch.hytrac.repository.EmpresaTercerizadaRepository;
import com.blackmesaresearch.hytrac.repository.LugarOperativoRepository;
import com.blackmesaresearch.hytrac.repository.RolRepository;
import com.blackmesaresearch.hytrac.repository.TipoVinculoRepository;
import com.blackmesaresearch.hytrac.repository.TransportistaRepository;
import com.blackmesaresearch.hytrac.repository.UsuarioRepository;

import jakarta.transaction.Transactional;


@Service
public class AdministradorService {

    private final UsuarioRepository usuarioRepository;
    private final TransportistaRepository transportistaRepository;
    private final RolRepository rolRepository;
    private final LugarOperativoRepository lugarOperativoRepository;
    private final TipoVinculoRepository tipoVinculoRepository;
    private final EmpresaTercerizadaRepository empresaTercerizadaRepository;

    public AdministradorService(
            UsuarioRepository usuarioRepository,
            TransportistaRepository transportistaRepository,
        RolRepository rolRepository,
        LugarOperativoRepository lugarOperativoRepository,
        TipoVinculoRepository tipoVinculoRepository,
        EmpresaTercerizadaRepository empresaTercerizadaRepository) {

        this.usuarioRepository = usuarioRepository;
        this.transportistaRepository = transportistaRepository;
        this.rolRepository = rolRepository;
        this.lugarOperativoRepository = lugarOperativoRepository;
        this.tipoVinculoRepository = tipoVinculoRepository;
        this.empresaTercerizadaRepository = empresaTercerizadaRepository;
    }

    public List<UsuarioAdminResponseDTO> obtenerUsuarios() {

        return usuarioRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public UsuarioAdminResponseDTO editarUsuario(Integer id, EditarUsuarioRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID" + id));

        if(dto.email() != null && !usuario.getEmail().equalsIgnoreCase(dto.email())) {
                if(usuarioRepository.existsByEmail(dto.email())) {
                        throw new IllegalArgumentException("El Email ya se encuentra registrado o en uso");
                }
                usuario.setEmail(dto.email());
        }

        if(dto.dni() != null && !usuario.getDni().equals(dto.dni())) {
                if(usuarioRepository.existsByDni(dto.dni())) {
                        throw new IllegalArgumentException("El DNI ya se encuentra registrado o en uso");
                }
                usuario.setDni(dto.dni());
        }

        if (dto.nombre() != null) usuario.setNombre(dto.nombre());
        if (dto.apellido() != null) usuario.setApellido(dto.apellido());

        if(dto.rolId() != null) {
                var rol = rolRepository.findById(dto.rolId())
                        .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));
                usuario.setRol(rol);
        }

        if(dto.lugarOperativoId() != null) {
                var lugarOperativo = lugarOperativoRepository.findById(dto.lugarOperativoId())
                        .orElseThrow(() -> new IllegalArgumentException("Lugar operativo no encontrado"));
                usuario.setLugarOperativo(lugarOperativo);
        }

        if(dto.activo() != null) {
                usuario.setActivo(dto.activo());
        }

        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        // Validar y Actualizar Transportista si aplica

        transportistaRepository.findByUsuario(usuarioActualizado).ifPresent(transportista -> {

                if (dto.cuit() != null && !transportista.getCuit().equalsIgnoreCase(dto.cuit())) {
                        if(transportistaRepository.existsByCuit(dto.cuit())) {
                                throw new IllegalArgumentException("El CUIT ingresado ya esta registrado o en uso");
                        }
                        transportista.setCuit(dto.cuit());
                }

                if(dto.tipoVinculoId() != null) {
                        var tipoVinculo = tipoVinculoRepository.findById(dto.tipoVinculoId())
                                .orElseThrow(() -> new IllegalArgumentException("Tipo de vinculo no encontrado"));
                        transportista.setTipoVinculo(tipoVinculo);
                }

                if (dto.empresaId() != null) {
                var empresa = empresaTercerizadaRepository.findById(dto.empresaId())
                        .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada."));
                transportista.setEmpresa(empresa);
                }

                transportistaRepository.save(transportista);
        });


        return toDTO(usuarioActualizado);
    }

    @Transactional
    public void darBajaUsuario(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));

        if (!usuario.isActivo()) {
                throw new IllegalArgumentException("El usuario ya se encuentra dado de baja");
        }

        usuario.setActivo(false);
        usuarioRepository.save(usuario);
        }

    private UsuarioAdminResponseDTO toDTO(
            Usuario usuario) {

        Transportista transportista =

                transportistaRepository
                        .findByUsuario(usuario)
                        .orElse(null);

        return new UsuarioAdminResponseDTO(

                usuario.getId(),

                usuario.getNombre(),

                usuario.getApellido(),

                usuario.getDni(),

                usuario.getEmail(),

                usuario.getLegajo(),

                usuario.getRol() != null
                        ? usuario.getRol().getNombre()
                        : null,

                usuario.getLugarOperativo() != null
                        ? usuario.getLugarOperativo().getNombre()
                        : null,

                usuario.isActivo(),

                transportista != null
                                && transportista.getTipoVinculo() != null
                                                ? transportista.getTipoVinculo().getNombre()
                                                : null,

                transportista != null
                                ? transportista.getCuit()
                                : null,

                transportista != null
                                && transportista.getEmpresa() != null
                                                ? transportista.getEmpresa().getNombreFantasia()
                                                : null);
    }
}