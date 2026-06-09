package com.blackmesaresearch.hytrac.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blackmesaresearch.hytrac.model.stats.StatsTransportista;

public interface StatsTransportistaRepository extends JpaRepository<StatsTransportista, Integer> {
  
  Optional<StatsTransportista> findByTransportistaId(Integer transportistaId);

}
