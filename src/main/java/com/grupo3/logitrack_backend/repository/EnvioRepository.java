package com.grupo3.logitrack_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grupo3.logitrack_backend.model.Envio;
import com.grupo3.logitrack_backend.model.EstadoEnvio;

import java.util.List;
import java.util.Optional;

public interface EnvioRepository extends JpaRepository<Envio, Long> {

  Optional<Envio> findByTrackingId(String trackingId);

  List<Envio> findByEstadoEnvio(EstadoEnvio estadoEnvio);

}