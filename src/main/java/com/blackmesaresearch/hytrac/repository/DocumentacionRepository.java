package com.blackmesaresearch.hytrac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blackmesaresearch.hytrac.model.core.Documentacion;

@Repository
public interface DocumentacionRepository extends JpaRepository<Documentacion, Integer> {
    List<Documentacion> findByTransportistaId(Integer transportistaId);
}
