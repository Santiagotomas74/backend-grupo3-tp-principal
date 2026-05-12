package com.blackmesaresearch.hytrac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blackmesaresearch.hytrac.model.reference.Localidad;

public interface LocalidadRepository extends JpaRepository<Localidad, Integer> {

    List<Localidad> findByProvinciaId(Integer provinciaId);
}