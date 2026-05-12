package com.blackmesaresearch.hytrac.repository;

import com.blackmesaresearch.hytrac.model.core.LugarOperativo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LugarOperativoRepository extends JpaRepository<LugarOperativo, Integer> {

    List<LugarOperativo> findByActivoTrue();

}