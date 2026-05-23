package com.blackmesaresearch.hytrac.repository;

import com.blackmesaresearch.hytrac.model.core.Documentacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentacionRepository extends JpaRepository<Documentacion, Integer> {
}
