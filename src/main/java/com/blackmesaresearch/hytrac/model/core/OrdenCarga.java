package com.blackmesaresearch.hytrac.model.core;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.blackmesaresearch.hytrac.model.lookup.EstadoOrdenCarga;
import com.blackmesaresearch.hytrac.model.reference.Combustible;

import java.time.LocalDateTime;

@Entity
@Table(name = "Orden_Carga")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdenCarga {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "numero_remito", unique = true, nullable = false)
  private String numeroRemito;

  @Column(unique = true, nullable = false)
  private String cot;

  @ManyToOne
  @JoinColumn(name = "camion_id")
  private Vehiculo camion;

  @ManyToOne
  @JoinColumn(name = "acoplado_id")
  private Acoplado acoplado;

  @ManyToOne
  @JoinColumn(name = "transportista_id")
  private Transportista transportista;

  @ManyToOne
  @JoinColumn(name = "planta_despacho_id")
  private LugarOperativo plantaDespacho;

  @ManyToOne
  @JoinColumn(name = "estacion_destino_id")
  private LugarOperativo estacionDestino;

  @ManyToOne
  @JoinColumn(name = "operador_id")
  private Usuario operador;

  @ManyToOne
  @JoinColumn(name = "combustible_id")
  private Combustible combustible;

@ManyToOne
@JoinColumn(name = "estado_id")
private EstadoOrdenCarga estadoOrdenCarga;

  @Column(name = "fecha_salida_planta")
  private LocalDateTime fechaSalidaPlanta;

  @Column(name = "fecha_entrega_estimada")
  private LocalDateTime fechaEntregaEstimada;

  @Column(name = "fecha_entrega_real")
  private LocalDateTime fechaEntregaReal;

  @Column(name = "temperatura_carga")
  private Double temperaturaCarga;

  @Column(name = "densidad_carga")
  private Double densidadCarga;

  @Column(name = "litros_cargados")
  private Double litrosCargados;

  @Column(name = "litros_entregados")
  private Double litrosEntregados;

  @Column(name = "fie_adjunta")
  private Boolean fieAdjunta = false;

  @Column(name = "observaciones", columnDefinition = "TEXT")
  private String observaciones;

  @CreationTimestamp
  @Column(name = "fecha_creacion", updatable = false)
  private LocalDateTime fechaCreacion;

  @UpdateTimestamp
  @Column(name = "fecha_modificacion")
  private LocalDateTime fechaModificacion;
}