package com.blackmesaresearch.hytrac.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.blackmesaresearch.hytrac.dto.response.EmpresaTercerizadaResponseDTO;
import com.blackmesaresearch.hytrac.dto.response.TipoDocumentoResponseDTO;
import com.blackmesaresearch.hytrac.dto.response.TipoVinculoResponseDTO;
import com.blackmesaresearch.hytrac.model.core.EmpresaTercerizada;
import com.blackmesaresearch.hytrac.repository.EmpresaTercerizadaRepository;
import com.blackmesaresearch.hytrac.repository.TipoDocumentoRepository;
import com.blackmesaresearch.hytrac.repository.TipoVinculoRepository;

@Service
public class EntidadService {

    private final EmpresaTercerizadaRepository empresaTercerizadaRepository;
    private final TipoVinculoRepository tipoVinculoRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;

    public EntidadService(
        EmpresaTercerizadaRepository empresaTercerizadaRepository,
        TipoVinculoRepository tipoVinculoRepository,
        TipoDocumentoRepository tipoDocumentoRepository) {
            this.empresaTercerizadaRepository = empresaTercerizadaRepository;
            this.tipoVinculoRepository = tipoVinculoRepository;
            this.tipoDocumentoRepository = tipoDocumentoRepository;
    }


    public List<EmpresaTercerizadaResponseDTO> obtenerEmpresas() {
        return empresaTercerizadaRepository.findAll().stream()
            .filter(EmpresaTercerizada::isActivo)
            .map(e -> new EmpresaTercerizadaResponseDTO(
                e.getId(),
                e.getNombreFantasia(),
                e.getRazonSocial(),
                e.getCuit(),
                e.getDireccion(),
                e.getTelefono(),
                e.isActivo()))
            .toList();
    }

    public List<TipoVinculoResponseDTO> obtenerTipoVinculo() {
        return tipoVinculoRepository.findAll().stream()
            .map (tv -> new TipoVinculoResponseDTO(
                tv.getId(),
                tv.getNombre()))
            .toList();
    }

    public List<TipoDocumentoResponseDTO> obtenerTipoDocumento() {
        return tipoDocumentoRepository.findAll().stream()
                .map(td -> new TipoDocumentoResponseDTO(
                    td.getId(),
                    td.getNombre(),
                    td.getCategoria()))
                .toList();
    }

}
