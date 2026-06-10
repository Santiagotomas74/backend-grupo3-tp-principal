package com.blackmesaresearch.hytrac.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blackmesaresearch.hytrac.model.core.Transportista;
import com.blackmesaresearch.hytrac.model.core.Usuario;

public interface TransportistaRepository
        extends JpaRepository<Transportista, Integer> {

    boolean existsByCuit(String cuit);

    Optional<Transportista> findByUsuario(Usuario usuario);

    List<Transportista> findAllByActivoTrueAndDisponibleTrue();

}