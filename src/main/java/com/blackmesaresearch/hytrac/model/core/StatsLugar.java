package com.blackmesaresearch.hytrac.model.core;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Stats_Lugar")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatsLugar {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "lugar_id")
  private LugarOperativo lugar;

  @Column(name = "despachos")
  private Integer despachos;

  @Column(name = "recepciones")
  private Integer recepciones;

}
