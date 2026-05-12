package com.blackmesaresearch.hytrac.service;

import com.blackmesaresearch.hytrac.dto.response.CombustibleResponseDTO;
import com.blackmesaresearch.hytrac.repository.CombustibleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import com.blackmesaresearch.hytrac.model.reference.Combustible;


@Service
public class CombustibleService {

    @Autowired private CombustibleRepository combustibleRepository;

    public List<CombustibleResponseDTO> obtenerCombustibles() {
        return combustibleRepository.findAll().stream()
                .map(c -> new CombustibleResponseDTO(
                        c.getId(),
                        c.getNombre(),
                        c.getNumeroOnu(),
                        c.getClaseRiesgo(),
                        c.getDensidad(),
                        c.getTemperaturaReferencia()
                ))
                .toList();
    }
}