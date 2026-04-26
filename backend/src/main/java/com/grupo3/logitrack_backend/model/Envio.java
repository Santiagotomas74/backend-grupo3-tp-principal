package com.grupo3.logitrack_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Envio {

  // Metadatos
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id; // Automatico.
  private String trackingId; // Automatico
  private String origen;
  private String destino;
  private String creadoPor;
  private LocalDateTime fechaCreacion; // Automatico
  private String destinatarioNombre;
  private String destinatarioTelefono;
  @Enumerated(EnumType.STRING)
  private EstadoEnvio estadoEnvio;
  private String notasAdicionales;

  // Features que necesita ML
  private Integer distanciaEstimada;
  @Enumerated(EnumType.STRING)
  private TipoEnvio tipoEnvio;
  private Integer ventanaHoras;
  private Integer volumen;
  private boolean frio;
  private boolean fragil;
  private Saturacion saturacion;

  @Enumerated(EnumType.STRING)
  private Prioridad prioridadEnvio; // Automatico
}