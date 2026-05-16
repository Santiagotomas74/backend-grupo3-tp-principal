package com.blackmesaresearch.hytrac.repository;

import com.blackmesaresearch.hytrac.model.lookup.EstadoOrdenCarga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EstadoOrdenCargaRepository extends JpaRepository<EstadoOrdenCarga, Integer> {

    Optional<EstadoOrdenCarga> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

}