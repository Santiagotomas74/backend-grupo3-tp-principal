package com.blackmesaresearch.hytrac.model.lookup;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "Estado_Orden_Carga")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor

public class EstadoOrdenCarga {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String nombre;
}