package com.grupo3.logitrack_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Data
public class HistorialEstado {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "envio_id")
  private Envio envio;

  @Enumerated(EnumType.STRING)
  private EstadoEnvio estadoAnterior;

  @Enumerated(EnumType.STRING)
  private EstadoEnvio estadoNuevo;

  private String motivoCambio;
  private String cambiadoPor;
  private LocalDateTime fechaCambio;
}