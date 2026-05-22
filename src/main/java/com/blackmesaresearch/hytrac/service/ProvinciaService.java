package com.blackmesaresearch.hytrac.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.blackmesaresearch.hytrac.dto.response.ProvinciaResponseDTO;
import com.blackmesaresearch.hytrac.model.reference.Provincia;
import com.blackmesaresearch.hytrac.repository.ProvinciaRepository;

@Service
public class ProvinciaService {

    private final ProvinciaRepository provinciaRepository;

    public ProvinciaService(ProvinciaRepository provinciaRepository) {
        this.provinciaRepository = provinciaRepository;
    }

    public List<ProvinciaResponseDTO> obtenerTodas() {
        return provinciaRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public ProvinciaResponseDTO obtenerPorId(Integer id) {
        Provincia provincia = provinciaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Provincia no encontrada."));

        return toResponseDTO(provincia);
    }

    private ProvinciaResponseDTO toResponseDTO(Provincia provincia) {
        return new ProvinciaResponseDTO(
                provincia.getId(),
<<<<<<< HEAD
                provincia.getNombre());
=======
                provincia.getNombre()
        );
>>>>>>> 5df270d33d9df5d9be29376f60cebc17b275eae7
    }
}