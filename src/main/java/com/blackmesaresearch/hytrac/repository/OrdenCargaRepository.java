package com.blackmesaresearch.hytrac.repository;

import com.blackmesaresearch.hytrac.model.core.OrdenCarga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrdenCargaRepository extends JpaRepository<OrdenCarga, Integer> {
    Optional<OrdenCarga> findByNumeroRemito(String numeroRemito);
    Optional<OrdenCarga> findByCot(String cot);


    List<OrdenCarga> findByEstadoOrdenCarga_Nombre(String nombre);
}
