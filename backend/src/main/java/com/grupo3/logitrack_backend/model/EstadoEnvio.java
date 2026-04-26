package com.grupo3.logitrack_backend.model;

import java.util.EnumSet;
import java.util.Set;

public enum EstadoEnvio {
  PENDIENTE,
  EN_VIAJE,
  ENTREGADO,
  CANCELADO;

  private Set<EstadoEnvio> cambiosPermitidos;

  static {
    PENDIENTE.cambiosPermitidos = EnumSet.of(EN_VIAJE, CANCELADO);
    EN_VIAJE.cambiosPermitidos = EnumSet.of(PENDIENTE, CANCELADO, ENTREGADO);
    ENTREGADO.cambiosPermitidos = EnumSet.noneOf(EstadoEnvio.class);
    CANCELADO.cambiosPermitidos = EnumSet.noneOf(EstadoEnvio.class);
  }

  public boolean puedeCambiarA(EstadoEnvio siguiente) {
    return cambiosPermitidos.contains(siguiente);
  }

  public String toPrettyString() {
    return switch (this) {
      case PENDIENTE -> "Pendiente";
      case EN_VIAJE -> "En viaje";
      case ENTREGADO -> "Entregado";
      case CANCELADO -> "Cancelado";
    };
  }
}