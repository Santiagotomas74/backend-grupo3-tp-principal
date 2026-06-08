package com.blackmesaresearch.hytrac.model.core;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Stats_Sistema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatsSistema {
    
  @Id // realmente no hace falta el id, pero Hibernate lo demanda
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "total_ordenes")
  private Integer totalOrdenes;

  @Column(name = "largas")
  private Integer largas;

  @Column(name = "largas_exitosas")
  private Integer largasExitosas;

  @Column(name = "medias")
  private Integer medias;

  @Column(name = "medias_exitosas")
  private Integer mediasExitosas;

  @Column(name = "cortas")
  private Integer cortas;

  @Column(name = "cortas_exitosas")
  private Integer cortasExitosas;

  @Column(name = "pesadas")
  private Integer pesadas;

  @Column(name = "pesadas_exitosas")
  private Integer pesadasExitosas;

  @Column(name = "livianas")
  private Integer livianas;

  @Column(name = "livianas_exitosas")
  private Integer livianasExitosas;


}
