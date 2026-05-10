package com.blackmesaresearch.hytrac.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Tipo_Lugar_Operativo")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TipoLugarOperativo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String nombre;
}