package com.blackmesaresearch.hytrac.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.blackmesaresearch.hytrac.model.core.Ruta;

@Repository
public interface RutaRepository extends JpaRepository<Ruta, Integer> {

    @Query("""
            select r
            from Ruta r
            where r.origen.id = :origenId
                and r.destino.id = :destinoId
            """)
    Optional<Ruta> findByOrigenIdAndDestinoId(
            @Param("origenId") Integer origenId,
            @Param("destinoId") Integer destinoId
    );
}
