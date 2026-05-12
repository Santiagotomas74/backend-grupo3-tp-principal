package com.blackmesaresearch.hytrac.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.blackmesaresearch.hytrac.dto.response.AcopladoResponseDTO;
import com.blackmesaresearch.hytrac.dto.response.VehiculoResponseDTO;
import com.blackmesaresearch.hytrac.repository.AcopladoRepository;
import com.blackmesaresearch.hytrac.repository.VehiculoRepository;

@Service
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final AcopladoRepository acopladoRepository;

    public VehiculoService(
            VehiculoRepository vehiculoRepository,
            AcopladoRepository acopladoRepository
    ) {
        this.vehiculoRepository = vehiculoRepository;
        this.acopladoRepository = acopladoRepository;
    }

    public List<VehiculoResponseDTO> obtenerCamiones() {

        return vehiculoRepository.findAll()
                .stream()
                .map(v -> new VehiculoResponseDTO(
                        v.getId(),
                        v.getPatente(),
                        v.getMarca(),
                        v.getModelo(),
                        v.getPeso_maximo_admitido()
                ))
                .toList();
    }

    public List<AcopladoResponseDTO> obtenerAcoplados() {

        return acopladoRepository.findAll()
                .stream()
                .map(a -> new AcopladoResponseDTO(
                        a.getId(),
                        a.getPatente(),
                        a.getCapacidadMaximaLitros()
                ))
                .toList();
    }
}