package com.blackmesaresearch.hytrac.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blackmesaresearch.hytrac.model.core.Vehiculo;


@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Integer> {

    Optional<Vehiculo> findByPatente(String patente);

    boolean existsByPatente(String patente);


}