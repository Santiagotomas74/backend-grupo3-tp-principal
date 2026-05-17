package com.blackmesaresearch.hytrac.repository;

import com.blackmesaresearch.hytrac.model.lookup.TipoVinculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoVinculoRepository extends JpaRepository<TipoVinculo, Integer> {
}
