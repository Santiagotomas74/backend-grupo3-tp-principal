package com.blackmesaresearch.hytrac.repository;

import com.blackmesaresearch.hytrac.model.lookup.TipoLugarOperativo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoLugarOperativoRepository extends JpaRepository<TipoLugarOperativo, Integer> {
    // No extra code needed; gives you .findAll() automatically
}