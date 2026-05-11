package com.blackmesaresearch.hytrac.repository;

import com.blackmesaresearch.hytrac.model.core.Acoplado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AcopladoRepository extends JpaRepository<Acoplado, Integer> {

    Optional<Acoplado> findByPatente(String patente);

    boolean existsByPatente(String patente);

}
