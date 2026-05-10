package com.blackmesaresearch.hytrac.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Tipo_Vinculo")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TipoVinculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String nombre;
}