package com.blackmesaresearch.hytrac.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blackmesaresearch.hytrac.model.reference.Provincia;

@Repository
public interface ProvinciaRepository extends JpaRepository<Provincia, Integer> {

    Optional<Provincia> findByNombreIgnoreCase(String nombre);
}