package com.blackmesaresearch.hytrac.service;

import com.blackmesaresearch.hytrac.dto.response.TransportistaResponseDTO;
import com.blackmesaresearch.hytrac.repository.TransportistaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransportistaService {

    private final TransportistaRepository transportistaRepository;

    public TransportistaService(
            TransportistaRepository transportistaRepository) {
        this.transportistaRepository = transportistaRepository;
    }

    public List<TransportistaResponseDTO> obtenerTodos() {

        return transportistaRepository.findAll()
                .stream()
                .map(t -> new TransportistaResponseDTO(

                        t.getId(),

                        t.getUsuario().getNombre(),
                        t.getUsuario().getApellido(),

                        t.getCuit(),

                        t.getTipoVinculo().getNombre()))
                .toList();
    }
}