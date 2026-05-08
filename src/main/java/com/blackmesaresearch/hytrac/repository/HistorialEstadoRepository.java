package com.blackmesaresearch.hytrac.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blackmesaresearch.hytrac.model.HistorialEstado;

import java.util.List;

public interface HistorialEstadoRepository extends JpaRepository<HistorialEstado, Long> {

  List<HistorialEstado> findByEnvioIdOrderByFechaCambioDesc(Long envioId);

}