package com.blackmesaresearch.hytrac.repository;

import com.blackmesaresearch.hytrac.model.lookup.EstadoVehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoVehiculoRepository extends JpaRepository<EstadoVehiculo, Integer> {
}
