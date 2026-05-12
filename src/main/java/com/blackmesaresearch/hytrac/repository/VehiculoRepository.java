package com.blackmesaresearch.hytrac.repository;

import com.blackmesaresearch.hytrac.model.core.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;



@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Integer> {

    Optional<Vehiculo> findByPatente(String patente);

    boolean existsByPatente(String patente);


}