package com.blackmesaresearch.hytrac.model.core;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.blackmesaresearch.hytrac.model.lookup.EstadoOrdenCarga;
import com.blackmesaresearch.hytrac.model.reference.Combustible;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

  @Column(name = "tracking_id", unique = true, nullable = false)
  private String trackingId;

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

  @Column(name = "litros_cargados")
  private Double litrosCargados;

  @Column(name = "litros_entregados")
  private Double litrosEntregados;

  @Column(name = "fie_adjunta")
  private Boolean fieAdjunta = false;

  @Column(name = "observaciones", columnDefinition = "TEXT")
  private String observaciones;

  @Column(name = "motivo_rechazo", length = 500)
  private String motivoRechazo;

  @CreationTimestamp
  @Column(name = "fecha_creacion", updatable = false)
  private LocalDateTime fechaCreacion;

  @UpdateTimestamp
  @Column(name = "fecha_modificacion")
  private LocalDateTime fechaModificacion;

  @Column(name = "confirmado")
  private Boolean confirmado = false;
  
  @Column(name = "codigo_confirmacion")
private String codigoConfirmacion;

  @ManyToOne
  @JoinColumn(name = "ruta_id")
  private Ruta ruta;


}