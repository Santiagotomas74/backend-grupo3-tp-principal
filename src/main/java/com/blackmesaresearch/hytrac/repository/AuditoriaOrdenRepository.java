package com.blackmesaresearch.hytrac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blackmesaresearch.hytrac.model.core.AuditoriaOrden;
import java.util.List;


@Repository
public interface AuditoriaOrdenRepository
        extends JpaRepository<AuditoriaOrden, Integer> {

    List<AuditoriaOrden>
    findByOrdenNumeroRemito(
            String ordenNumeroRemito
    );
}