package com.blackmesaresearch.hytrac.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.blackmesaresearch.hytrac.dto.response.VehiculoResponseDTO;
import com.blackmesaresearch.hytrac.repository.VehiculoRepository;
import com.blackmesaresearch.hytrac.repository.AcopladoRepository;
import com.blackmesaresearch.hytrac.model.core.Vehiculo;
import com.blackmesaresearch.hytrac.dto.response.VehiculoResponseDTO;


@Service
public class VehiculoService {

    @Autowired private VehiculoRepository vehiculoRepository;
    @Autowired private AcopladoRepository acopladoRepository;

    public List<VehiculoResponseDTO> obtenerCamiones() {
        return vehiculoRepository.findAll()
                .stream()
                .map(v -> new VehiculoResponseDTO(v.getId(), v.getPatente()))
                .toList();
    }

    public List<VehiculoResponseDTO> obtenerAcoplados() {
        return acopladoRepository.findAll()
                .stream()
                .map(a -> new VehiculoResponseDTO(a.getId(), a.getPatente()))
                .toList();
    }
}