package com.blackmesaresearch.hytrac.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blackmesaresearch.hytrac.model.core.OrdenCarga;
import com.blackmesaresearch.hytrac.repository.OrdenCargaRepository;

@Service
public class OrdenCargaService {

    @Autowired
    private OrdenCargaRepository ordenCargaRepository;

    public List<OrdenCarga> obtenerTodas() {
        return ordenCargaRepository.findAll();
    }
}