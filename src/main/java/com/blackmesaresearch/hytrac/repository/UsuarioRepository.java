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

}