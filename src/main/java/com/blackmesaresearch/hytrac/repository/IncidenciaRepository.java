package com.blackmesaresearch.hytrac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.blackmesaresearch.hytrac.model.core.Incidencia;

@Repository
public interface IncidenciaRepository extends JpaRepository<Incidencia, Integer> {

        @Query("SELECT COUNT(i) FROM Incidencia i WHERE i.orden.transportista.id = :transportistaId")
        long countByTransportistaId(@Param("transportistaId") Integer transportistaId);

}