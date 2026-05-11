package com.blackmesaresearch.hytrac.repository;

import com.blackmesaresearch.hytrac.model.core.AuditoriaEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuditoriaEstadoRepository extends JpaRepository<AuditoriaEstado, Integer> {

    List<AuditoriaEstado> findByOrdenIdOrderByFechaCambioDesc(Integer ordenId);


}