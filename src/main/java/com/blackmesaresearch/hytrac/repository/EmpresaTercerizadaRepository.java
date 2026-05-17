package com.blackmesaresearch.hytrac.repository;

import com.blackmesaresearch.hytrac.model.core.EmpresaTercerizada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpresaTercerizadaRepository extends JpaRepository<EmpresaTercerizada, Integer> {

}
