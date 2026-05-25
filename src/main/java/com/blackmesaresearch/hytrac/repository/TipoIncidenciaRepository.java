package com.blackmesaresearch.hytrac.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blackmesaresearch.hytrac.model.lookup.TipoIncidencia;

@Repository
public interface TipoIncidenciaRepository
        extends JpaRepository<TipoIncidencia, Integer> {

    Optional<TipoIncidencia> findByNombre(
            String nombre);
}