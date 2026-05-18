package com.blackmesaresearch.hytrac.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blackmesaresearch.hytrac.model.core.Transportista;

public interface TransportistaRepository
        extends JpaRepository<Transportista, Integer> {

}