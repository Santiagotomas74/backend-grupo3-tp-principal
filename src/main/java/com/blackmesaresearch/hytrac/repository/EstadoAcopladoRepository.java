package com.blackmesaresearch.hytrac.repository;

import com.blackmesaresearch.hytrac.model.lookup.EstadoAcoplado;
import com.blackmesaresearch.hytrac.model.lookup.EstadoVehiculo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EstadoAcopladoRepository extends JpaRepository<EstadoAcoplado, Integer> {
    Optional<EstadoAcoplado> findByNombre(String nombre);
}
