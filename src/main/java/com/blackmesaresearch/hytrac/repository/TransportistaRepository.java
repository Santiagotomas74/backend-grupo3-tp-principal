package com.blackmesaresearch.hytrac.repository;

import com.blackmesaresearch.hytrac.model.core.Transportista;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransportistaRepository
                extends JpaRepository<Transportista, Integer> {
}