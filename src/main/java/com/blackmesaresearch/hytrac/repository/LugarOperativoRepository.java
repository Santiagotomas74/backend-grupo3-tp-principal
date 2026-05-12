package com.blackmesaresearch.hytrac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.blackmesaresearch.hytrac.model.core.LugarOperativo;

@Repository
public interface LugarOperativoRepository extends JpaRepository<LugarOperativo, Integer> {

    List<LugarOperativo> findByActivoTrue();

        @Query("""
                        select lo
                        from LugarOperativo lo
                        join fetch lo.tipo tipo
                        left join fetch lo.localidad localidad
                        left join fetch localidad.provincia provincia
                        where lo.activo = true
                            and lower(tipo.nombre) = lower(:nombreTipo)
                        """)
        List<LugarOperativo> findActivosByTipoNombre(@Param("nombreTipo") String nombreTipo);

}