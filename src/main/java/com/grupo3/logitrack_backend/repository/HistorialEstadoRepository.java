package com.grupo3.logitrack_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grupo3.logitrack_backend.model.HistorialEstado;

import java.util.List;

public interface HistorialEstadoRepository extends JpaRepository<HistorialEstado, Long> {

  List<HistorialEstado> findByEnvioIdOrderByFechaCambioDesc(Long envioId);

}