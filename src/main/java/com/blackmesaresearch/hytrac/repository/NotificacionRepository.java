package com.blackmesaresearch.hytrac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blackmesaresearch.hytrac.model.core.Notificacion;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    
    List<Notificacion> findByLegajoReceptorOrderByFechaCreacionDesc(String legajoReceptor);
    List<Notificacion> findByLegajoReceptorAndVistoFalseOrderByFechaCreacionDesc(String legajoReceptor);

}