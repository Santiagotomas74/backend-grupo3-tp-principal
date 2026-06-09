package com.blackmesaresearch.hytrac.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blackmesaresearch.hytrac.model.core.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByDni(Long dni);

    Optional<Usuario> findByLegajo(String legajo);

    boolean existsByEmail(String email);

    boolean existsByDni(Long dni);

    boolean existsByLegajo(String legajo);
    
    Optional<Usuario> findTopByLegajoStartingWithOrderByLegajoDesc(String prefix);

    @org.springframework.data.jpa.repository.Query("SELECT u FROM Usuario u WHERE u.rol.nombre = :rolNombre AND u.lugarOperativo = :lugar")
    java.util.List<Usuario> findByRolAndLugarOperativo(
    @org.springframework.data.repository.query.Param("rolNombre") String rolNombre, 
    @org.springframework.data.repository.query.Param("lugar") com.blackmesaresearch.hytrac.model.core.LugarOperativo lugar
);

}