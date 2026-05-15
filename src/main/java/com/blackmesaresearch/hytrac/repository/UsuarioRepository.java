package com.blackmesaresearch.hytrac.repository;

import com.blackmesaresearch.hytrac.model.core.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

   Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByDni(Long dni);

    Optional<Usuario> findByLegajo(String legajo);

    boolean existsByEmail(String email);

    boolean existsByDni(Long dni);

    boolean existsByLegajo(String legajo);

}