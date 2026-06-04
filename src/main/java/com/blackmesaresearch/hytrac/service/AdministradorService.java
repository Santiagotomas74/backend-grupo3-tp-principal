package com.blackmesaresearch.hytrac.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.blackmesaresearch.hytrac.dto.response.UsuarioAdminResponseDTO;
import com.blackmesaresearch.hytrac.model.core.Transportista;
import com.blackmesaresearch.hytrac.model.core.Usuario;
import com.blackmesaresearch.hytrac.repository.TransportistaRepository;
import com.blackmesaresearch.hytrac.repository.UsuarioRepository;

@Service
public class AdministradorService {

    private final UsuarioRepository usuarioRepository;

    private final TransportistaRepository transportistaRepository;

    public AdministradorService(
            UsuarioRepository usuarioRepository,
            TransportistaRepository transportistaRepository) {

        this.usuarioRepository = usuarioRepository;
        this.transportistaRepository = transportistaRepository;
    }

    public List<UsuarioAdminResponseDTO> obtenerUsuarios() {

        return usuarioRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
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