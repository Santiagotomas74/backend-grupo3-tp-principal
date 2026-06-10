package com.blackmesaresearch.hytrac.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blackmesaresearch.hytrac.model.stats.StatsLugar;

public interface StatsLugarRepository extends JpaRepository<StatsLugar, Long> {

  Optional<StatsLugar> findByLugarId(Integer lugarId);
  
}
