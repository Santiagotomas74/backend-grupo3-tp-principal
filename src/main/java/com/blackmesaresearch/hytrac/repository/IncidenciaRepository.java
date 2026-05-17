package com.blackmesaresearch.hytrac.repository;

import com.blackmesaresearch.hytrac.model.core.Incidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidenciaRepository extends JpaRepository<Incidencia, Integer> {
}
