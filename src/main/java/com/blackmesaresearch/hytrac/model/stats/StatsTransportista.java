package com.blackmesaresearch.hytrac.model.stats;

import com.blackmesaresearch.hytrac.model.core.Transportista;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Stats_Transportista")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatsTransportista {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "transportista_id")
  private Transportista transportista;

  @Column(name = "total_ordenes")
  private Integer totalOrdenes = 0;

  @Column(name = "largas")
  private Integer largas = 0;

  @Column(name = "largas_exitosas")
  private Integer largasExitosas = 0;

  @Column(name = "medias")
  private Integer medias = 0;

  @Column(name = "medias_exitosas")
  private Integer mediasExitosas = 0;

  @Column(name = "cortas")
  private Integer cortas = 0;

  @Column(name = "cortas_exitosas")
  private Integer cortasExitosas = 0;

  @Column(name = "pesadas")
  private Integer pesadas = 0;

  @Column(name = "pesadas_exitosas")
  private Integer pesadasExitosas = 0;

  @Column(name = "livianas")
  private Integer livianas = 0;

  @Column(name = "livianas_exitosas")
  private Integer livianasExitosas = 0;

  @Column(name = "incidencias_graves")
  private Integer incidenciasGraves = 0;

}
