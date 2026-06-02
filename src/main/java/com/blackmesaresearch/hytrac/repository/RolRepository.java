package com.blackmesaresearch.hytrac.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blackmesaresearch.hytrac.model.lookup.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
    // This gives you save(), findAll(), count(), and delete() for free
    Optional<Rol> findByNombre(String nombre);
}