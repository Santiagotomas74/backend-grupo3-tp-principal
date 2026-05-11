package com.blackmesaresearch.hytrac.repository;

import com.blackmesaresearch.hytrac.model.reference.Combustible;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CombustibleRepository extends JpaRepository<Combustible, Integer> {

}