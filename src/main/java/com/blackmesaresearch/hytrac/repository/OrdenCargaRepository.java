package com.blackmesaresearch.hytrac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blackmesaresearch.hytrac.model.core.OrdenCarga;

@Repository
public interface OrdenCargaRepository extends JpaRepository<OrdenCarga, Integer> {

}