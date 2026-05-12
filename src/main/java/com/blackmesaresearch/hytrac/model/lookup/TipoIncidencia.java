package com.blackmesaresearch.hytrac.model.lookup;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Tipo_Incidencia")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TipoIncidencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String nombre;
}