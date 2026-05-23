package com.blackmesaresearch.hytrac.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.blackmesaresearch.hytrac.dto.response.LocalidadResponseDTO;
import com.blackmesaresearch.hytrac.model.reference.Localidad;
import com.blackmesaresearch.hytrac.repository.LocalidadRepository;

@Service
public class LocalidadService {

    private final LocalidadRepository localidadRepository;

    public LocalidadService(LocalidadRepository localidadRepository) {
        this.localidadRepository = localidadRepository;
    }

    public List<LocalidadResponseDTO> obtenerTodas() {
        return localidadRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<LocalidadResponseDTO> obtenerPorProvincia(Integer provinciaId) {
        return localidadRepository.findByProvinciaId(provinciaId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private LocalidadResponseDTO toResponseDTO(Localidad localidad) {
        return new LocalidadResponseDTO(
                localidad.getId(),
                localidad.getNombre(),
                localidad.getCodigoPostal(),
                localidad.getProvincia().getId(),
                localidad.getProvincia().getNombre());
    }
}