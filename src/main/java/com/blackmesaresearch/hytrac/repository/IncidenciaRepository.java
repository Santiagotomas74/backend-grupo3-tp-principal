package com.blackmesaresearch.hytrac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blackmesaresearch.hytrac.model.core.Incidencia;

@Repository
public interface IncidenciaRepository
        extends JpaRepository<Incidencia, Integer> {

}