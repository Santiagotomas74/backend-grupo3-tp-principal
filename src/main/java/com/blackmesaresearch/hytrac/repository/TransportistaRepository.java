package com.blackmesaresearch.hytrac.repository;

import com.blackmesaresearch.hytrac.model.core.Transportista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TransportistaRepository extends JpaRepository<Transportista, Integer> {

    Optional<Transportista> findByCuit(String cuit);

    boolean existsByCuit(String cuit);

}
