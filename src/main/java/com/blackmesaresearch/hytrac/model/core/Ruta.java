package com.blackmesaresearch.hytrac.model.core;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Ruta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ruta {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "origen_id")
  private LugarOperativo origen;

  @ManyToOne
  @JoinColumn(name = "destino_id")
  private LugarOperativo destino;

  @Column(name = "distancia_km")
  private Double distanciaKm;

  @Column(name = "tiempo_estimado_horas")
  private Double tiempoEstimadoHoras;

  @Column(name = "geometria_json", columnDefinition = "TEXT")
  private String geometriaJson;

}
